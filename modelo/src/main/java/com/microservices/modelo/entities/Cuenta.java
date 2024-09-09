package com.microservices.modelo.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="cuenta", schema = "public")
public class Cuenta implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cuenta_id")
	private Long cuentaId;
	@Column(name="numero_cuenta")
	private String numeroCuenta;
	@Column(name="saldo_inicial")
	private BigDecimal saldoInicial;
	@Column(name="tipo_cuenta")
	private String tipoCuenta;
	@Column(name="cliente_id")
	private Long clienteId;
	private Boolean estado;

}
