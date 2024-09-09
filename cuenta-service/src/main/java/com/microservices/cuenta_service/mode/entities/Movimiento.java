package com.microservices.cuenta_service.mode.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="movimiento", schema = "public")
public class Movimiento implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long movimientoId;
	@Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
	private BigDecimal saldo;
	@Column(name="tipo_movimiento")
	private String tipoMovimiento;
	private BigDecimal valor;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="cuenta_id")
	private Cuenta cuenta;
}
