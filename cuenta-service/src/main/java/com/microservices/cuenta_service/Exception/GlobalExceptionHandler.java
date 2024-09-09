package com.microservices.cuenta_service.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        // Log el error completo para obtener detalles
        ex.printStackTrace();

        // Crear una respuesta de error detallada
        ErrorResponse errorResponse = new ErrorResponse("Error inesperado", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

	@ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("mensaje", e.getReason());
        responseMap.put("detalle", e.getMessage());
        return new ResponseEntity<>(responseMap, e.getStatusCode());
    }
	
}
