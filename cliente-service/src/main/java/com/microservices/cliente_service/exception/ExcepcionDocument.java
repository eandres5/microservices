package com.microservices.cliente_service.exception;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class ExcepcionDocument extends Exception{

	private static final long serialVersionUID = 1L;
	@Autowired
	static MessageSource messageSource;

	protected String codigoException;
	protected String mensaje;

	public ExcepcionDocument() {

	}

	public ExcepcionDocument(String codigoException, String... messageArguments) {
		this(getReporteException(codigoException, messageArguments));
		this.codigoException = codigoException;
	}

	public ExcepcionDocument(String mensaje) {
		super(mensaje);
		this.mensaje = mensaje;
	}

	private static String getReporteException(String mensaje, String... messageArguments) {
		if (messageArguments != null) {
			MessageFormat formatter = new MessageFormat("");
			formatter.applyPattern(mensaje);
			mensaje = formatter.format(messageArguments);
		}
		return mensaje;
	}

	public String getCodigoException() {
		return codigoException;
	}

	public void setCodigoException(String codigoException) {
		this.codigoException = codigoException;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	@Override
	public String toString() {
		return mensaje != null ? mensaje : "";
	}

}
