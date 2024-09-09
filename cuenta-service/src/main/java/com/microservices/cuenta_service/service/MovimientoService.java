package com.microservices.cuenta_service.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microservices.cuenta_service.dto.MovimientoDto;

public interface MovimientoService {
	
	CompletableFuture<List<MovimientoDto>> listMovimientos() throws Exception;

	CompletableFuture<Void> saveDTO(MovimientoDto dto) throws Exception;

	CompletableFuture<Void> deleteMovimiento(Long id) throws Exception;
	
	CompletableFuture<Void> updateMovimiento(Long id, MovimientoDto movimientoDto) throws Exception;

	CompletableFuture<MovimientoDto> getMovimientoDto(Long id);
	
}