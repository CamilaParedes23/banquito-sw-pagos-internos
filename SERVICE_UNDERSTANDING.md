# Entendimiento del servicio

`banquito-switch-on-us-settlement-service` procesa pagos cuyo beneficiario pertenece a Banco BanQuito.

## Nota de migracion vigente

La llamada activa a Core para lineas On-Us usa REST via Kong:

```http
POST /api/v1/switch-core/payment-reservations/{reservationUuid}/consume
```

`coreFundingId` es un nombre legacy/transicional y su valor se usa como `reservationUuid`. `lineId` se envia como `paymentLineUuid`. Un rechazo posterior al fondeo no genera devolucion de dinero a la empresa; solo produce estado final de linea y reporte.

## Responsabilidad en lenguaje simple

Recibe una linea On-Us, pide al Core Bancario que acredite la cuenta destino y registra si la linea fue acreditada, rechazada o fallida.

## Flujo interno principal

1. `PaymentLineRoutedOnUsListener` consume `PaymentLineRoutedOnUsEvent`.
2. `OnUsSettlementServiceImpl` valida identificadores y revisa idempotencia por `lineId`.
3. Se registra la linea en `on_us_settlement`.
4. Se crea un registro en `on_us_settlement_attempt`.
5. `CoreBankingClient` invoca `POST /payment-reservations/{reservationUuid}/consume` por REST via Kong.
6. Se actualiza la linea segun respuesta del Core.
7. Se publica `OnUsSettlementCompletedEvent`.

## Paquetes importantes

- `client`: cliente REST hacia Core Bancario via Kong.
- `config`: configuracion RabbitMQ, RestClient Core/Kong y canal gRPC legacy.
- `dto.event`: eventos consumidos y publicados.
- `dto.request` y `dto.response`: DTOs internos y DTOs REST de integracion con Core.
- `enums`: estados locales de liquidacion e intentos.
- `listener`: listener RabbitMQ de lineas On-Us.
- `mapper`: conversion manual entre eventos, entidades y DTOs del Core.
- `model`: entidades JPA propias.
- `repository`: repositorios de tablas propias.
- `service` y `service.impl`: interfaces e implementaciones de aplicacion.

## Clases principales

- `PaymentLineRoutedOnUsListener`: recibe eventos On-Us desde RabbitMQ.
- `OnUsSettlementServiceImpl`: coordina idempotencia, persistencia, llamada al Core y publicacion de resultado.
- `CoreBankingClient`: encapsula el consumo REST de Core/Kong.
- `RabbitOnUsSettlementEventPublisher`: publica `OnUsSettlementCompletedEvent`.
- `OnUsSettlementMapper`: construye entidades, requests internos para Core/Kong y eventos de salida.

## Tablas propias

- `on_us_settlement`: snapshot de linea On-Us y resultado final.
- `on_us_settlement_attempt`: intento de llamada al Core y respuesta tecnica/funcional.

No hay foreign keys hacia otros microservicios ni hacia el Core.

## Eventos propios

Consume:

- `PaymentLineRoutedOnUsEvent`

Publica:

- `OnUsSettlementCompletedEvent`

Se publica en `switch.settlement.exchange` con routing key `payment.line.on-us.completed`.
La cola local `switch.settlement.completed.queue` queda enlazada para validar la publicacion mientras billing y reporting no consumen aun este contrato.

## Integraciones externas

Integra con:

- RabbitMQ para consumir/publicar eventos.
- PostgreSQL propio para trazabilidad.
- Core Bancario por REST via Kong para consumo/acreditacion interna On-Us.

## Que NO hace

- No expone endpoints REST de negocio.
- No procesa lineas Off-Us.
- No genera archivo de compensacion.
- No debita la cuenta matriz.
- No calcula IVA ni registra contabilidad.
- No calcula comisiones.
- No consolida el lote completo.

## Pendientes o limitaciones conocidas

- El token tecnico se obtiene automaticamente por `POST /api/v1/auth/client-token` cuando no existe override manual en `CORE_KONG_AUTH_TOKEN`.
- Core devuelve `ReservationResponse` y no incluye `transactionUuid` de acreditacion; `coreTransactionId` queda `null` mientras el contrato no se amplie.
- `coreFundingId` debe renombrarse a `reservationUuid` en eventos, tablas y DTOs en una fase posterior.
- La integracion legacy Switch-Core por gRPC fue retirada; el servicio consume Core solo por REST via Kong.
- No se implementaron DLQ ni politica avanzada de reintentos.
- El estado Core `CONSUMIDA_TOTAL` se interpreta como consumo exitoso y produce `ACREDITADA_ON_US`, `billable=true`.
- Billing depende de su propio provider de token automatico para `service-fee-charge`.
