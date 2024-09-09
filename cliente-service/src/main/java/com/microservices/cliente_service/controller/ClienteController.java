package com.microservices.cliente_service.controller;

import java.util.List;
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

import com.microservices.cliente_service.dto.ClienteDto;
import com.microservices.cliente_service.dto.ClienteEditDto;
import com.microservices.cliente_service.dto.ClienteResponseDto;
import com.microservices.cliente_service.dto.MensajeDto;
import com.microservices.cliente_service.exception.ErrorResponse;
import com.microservices.cliente_service.service.ClienteService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	public static final String MENSAJE = "mensaje";

	@GetMapping(value = "/isCliente/{clienteId}", produces = "application/json")
	public CompletableFuture<ResponseEntity<Boolean>> getIsCliente(@PathVariable Long clienteId) {
		return clienteService.isCliente(clienteId).thenApply(isCliente -> ResponseEntity.ok(isCliente))
				.exceptionally(ex -> {
					// Maneja excepciones y retorna un error en caso de fallo
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
				});
	}

//	@GetMapping(value = "/{clienteId}", produces = "application/json")
//	public CompletableFuture<ClienteDto> getCliente(@PathVariable Long clienteId) throws Exception {
//		return clienteService.getClienteDto(clienteId).exceptionally(e -> {
//			// Manejo de excepciones
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", e);
//		});
//	}
	
	@GetMapping(value = "/{clienteId}", produces = "application/json")
	public Mono<ClienteDto> getCliente(@PathVariable Long clienteId) throws Exception {
	    return Mono.fromFuture(clienteService.getClienteDto(clienteId));
	}
	

	@GetMapping
	public CompletableFuture<ResponseEntity<List<ClienteDto>>> getAllClientes() throws Exception {
		return clienteService.listClientes().thenApply(clientes -> ResponseEntity.ok(clientes)).exceptionally(ex -> {
			// Log el error completo para obtener detalles
			ex.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse("Error al obtener cuentas", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		});
	}

	@PostMapping(value = "/save", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveCliente(@RequestBody ClienteDto clienteDto) throws Exception {
		// Llamar al método asíncrono y esperar su finalización
		try {
			CompletableFuture<Void> future = clienteService.saveDTO(clienteDto);
			future.get();
			// Crear respuesta exitosa
			ClienteResponseDto cuentaResponseDto = new ClienteResponseDto(true, "Éxito");
			return new ResponseEntity<>(cuentaResponseDto, HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			// Lanza una excepción para que el GlobalExceptionHandler la maneje
			throw new RuntimeException("Error al guardar la cuenta", e);
		}
	}

	@PutMapping(value = "edit/{clienteId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> editarCliente(@PathVariable("clienteId") Long clienteId,
			@RequestBody ClienteEditDto clienteEditDto) throws Exception {
		try {
			CompletableFuture<Void> future = clienteService.updateCliente(clienteId, clienteEditDto);
			future.get();
			// Crear respuesta exitosa
			ClienteResponseDto cuentaResponseDto = new ClienteResponseDto(true, "Éxito");
			return new ResponseEntity<>(cuentaResponseDto, HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			// Lanza una excepción para que el GlobalExceptionHandler la maneje
			throw new RuntimeException("Error al guardar la cuenta", e);
		}
	}

	@DeleteMapping(value = "delete/{clienteId}", produces = "application/json")
	@ResponseBody
	public CompletableFuture<ResponseEntity<MensajeDto>> deleteCliente(@PathVariable("clienteId") Long clienteId)
			throws Exception {
		return clienteService.deleteCliente(clienteId).thenApply(v -> {
			// Retorna una respuesta exitosa si el CompletableFuture se completa sin
			// excepciones
			MensajeDto mensajeDto = new MensajeDto(null, "Cliente eliminado correctamente");
			return new ResponseEntity<>(mensajeDto, HttpStatus.OK);
		}).exceptionally(ex -> {
			throw new RuntimeException("Error al guardar la cuenta", ex);
		});
	}
}