# banquito-switch-on-us-settlement-service

## Nota de arquitectura objetivo

Este servicio ya consume Core REST via Kong/API Manager para procesar lineas On-Us.

Operacion vigente:

```http
POST http://localhost:8000/api/v1/switch-core/payment-reservations/{reservationUuid}/consume
```

`reservationUuid` viene del fondeo global del batch. `lineId` debe mapearse como `paymentLineUuid` en la solicitud a Core. La integracion legacy Switch-Core por gRPC fue retirada.

## Responsabilidad

Servicio responsable de procesar lineas On-Us ya clasificadas por `routing-service`. Consume eventos RabbitMQ, solicita al Core Bancario el consumo/acreditacion interna por REST via Kong, registra intentos y publica el resultado final de la linea.

No expone endpoints REST de negocio. No procesa lineas Off-Us, no debita la cuenta matriz, no calcula IVA, no registra contabilidad y no calcula comisiones.

## Ejecucion local

Desde la raiz del workspace:

```powershell
docker compose build on-us-settlement-service
docker compose up -d postgres rabbitmq routing-service on-us-settlement-service
```

Compilar sin Maven global:

```powershell
docker run --rm -v maven_repo:/root/.m2 -v "${PWD}\banquito-switch-on-us-settlement-service:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn -o -DskipTests compile
```

Puerto por defecto: `8083`.

Health:

```http
GET http://localhost:8083/actuator/health
```

## Variables de entorno

