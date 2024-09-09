package com.microservices.cliente_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteEditDto {

	private String nombre;
	private String genero;
	private String edad;
	private String direccion;
	private String telefono;
	private String contrasena;
}
