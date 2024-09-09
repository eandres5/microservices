package com.microservices.cuenta_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microservices.cuenta_service.dto.CuentaDto;
import com.microservices.cuenta_service.dto.CuentaEditDto;
import com.microservices.cuenta_service.dto.ReporteDto;
import com.microservices.cuenta_service.mode.entities.Cuenta;


public interface CuentaService {

	CompletableFuture<List<CuentaDto>> listCuentas() throws Exception;

	CompletableFuture<CuentaDto> getCuentaDto(Long cuentaId) throws Exception;

	CompletableFuture<Void> saveDTO(CuentaDto dto) throws Exception;

	void deleteCuenta(Long cuentaId) throws Exception;
	
	void updateCuenta (Long cuentaId, CuentaEditDto cuentaDto) throws Exception;

	Cuenta getCuentaByNumeroCuenta(String numeroCuenta) throws Exception;
	
	CompletableFuture<ReporteDto> generarReporte(Long clienteId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) ;
}

