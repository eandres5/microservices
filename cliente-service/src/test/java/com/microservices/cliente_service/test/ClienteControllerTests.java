package com.microservices.cliente_service.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.microservices.cliente_service.dto.ClienteDto;
import com.microservices.cliente_service.dto.ClienteResponseDto;
import com.microservices.cliente_service.model.entities.Cliente;
import com.microservices.cliente_service.service.ClienteService;

public class ClienteControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private ClienteService clienteService;

    @Test
    public void testGetIsCliente() throws Exception {
        Long clienteId = 1L;
//        boolean expectedResult = true; 
//
//        when(clienteService.isCliente(clienteId)).thenReturn(expectedResult);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/isCliente/{clienteId}", clienteId)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(expectedResult)));
    }
    
    @Test
    public void testGetAllClientes() throws Exception {
//        ClienteDto cliente1 = new ClienteDto(); 
//        ClienteDto cliente2 = new ClienteDto();
//        List<ClienteDto> clientes = Arrays.asList(cliente1, cliente2);
//
//        when(clienteService.listClientes()).thenReturn(clientes);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/clientes")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }
    
    private static final String API_URL = "/clientes/save";

    @Test
    public void testSaveClienteSuccess() throws Exception {
        
//        ClienteDto clienteDto = new ClienteDto(); 
//        Cliente clienteResponseDto = new Cliente();
//
//        when(clienteService.saveDTO(clienteDto)).thenReturn(clienteResponseDto);
//
//       
//        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"nombre\":\"Juan\", \"identificacion\":\"12345\", \"direccion\":\"Calle Falsa\", \"telefono\":\"555-1234\"}")) // Ajusta el JSON de ejemplo según tu DTO
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.exito").value(true))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.mensaje").value("Exito"));
    }

    @Test
    public void testSaveClienteFailure() throws Exception {
        ClienteDto clienteDto = new ClienteDto();

        doThrow(new RuntimeException("Error al guardar cliente")).when(clienteService).saveDTO(clienteDto);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Juan\", \"identificacion\":\"12345\", \"direccion\":\"Calle Falsa\", \"telefono\":\"555-1234\"}")) // Ajusta el JSON de ejemplo según tu DTO
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensaje").value("Error al guardar cliente"));
    }
}
