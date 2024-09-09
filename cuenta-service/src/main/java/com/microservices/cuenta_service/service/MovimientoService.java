package com.microservices.cuenta_service.service;

import java.util.List;

import com.microservices.cuenta_service.dto.MovimientoDto;
import com.microservices.cuenta_service.dto.MovimientoResponseDto;

public interface MovimientoService {
	
	List<MovimientoDto> listMovimientos() throws Exception;

	MovimientoDto getMomivimientoDto(Long id) throws Exception;

	void saveDTO(MovimientoDto dto) throws Exception;

	void deleteMovimiento(Long id) throws Exception;
	
	void updateMovimiento(Long id, MovimientoDto movimientoDto) throws Exception;
	
	List<MovimientoResponseDto> getReportePorUsuario() throws Exception;
}