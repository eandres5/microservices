package com.microservices.cuenta_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteDto {

	private String fecha;
	private String cliente;
	private String numeroCuenta;
	private String tipo;
	private String saldoInicial;
	private String estado;
	private String movimiento;
	private String saldoDisponible;

}
