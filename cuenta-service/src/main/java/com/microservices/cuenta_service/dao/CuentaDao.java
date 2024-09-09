package com.microservices.cuenta_service.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.cuenta_service.mode.entities.Cuenta;

@Repository
public interface CuentaDao extends JpaRepository<Cuenta, Long>{

	Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
	
    Optional<Cuenta> findFirstByClienteId(Long clienteId);
}
