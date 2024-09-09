package com.microservices.cuenta_service.serviceImp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservices.cuenta_service.dao.CuentaDao;
import com.microservices.cuenta_service.dao.MovimientoDao;
import com.microservices.cuenta_service.dto.ClienteDto;
import com.microservices.cuenta_service.dto.CuentaDto;
import com.microservices.cuenta_service.dto.CuentaEditDto;
import com.microservices.cuenta_service.dto.ReporteDto;
import com.microservices.cuenta_service.mode.entities.Cuenta;
import com.microservices.cuenta_service.mode.entities.Movimiento;
import com.microservices.cuenta_service.service.CuentaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaServiceImp implements CuentaService {

	@Autowired
	CuentaDao cuentaDao;
	@Autowired
	MovimientoDao movimientoDao;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<List<CuentaDto>> listCuentas() throws Exception {
		var cuentas = cuentaDao.findAll();
		List<CuentaDto> listaCuentas = cuentas.stream().map(this::mapToCuentaDto).toList();
		return CompletableFuture.completedFuture(listaCuentas);
	}

	@Transactional
	@Async("asyncExecutor")
	@Override
	public CompletableFuture<CuentaDto> getCuentaDto(Long cuentaId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Cuenta cuenta = cuentaDao.findById(cuentaId)
						.orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

				return CuentaDto.builder()
						.clienteId(cuenta.getClienteId() != null ? cuenta.getClienteId().toString() : null)
						.numeroCuenta(cuenta.getNumeroCuenta()).tipoCuenta(cuenta.getTipoCuenta())
						.saldo(cuenta.getSaldoInicial() != null ? cuenta.getSaldoInicial().toString() : null).build();
			} catch (Exception e) {
				throw new RuntimeException("Error al obtener la cuenta", e);
			}
		});
	}

	@Async("asyncExecutor")
	@Override
	public CompletableFuture<Void> saveDTO(CuentaDto dto) {
		return webClientBuilder.build().get().uri("lb://cliente-service/clientes/isCliente/" + dto.getClienteId())
				.retrieve().bodyToMono(Boolean.class).flatMap(result -> {
					if (result != null && result) {
						Cuenta cuenta = new Cuenta();
						cuenta.setNumeroCuenta(dto.getNumeroCuenta());
						cuenta.setEstado(Boolean.parseBoolean(dto.getEstado()));
						cuenta.setTipoCuenta(dto.getTipoCuenta());
						cuenta.setClienteId(Long.parseLong(dto.getClienteId()));
						cuenta.setSaldoInicial(new BigDecimal(dto.getSaldo()));

						// Guardar la entidad de manera asíncrona
						return Mono.fromRunnable(() -> {
							try {
								save(cuenta);
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					} else {
						return Mono.error(new RuntimeException("Cliente no encontrado"));
					}
				}).then().toFuture().exceptionally(ex -> {
					throw new RuntimeException("Error al guardar DTO: " + ex.getMessage(), ex);
				});
	}

	private void save(Cuenta cuenta) throws Exception {
		try {
			cuentaDao.save(cuenta);
		} catch (Exception e) {
			throw new Exception("error: " + e.getMessage());
		}
	}

	@Override
	public void deleteCuenta(Long cuentaId) throws Exception {
		try {
			Cuenta cuenta = getCuenta(cuentaId);
			cuentaDao.delete(cuenta);
		} catch (Exception e) {
			throw new Exception("error: " + e.getMessage());
		}
	}

	@Override
	public void updateCuenta(Long cuentaId, CuentaEditDto cuentaDto) throws Exception {
		Cuenta cuenta = getCuenta(cuentaId);
		cuenta.setSaldoInicial(new BigDecimal(cuentaDto.getSaldoInicial()));
		cuenta.setTipoCuenta(cuentaDto.getTipoCuenta());
		cuenta.setEstado(Boolean.parseBoolean(cuentaDto.getEstado()));
		cuentaDao.save(cuenta);
	}

	public Cuenta getCuenta(Long cuentaId) throws Exception {
		return cuentaDao.getReferenceById(cuentaId);
	}

	private CuentaDto mapToCuentaDto(Cuenta cuenta) {
		return CuentaDto.builder().clienteId(cuenta.getClienteId() + "").numeroCuenta(cuenta.getNumeroCuenta())
				.tipoCuenta(cuenta.getTipoCuenta()).saldo(cuenta.getSaldoInicial() + "").build();
	}

	@Override
	public Cuenta getCuentaByNumeroCuenta(String numeroCuenta) throws Exception {
		Optional<Cuenta> cuenta = cuentaDao.findByNumeroCuenta(numeroCuenta);
		if (cuenta != null) {
			Cuenta cuentaEntity = cuenta.get();
			return cuentaEntity;
		} else {
			throw new Exception("Cuenta no encontrada");
		}
	}

	@Override
	@Async("asyncExecutor")
	public CompletableFuture<ReporteDto> generarReporte(Long clienteId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return CompletableFuture.supplyAsync(() -> {
            // Primero obtenemos la cuenta
            Optional<Cuenta> cuentaOptional = cuentaDao.findFirstByClienteId(clienteId);
            if (cuentaOptional.isEmpty()) {
                // Manejo del caso en que no se encuentra la cuenta
                return null;
            }

            Cuenta cuenta = cuentaOptional.get();

            // Llamada al microservicio para obtener la persona
            ClienteDto persona = webClientBuilder.build()
                .get()
                .uri("http://cliente-service/clientes/" + clienteId)
                .retrieve()
                .bodyToMono(ClienteDto.class)
                .block(); // Bloqueo para obtener el resultado

            if (persona == null) {
                return null; // Manejo del caso en que no se encuentra la persona
            }

            // Obtener movimientos por cuenta y fechas
            List<Movimiento> movimientos = movimientoDao.findByCuenta_CuentaIdAndFechaBetween(cuenta.getCuentaId(), fechaDesde, fechaHasta);

            // Crear el reporte DTO
            ReporteDto reporte = new ReporteDto();
            reporte.setFecha(fechaDesde + " a " + fechaHasta);
            reporte.setCliente(persona.getNombre());
            reporte.setNumeroCuenta(cuenta.getNumeroCuenta());
            reporte.setTipo(cuenta.getTipoCuenta());
            reporte.setSaldoInicial(cuenta.getSaldoInicial().toString());
            reporte.setEstado(cuenta.getEstado().toString());

            // Sumar todos los movimientos
            BigDecimal totalMovimientos = movimientos.stream()
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            reporte.setMovimiento(totalMovimientos.toString());

            // Calcular el saldo disponible
            reporte.setSaldoDisponible(cuenta.getSaldoInicial().add(totalMovimientos).toString());

            return reporte;
        });
    }
	
//	@Async("asyncExecutor")
//    @Override
//	public CompletableFuture<ReporteDto> generarReporte(Long clienteId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
//        // Primero obtenemos la cuenta
//        Optional<Cuenta> cuentaOptional = cuentaDao.findFirstByClienteId(clienteId);
//        if (cuentaOptional.isEmpty()) {
//            // Manejo del caso en que no se encuentra la cuenta
//            return CompletableFuture.completedFuture(null);
//        }
//
//        Cuenta cuenta = cuentaOptional.get();
//
//        // Llamada al microservicio para obtener la persona
//        return CompletableFuture.supplyAsync(() ->
//            webClientBuilder.build()
//                .get()
//                .uri("http://cliente-service/clientes/" + clienteId)
//                .retrieve()
//                .bodyToMono(ClienteDto.class)
//                .block() // Bloqueo para obtener el resultado
//        ).thenApply(persona -> {
//            // Obtener movimientos por cuenta y fechas
//            List<Movimiento> movimientos = movimientoDao.findByCuentaIdAndFechaBetween(cuenta.getCuentaId(), fechaDesde, fechaHasta);
//
//            // Crear el reporte DTO
//            ReporteDto reporte = new ReporteDto();
//            reporte.setFecha(fechaDesde + " a " + fechaHasta);
//            reporte.setCliente(persona.getNombre());
//            reporte.setNumeroCuenta(cuenta.getNumeroCuenta());
//            reporte.setTipo(cuenta.getTipoCuenta());
//            reporte.setSaldoInicial(cuenta.getSaldoInicial().toString());
//            reporte.setEstado(cuenta.getEstado().toString());
//
//            // Sumar todos los movimientos
//            BigDecimal totalMovimientos = movimientos.stream()
//                .map(Movimiento::getValor)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//            reporte.setMovimiento(totalMovimientos.toString());
//
//            // Calcular el saldo disponible
//            reporte.setSaldoDisponible(cuenta.getSaldoInicial().add(totalMovimientos).toString());
//
//            return reporte;
//        });
//    }
}
