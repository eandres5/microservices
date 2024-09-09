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
public class MovimientoDto {

	private String numeroDeCuenta;
	private String tipoMovimiento;
	private String valor;
	private String clienteId;
	private String fecha;
	private String saldo;

}
