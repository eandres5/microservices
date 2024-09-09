package com.microservices.modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoResponseDto {
	
	private LocalDateTime fecha;
    private String nombre;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal valor;
    private boolean estado;
    private String tipoMovimiento;
    private BigDecimal saldo;

    // Constructor
    public MovimientoResponseDto(LocalDateTime fecha, String nombre, String numeroCuenta, String tipoCuenta, BigDecimal valor, boolean estado, String tipoMovimiento, BigDecimal saldo) {
        this.fecha = fecha;
        this.nombre = nombre;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.valor = valor;
        this.estado = estado;
        this.tipoMovimiento = tipoMovimiento;
        this.saldo = saldo;
    }
    
}