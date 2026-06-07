package com.banquito.switchpagos.onussettlement.repository;

import com.banquito.switchpagos.onussettlement.model.OnUsSettlementAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OnUsSettlementAttemptRepository extends JpaRepository<OnUsSettlementAttempt, UUID> {

    Long countByLineId(UUID lineId);
}
