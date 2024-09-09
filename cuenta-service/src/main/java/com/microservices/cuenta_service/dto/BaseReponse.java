package com.microservices.cuenta_service.dto;

public record BaseReponse(String[] errorMessages) {

	public boolean hasError() {
		return errorMessages != null && errorMessages.length > 0;
	}
}