- `SERVER_PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `RABBIT_EXCHANGE_ROUTING`
- `RABBIT_QUEUE_SETTLEMENT_ON_US`
- `RABBIT_ROUTING_KEY_ROUTED_ON_US`
- `RABBIT_EXCHANGE_SETTLEMENT`
- `RABBIT_QUEUE_SETTLEMENT_COMPLETED`
- `RABBIT_ROUTING_KEY_ON_US_COMPLETED`
- `RABBIT_ROUTING_KEY_LINE_REJECTED`
- `CORE_KONG_BASE_URL`
- `CORE_KONG_SWITCH_CORE_PATH`
- `CORE_KONG_PAYMENT_RESERVATIONS_PATH`
- `CORE_KONG_AUTH_TOKEN`
- `CORE_KONG_CLIENT_TOKEN_ENABLED`
- `CORE_KONG_CLIENT_ID`
- `CORE_KONG_CLIENT_SECRET`
- `CORE_KONG_REQUIRED_SCOPE`
- `CORE_KONG_CLIENT_TOKEN_PATH`
- `CORE_KONG_CLIENT_TOKEN_REFRESH_SKEW_SECONDS`
- `CORE_KONG_CONNECT_TIMEOUT_MS`
- `CORE_KONG_READ_TIMEOUT_MS`
- `CORE_SWITCH_DEFAULT_ACCOUNTING_DATE`

## Base de datos

Usa `SWITCH_LIQUIDACION_ON_US_DB`. La base se inicializa localmente con:

```text
src/main/resources/db/init/001_create_"LIQUIDACION_ON_US"_tables.sql
```

Hibernate se mantiene en `spring.jpa.hibernate.ddl-auto=validate`; no genera ni actualiza tablas.

Tablas propias:

- `"LIQUIDACION_ON_US"`
- `"INTENTO_LIQUIDACION_ON_US"`

## Eventos

Consume:

- Exchange: `rabbit.exchange.routing`
- Queue: `rabbit.queue.settlement.on-us`
- Routing key: `rabbit.routing-key.routed-on-us`
- Evento: `PaymentLineRoutedOnUsEvent`

Publica:

- Exchange: `rabbit.exchange.settlement`
- Routing key: `rabbit.routing-key.on-us-completed`
- Queue observable local: `rabbit.queue.settlement.completed`
- Evento: `OnUsSettlementCompletedEvent`

## Integracion Core

El servicio llama al Core Bancario mediante REST via Kong:

```http
POST /api/v1/switch-core/payment-reservations/{reservationUuid}/consume
```

Mapeo del request:

- `coreFundingId` legacy se usa como `{reservationUuid}` en el path.
- `lineId` se envia como `paymentLineUuid`.
- `destinationType` se envia fijo como `ON_US`.
- `routingCode` se envia fijo como `BQTO001`, codigo Core/Kong vigente para Banco BanQuito.
- `destinationAccountNumber` viene del evento.
- `beneficiaryIdentification` viene del evento.
- `beneficiaryName` viene del evento.
- `notificationEmail` se envia como `beneficiaryEmail`.
- `reference` se envia como `concept`.
- `amount` viene del evento.
- `currency` se conserva localmente, pero no se envia porque el contrato Core detectado no lo incluye.
- `correlationId` viene del evento.
- `accountingDate` usa `CORE_SWITCH_DEFAULT_ACCOUNTING_DATE`; si esta vacia, usa la fecha local del servicio.

El endpoint devuelve `ReservationResponse`. Core no devuelve actualmente `transactionUuid` de acreditacion, por lo que `coreTransactionId` queda `null` salvo que el contrato Core se amplie.

Autenticacion transicional:

```text
CORE_KONG_AUTH_TOKEN
CORE_KONG_CLIENT_TOKEN_ENABLED
CORE_KONG_CLIENT_ID
CORE_KONG_CLIENT_SECRET
CORE_KONG_REQUIRED_SCOPE
CORE_KONG_CLIENT_TOKEN_PATH
CORE_KONG_CLIENT_TOKEN_REFRESH_SKEW_SECONDS
```

Si `CORE_KONG_AUTH_TOKEN` esta configurado, se usa como override manual. Si esta vacio y `CORE_KONG_CLIENT_TOKEN_ENABLED=true`, el servicio obtiene `client-token` por `POST /api/v1/auth/client-token`, lo cachea en memoria y lo renueva antes de expirar.

## Idempotencia

La idempotencia local se controla por `lineId` en `"LIQUIDACION_ON_US"`. Si una linea ya esta en estado final (`ACREDITADA_ON_US`, `RECHAZADA` o `FALLIDA`), el servicio hace ack del mensaje y no vuelve a llamar al Core ni vuelve a publicar resultado. `lineId` tambien se envia como `paymentLineUuid` para aprovechar la idempotencia parcial del Core.

## Estados

- `RECIBIDA`
- `ENVIADA_CORE`
- `ACREDITADA_ON_US`
- `RECHAZADA`
- `FALLIDA`

Si el Core devuelve `ACTIVA`, `APPROVED`, `CONSUMIDA`, `CONSUMIDA_PARCIAL` o `CONSUMIDA_TOTAL`, la linea queda `ACREDITADA_ON_US` y `billable=true`. En la integracion REST actual, `CONSUMIDA_TOTAL` es una respuesta exitosa esperada cuando la reserva queda totalmente consumida por la linea On-Us.

Si Core devuelve rechazo funcional HTTP `400`, `404`, `409` o `422`, la linea queda `RECHAZADA` y `billable=false`.

Si ocurre error tecnico controlado contra REST/Kong, por ejemplo `401`, `403`, `500`, `503` o timeout, la linea queda `FALLIDA` y `billable=false`.

## Prueba manual basica

1. Levantar servicios necesarios:

```powershell
docker compose up -d postgres rabbitmq batch-service routing-service on-us-settlement-service
```

Para probar contra Core real por Kong, configurar al menos:

```powershell
$env:CORE_KONG_BASE_URL="http://localhost:8000"
$env:CORE_KONG_CLIENT_SECRET="<secret-tecnico>"
$env:CORE_SWITCH_DEFAULT_ACCOUNTING_DATE="2026-06-05"
```

Si el servicio corre dentro de Docker Compose junto al stack Core, usar la red externa compartida `banquito-net` y configurar `CORE_KONG_BASE_URL=http://kong-gateway:8000`. `host.docker.internal:8000` queda solo como alternativa local si no se usa la red compartida.

2. Cargar un lote mixto desde batch-service:

