package com.microservices.cliente_service.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microservices.cliente_service.dto.ClienteDto;
import com.microservices.cliente_service.dto.ClienteEditDto;
import com.microservices.cliente_service.model.entities.Cliente;

public interface ClienteService {
	
	void save (Cliente cliente) throws Exception;
	
	CompletableFuture<Void> updateCliente (Long clienteId, ClienteEditDto clienteDto) throws Exception;
	
	CompletableFuture<List<ClienteDto>> listClientes() throws Exception;
	
	CompletableFuture<ClienteDto> getClienteDto(Long clienteId) throws Exception;
	
	CompletableFuture<Void> saveDTO(ClienteDto dto) throws Exception;
	
	CompletableFuture<Void> deleteCliente (Long clienteId) throws Exception;
	
	CompletableFuture<Boolean> isCliente(Long clienteId);
}
