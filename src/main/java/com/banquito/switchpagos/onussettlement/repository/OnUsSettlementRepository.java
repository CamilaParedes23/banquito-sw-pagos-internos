package com.banquito.switchpagos.onussettlement.repository;

import com.banquito.switchpagos.onussettlement.model.OnUsSettlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OnUsSettlementRepository extends JpaRepository<OnUsSettlement, UUID> {

    Optional<OnUsSettlement> findByLineId(UUID lineId);
}