```powershell
$response = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/v1/batches/upload" `
  -Form @{ file = Get-Item ".\docs\examples\files\valid_mixed_batch.csv"; channel = "WEB"; receivedBy = "local" }
```

3. Verificar que On-Us consumio solo las lineas On-Us:

```powershell
docker exec banquito-switch-postgres psql -U postgres -d SWITCH_LIQUIDACION_ON_US_DB -c 'select "ESTADO", count(*) from "LIQUIDACION_ON_US" group by "ESTADO";'
```

4. Verificar intentos:

```powershell
docker exec banquito-switch-postgres psql -U postgres -d SWITCH_LIQUIDACION_ON_US_DB -c 'select "ESTADO_INTENTO", count(*) from "INTENTO_LIQUIDACION_ON_US" group by "ESTADO_INTENTO";'
```

5. Verificar eventos publicados:

```powershell
docker exec banquito-switch-rabbitmq rabbitmqctl list_queues name messages --formatter=json
```

## Evidencia de validacion con Core real

Validacion ejecutada contra Kong/Core real usando `CORE_KONG_BASE_URL=http://kong-gateway:8000`. La primera validacion uso token inyectado en `CORE_KONG_AUTH_TOKEN`; desde la fase de token automatico el servicio puede obtenerlo por `client-token` usando `CORE_KONG_CLIENT_SECRET`.

- Token tecnico obtenido por `POST /api/v1/auth/client-token` con cliente demo `switch-pagos-internos-service` y scope solicitado `core.reserve.consume`. El token emitido incluyo `core.reserve.consume`, `core.reserve.create` y `core.reserve.release`.
- Lote base previo `07c406d1-51e6-4d60-b1b0-598e52d96c4d` ya no tenia eventos frescos en la cola On-Us; su linea habia quedado `FALLIDA` por una ejecucion anterior sin token.
- Lote fresco `98141992-cb2e-4293-bb49-79b6b553bb91`, reserva `06472a1b-12b1-4d95-83e1-a87b4ae4f1c2`, alcanzo Core por `POST /payment-reservations/{reservationUuid}/consume`, pero Core respondio `409 ACCOUNTING_DATE_NOT_FOUND` para fecha contable `2026-06-07`.
- Lote fresco `bf58b89a-f1f9-4669-8ddf-7d974e495459`, reserva `9430ec47-ea0d-43a2-9864-8d9071e685a0`, alcanzo Core usando fecha contable demo `2026-06-05`, pero Core respondio `500 INTERNAL_ERROR` con detalle `InvalidDataAccessApiUsageException`.
- Luego de la correccion en Core, el lote `b14fb91d-8b42-45f1-a74b-8f8bf181ac2e`, reserva `d8b62b81-9316-44d0-8d1b-02fdfc652652`, linea `42648d62-b786-43bf-9f7b-cf407e9bab77`, cuenta destino `0010000010600`, fecha contable `2026-06-05` y monto `10.00` recibio `HTTP 200` de Core. La respuesta `CONSUMIDA_TOTAL` se registro como `ACREDITADA_ON_US`, intento `APROBADO`, `billable=true` y `coreTransactionId=null`.

Conclusion: la integracion de red, token, path, mapeo `coreFundingId -> reservationUuid` y mapeo `lineId -> paymentLineUuid` funcionan contra Core real. El caso positivo On-Us desde Switch queda validado; billing y notification pueden fallar por token propio en fases posteriores sin invalidar la acreditacion On-Us.

## Decisiones tecnicas

- El listener delega todo a `OnUsSettlementService`.
- El cliente REST de Core/Kong queda encapsulado en `CoreBankingClient`.
- `coreFundingId` sigue siendo nombre legacy/transicional y contiene el `reservationUuid`.
- `coreTransactionId` no se inventa porque Core no lo devuelve en `ReservationResponse`.
- Los eventos son DTOs propios en `dto.event`; no se publican entidades JPA.
- No se implementan DLQ ni reintentos avanzados en esta fase.
