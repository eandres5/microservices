package com.microservices.cuenta_service.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.microservices.cuenta_service.Exception.ErrorResponse;
import com.microservices.cuenta_service.dto.CuentaDto;
import com.microservices.cuenta_service.dto.CuentaEditDto;
import com.microservices.cuenta_service.dto.CuentaResponseDto;
import com.microservices.cuenta_service.dto.MensajeDto;
import com.microservices.cuenta_service.service.CuentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

	@Autowired
	private CuentaService cuentaService;

	public static final String MENSAJE = "mensaje";

	@GetMapping(value = "/{cuentaId}", produces = "application/json")
	public CompletableFuture<CuentaDto> getCuenta(@PathVariable Long cuentaId) throws Exception {
		return cuentaService.getCuentaDto(cuentaId).exceptionally(e -> {
			// Manejo de excepciones
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", e);
		});
	}

	@GetMapping
	public CompletableFuture<ResponseEntity<List<CuentaDto>>> getAllCuentas() throws Exception {
		return cuentaService.listCuentas().thenApply(cuentas -> ResponseEntity.ok(cuentas)).exceptionally(ex -> {
			// Log el error completo para obtener detalles
			ex.printStackTrace();
			// Crear una respuesta de error detallada
			ErrorResponse errorResponse = new ErrorResponse("Error al obtener cuentas", ex.getMessage());
			// Devuelve la respuesta de error como un ResponseEntity con el error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		});
	}

	@PostMapping(value = "/save", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> saveCuenta(@RequestBody CuentaDto cuentaDto) throws Exception {
        // Llamar al método asíncrono y esperar su finalización
        try {
            CompletableFuture<Void> future = cuentaService.saveDTO(cuentaDto);
            future.get();
            // Crear respuesta exitosa
            CuentaResponseDto cuentaResponseDto = new CuentaResponseDto(true, "Éxito");
            return new ResponseEntity<>(cuentaResponseDto, HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            // Lanza una excepción para que el GlobalExceptionHandler la maneje
            throw new RuntimeException("Error al guardar la cuenta", e);
        }
    }

	@PutMapping(value = "edit/{cuentaId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> editarCuenta(@PathVariable("cuentaId") Long cuentaId,
			@RequestBody CuentaEditDto cuentaEditDto) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		MensajeDto mensajeDto = null;
		try {
			cuentaService.updateCuenta(cuentaId, cuentaEditDto);
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<MensajeDto>(mensajeDto, HttpStatus.OK);
	}

	@DeleteMapping(value = "delete/{cuentaId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> borrarCuenta(@PathVariable("cuentaId") Long cuentaId) {
		Map<String, Object> responseMap = new HashMap<>();
		MensajeDto mensajeDto;

		try {
			cuentaService.deleteCuenta(cuentaId);
			mensajeDto = new MensajeDto(true, "Registro eliminado");
		} catch (Exception e) {
			responseMap.put(MENSAJE, "Error");
			responseMap.put("mensaje", e.toString());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<MensajeDto>(mensajeDto, HttpStatus.OK);
	}
	
	@GetMapping("/reporte")
	public CompletableFuture<ResponseEntity<? extends Object>> getReporteByCuenta(
	        @RequestParam Long clienteId,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaDesde,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaHasta) {

	    return cuentaService.generarReporte(clienteId, fechaDesde, fechaHasta)
	        .thenApply(reporte -> {
	            if (reporte != null) {
	                return ResponseEntity.ok(reporte);
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	            }
	        })
	        .exceptionally(ex -> {
	            // Log the error
	            ex.printStackTrace();
	            // Return a 500 error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        });
	}
	
}
