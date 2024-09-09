package com.microservices.cuenta_service.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.cuenta_service.mode.entities.Movimiento;

@Repository
public interface MovimientoDao extends JpaRepository<Movimiento, Long>{
	
    List<Movimiento> findByCuenta_CuentaIdAndFechaBetween(Long cuentaId, LocalDateTime fechaDesde, LocalDateTime fechaHasta);

}