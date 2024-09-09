package com.microservices.cuenta_service.serviceImp;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.microservices.cuenta_service.dao.MovimientoDao;
import com.microservices.cuenta_service.dto.CuentaDto;
import com.microservices.cuenta_service.dto.MovimientoDto;
import com.microservices.cuenta_service.mode.entities.Cuenta;
import com.microservices.cuenta_service.mode.entities.Movimiento;
import com.microservices.cuenta_service.service.CuentaService;
import com.microservices.cuenta_service.service.MovimientoService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoServiceImp implements MovimientoService {

	@Autowired
	MovimientoDao movimientoDao;
	@Autowired
	CuentaService cuentaService;

	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<List<MovimientoDto>> listMovimientos() throws Exception {
		var movimientos = movimientoDao.findAll();
		List<MovimientoDto> listaMovimientos = movimientos.stream().map(this::mapToMovimientoDto).toList();
		return CompletableFuture.completedFuture(listaMovimientos);
	}

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<MovimientoDto> getMovimientoDto(Long id) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Movimiento movimiento = getMovimiento(id); // Asume que este método es síncrono

				// Define un formateador para LocalDateTime
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String fechaFormat = movimiento.getFecha().format(formatter);

				return MovimientoDto.builder()
						.numeroDeCuenta(
								movimiento.getCuenta() != null ? movimiento.getCuenta().getNumeroCuenta() : null)
						.tipoMovimiento(movimiento.getTipoMovimiento()).fecha(fechaFormat)
						.valor(movimiento.getValor() != null ? movimiento.getValor().toString() : null)
						.saldo(movimiento.getSaldo() != null ? movimiento.getSaldo().toString() : null)
						.clienteId(movimiento.getCuenta().getClienteId().toString()).build();
			} catch (Exception e) {
				// Agrega más información al error
				System.err.println("Error en getMovimientoDto: " + e.getMessage());
				e.printStackTrace(); // Imprime el stack trace
				throw new RuntimeException("Error al obtener el movimiento", e);
			}
		});
	}

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<Void> saveDTO(MovimientoDto dto) {
		return CompletableFuture.runAsync(() -> {
			try {
				// Se debe guardar el movimiento con la entidad de la cuenta
				LocalDateTime now = LocalDateTime.now();
				Cuenta cuenta = cuentaService.getCuentaByNumeroCuenta(dto.getNumeroDeCuenta());

				BigDecimal saldo = new BigDecimal(dto.getSaldo());
				BigDecimal valor = new BigDecimal(dto.getValor());
				if (dto.getTipoMovimiento().equals("Retiro")) {
					saldo = saldo.subtract(valor);
				} else {
					saldo = saldo.add(valor);
				}

				Movimiento movimiento = new Movimiento();
				movimiento.setFecha(now);
				movimiento.setValor(valor);
				movimiento.setTipoMovimiento(dto.getTipoMovimiento());
				movimiento.setCuenta(cuenta);
				movimiento.setSaldo(saldo);
				movimientoDao.save(movimiento);
			} catch (Exception e) {
				throw new RuntimeException("Error al guardar el DTO del movimiento", e);
			}
		});
	}

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<Void> deleteMovimiento(Long id) {
		return CompletableFuture.runAsync(() -> {
			try {
				Movimiento movimiento = getMovimiento(id);
				movimientoDao.delete(movimiento);
			} catch (Exception e) {
				throw new RuntimeException("Error al eliminar el movimiento: " + e.getMessage(), e);
			}
		});
	}

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<Void> updateMovimiento(Long id, MovimientoDto movimientoDto) {
		return CompletableFuture.runAsync(() -> {
			try {
				Movimiento movimiento = getMovimiento(id);
				movimiento.setTipoMovimiento(movimientoDto.getTipoMovimiento());
				movimiento.setValor(new BigDecimal(movimientoDto.getValor()));
				movimientoDao.save(movimiento);
			} catch (Exception e) {
				throw new RuntimeException("Error al actualizar el movimiento: " + e.getMessage(), e);
			}
		});
	}

	private MovimientoDto mapToMovimientoDto(Movimiento movimiento) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String fechaFormat = movimiento.getFecha().format(formatter);
		return MovimientoDto.builder().clienteId(movimiento.getCuenta().getClienteId() + "")
				.numeroDeCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getNumeroCuenta() : null)
				.tipoMovimiento(movimiento.getTipoMovimiento()).fecha(fechaFormat)
				.valor(movimiento.getValor() != null ? movimiento.getValor().toString() : null)
				.saldo(movimiento.getSaldo() != null ? movimiento.getSaldo().toString() : null).build();
	}

	public Movimiento getMovimiento(Long id) throws Exception {
	    return movimientoDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));

	}

}
