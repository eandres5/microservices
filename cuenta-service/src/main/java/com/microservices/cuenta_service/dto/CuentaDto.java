package com.microservices.cuenta_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CuentaDto {

	private String numeroCuenta;
	private String saldo;
	private String tipoCuenta;
	private String clienteId;
	private String estado;
}
