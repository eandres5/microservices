package com.microservices.cliente_service.serviceImp;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.microservices.cliente_service.dao.ClienteDao;
import com.microservices.cliente_service.dao.PersonaDao;
import com.microservices.cliente_service.dto.ClienteDto;
import com.microservices.cliente_service.dto.ClienteEditDto;
import com.microservices.cliente_service.model.entities.Cliente;
import com.microservices.cliente_service.model.entities.Persona;
import com.microservices.cliente_service.service.ClienteService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImp implements ClienteService {

	@Autowired
	ClienteDao clienteDao;
	@Autowired
	PersonaDao personaDao;

	@Override
	public void save(Cliente cliente) throws Exception {
		try {
			clienteDao.save(cliente);
		} catch (Exception e) {
			throw new Exception("error: " + e.getMessage());
		}
	}

	@Async("asyncExecutorCliente")
	@Override
	public CompletableFuture<List<ClienteDto>> listClientes() throws Exception {
		var cuentas = clienteDao.findAll();
		List<ClienteDto> listaCuentas = cuentas.stream().map(this::mapToClienteDto).toList();
		return CompletableFuture.completedFuture(listaCuentas);
	}

	@Async("asyncExecutorCliente")
	@Override
	public CompletableFuture<Void> updateCliente(Long clienteId, ClienteEditDto clienteDto) {
		return CompletableFuture.runAsync(() -> {
			try {
				// Obtener el cliente de manera sincrónica
				Cliente cliente = getCliente(clienteId);
				if (cliente == null) {
					throw new RuntimeException("Cliente no encontrado");
				}

				// Actualizar datos del cliente
				cliente.setContrasenia(clienteDto.getContrasena());
				cliente.setEstado(true);
				clienteDao.save(cliente);

				// Actualizar datos de la persona asociada al cliente
				Persona persona = cliente.getPersona();
				if (persona == null) {
					throw new RuntimeException("Persona no encontrada");
				}

				persona.setNombre(clienteDto.getNombre().trim());
				persona.setGenero(clienteDto.getGenero().trim());
				persona.setEdad(Integer.parseInt(clienteDto.getEdad()));
				persona.setDireccion(clienteDto.getDireccion().trim());
				persona.setTelefono(clienteDto.getTelefono().trim());
				personaDao.save(persona);
			} catch (Exception e) {
				// Manejar la excepción lanzando una RuntimeException
				throw new RuntimeException("Error al actualizar cliente: " + e.getMessage(), e);
			}
		});
	}

	@Transactional
	@Async("asyncExecutorCliente")
	@Override
	public CompletableFuture<ClienteDto> getClienteDto(Long clienteId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Cliente cliente = getCliente(clienteId);
				if (cliente.getPersona() == null) {
					throw new RuntimeException("La información del cliente está incompleta.");
				}
				return ClienteDto.builder().clienteId(String.valueOf(cliente.getClienteId()))
						.nombre(cliente.getPersona().getNombre()).genero(cliente.getPersona().getGenero())
						.identificacion(cliente.getPersona().getIdentificacion())
						.direccion(cliente.getPersona().getDireccion()).telefono(cliente.getPersona().getTelefono())
						.edad(cliente.getPersona().getEdad() + "").build();
			} catch (Exception e) {
				// Maneja la excepción según sea necesario
				throw new RuntimeException("Error al obtener el cliente", e);
			}
		});
	}

	public Cliente getCliente(Long clienteId) throws Exception {
		return clienteDao.findById(clienteId).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
	}

	@Async("asyncExecutorCliente")
	@Override
	public CompletableFuture<Void> saveDTO(ClienteDto dto) {
		return CompletableFuture.runAsync(() -> {
			try {
				// Crear y guardar la entidad Persona
				Persona persona = new Persona();
				persona.setNombre(dto.getNombre().trim());
				persona.setGenero(dto.getGenero().trim());
				persona.setEdad(Integer.parseInt(dto.getEdad()));
				persona.setIdentificacion(dto.getIdentificacion().trim());
				persona.setDireccion(dto.getDireccion().trim());
				persona.setTelefono(dto.getTelefono().trim());
				personaDao.save(persona);

				// Crear y guardar la entidad Cliente
				Cliente cliente = new Cliente();
				cliente.setContrasenia(dto.getContrasena().trim());
				cliente.setEstado(true);
				cliente.setPersona(persona);
				clienteDao.save(cliente);
			} catch (Exception e) {
				// Manejar la excepción lanzando una RuntimeException
				throw new RuntimeException("Error al guardar DTO: " + e.getMessage(), e);
			}
		});
	}

	private ClienteDto mapToClienteDto(Cliente cliente) {
		return ClienteDto.builder().clienteId(cliente.getClienteId() + "").nombre(cliente.getPersona().getNombre())
				.genero(cliente.getPersona().getGenero()).edad(cliente.getPersona().getEdad() + "")
				.identificacion(cliente.getPersona().getIdentificacion()).direccion(cliente.getPersona().getDireccion())
				.telefono(cliente.getPersona().getTelefono()).contrasena(cliente.getContrasenia())
				.estado(cliente.getEstado() ? "true" : "false").build();
	}

	@Async("asyncExecutorCliente")
	@Override
	public CompletableFuture<Void> deleteCliente(Long clienteId) {
		return CompletableFuture.runAsync(() -> {
			try {
				Cliente cliente = getCliente(clienteId); // Asegúrate de que getCliente() no sea bloqueante
				clienteDao.delete(cliente);

				Persona persona = cliente.getPersona();
				personaDao.delete(persona);
			} catch (Exception e) {
				throw new RuntimeException("Error al eliminar cliente: " + e.getMessage(), e);
			}
		});
	}

	@Async("asyncExecutorCliente")
    @Override
    public CompletableFuture<Boolean> isCliente(Long clienteId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getCliente(clienteId) != null;
            } catch (Exception e) {
                // Maneja la excepción según sea necesario
                e.printStackTrace();
                return false;
            }
        });
    }
}
