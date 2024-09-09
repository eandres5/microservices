package com.microservices.cuenta_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import org.springframework.web.server.ResponseStatusException;

import com.microservices.cuenta_service.Exception.ErrorResponse;
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
	public CompletableFuture<MovimientoDto> getMovimiento(@PathVariable Long movimientoId) throws Exception {
		return movimientoService.getMovimientoDto(movimientoId).exceptionally(e -> {
			// Manejo de excepciones
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", e);
		});
	}

	@GetMapping
	public CompletableFuture<ResponseEntity<List<MovimientoDto>>> getAllMovimientos() throws Exception {
		return movimientoService.listMovimientos().thenApply(movimientos -> ResponseEntity.ok(movimientos)).exceptionally(ex -> {
			// Log el error completo para obtener detalles
			ex.printStackTrace();
			// Crear una respuesta de error detallada
			ErrorResponse errorResponse = new ErrorResponse("Error al obtener movimientos", ex.getMessage());
			// Devuelve la respuesta de error como un ResponseEntity con el error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		});
	}
	
	@PostMapping(value = "/save", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> saveMovimiento(@RequestBody MovimientoDto movimientoDto) throws Exception {
        // Llamar al método asíncrono y esperar su finalización
        try {
            CompletableFuture<Void> future = movimientoService.saveDTO(movimientoDto);
            future.get();
            // Crear respuesta exitosa
            CuentaResponseDto cuentaResponseDto = new CuentaResponseDto(true, "Éxito");
            return new ResponseEntity<>(cuentaResponseDto, HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            // Lanza una excepción para que el GlobalExceptionHandler la maneje
            throw new RuntimeException("Error al guardar la cuenta", e);
        }
    }
	
	@PutMapping(value = "edit/{movimientoId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> editarMovimiento(@PathVariable("movimientoId") Long movimientoId,
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
	
}
