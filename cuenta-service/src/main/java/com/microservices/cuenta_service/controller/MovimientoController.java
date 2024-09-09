package com.microservices.cuenta_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.cuenta_service.dto.CuentaDto;
import com.microservices.cuenta_service.dto.CuentaEditDto;
import com.microservices.cuenta_service.dto.CuentaResponseDto;
import com.microservices.cuenta_service.dto.MensajeDto;
import com.microservices.cuenta_service.dto.MovimientoDto;
import com.microservices.cuenta_service.dto.MovimientoResponseDto;
import com.microservices.cuenta_service.service.MovimientoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

	@Autowired
	private MovimientoService movimientoService;
	public static final String MENSAJE = "mensaje";

	
	@GetMapping(value = "/{movimientoId}", produces = "application/json")
	public MovimientoDto getMovimiento(@PathVariable Long movimientoId) throws Exception{
		try {
			MovimientoDto movimiento = movimientoService.getMomivimientoDto(movimientoId); 
			return movimiento;
		} catch (Exception e) {
			System.out.println("Error inesperado: " + e.getMessage()); // Log de error
		}
		return null;
	}
	
	@GetMapping
	public List<MovimientoDto> getAllMovimientos() throws Exception{
		Map<String, Object> responseMap = new HashMap<>();
		CuentaResponseDto cuentaResponseDto;
		try {
			return this.movimientoService.listMovimientos();
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return (List<MovimientoDto>) new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "save", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveMovimiento(@RequestBody MovimientoDto movimientoDto) {
		Map<String, Object> responseMap = new HashMap<>();
		CuentaResponseDto cuentaResponseDto;
		try {
			movimientoService.saveDTO(movimientoDto);
			cuentaResponseDto = new CuentaResponseDto(true, "Exito");
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<CuentaResponseDto>(cuentaResponseDto, HttpStatus.OK);
	}
	
	@PutMapping(value = "edit/{cuentaId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> editarCuenta(@PathVariable("movimientoId") Long movimientoId,
			@RequestBody MovimientoDto movimientoDto) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		MensajeDto mensajeDto = null;
		try {
			movimientoService.updateMovimiento(movimientoId, movimientoDto);
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<MensajeDto>(mensajeDto, HttpStatus.OK);
	}

	@DeleteMapping(value = "delete/{movimientoId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> borrarMovimiento(@PathVariable("movimientoId") Long movimientoId) {
		Map<String, Object> responseMap = new HashMap<>();
		MensajeDto mensajeDto;

		try {
			movimientoService.deleteMovimiento(movimientoId);
			mensajeDto = new MensajeDto(true, "Registro eliminado");
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<MensajeDto>(mensajeDto, HttpStatus.OK);
	}
	
	@GetMapping(name="/reporteMovimiento", produces = "application/json")
	public List<MovimientoResponseDto> getReporte() throws Exception{
		Map<String, Object> responseMap = new HashMap<>();
		CuentaResponseDto cuentaResponseDto;
		try {
			return this.movimientoService.getReportePorUsuario();
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return (List<MovimientoResponseDto>) new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
	}
}
