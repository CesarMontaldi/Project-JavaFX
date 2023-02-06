package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	// # Cria uma lista para ir adicionando os possiveis erros, com chave e valor.
	private Map<String, String> errors = new HashMap<>(); 
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	// # Retorna os erros capturados.
	public Map<String, String> getErrors() { 
		return errors; 
	}
	
	// # Adiciona os erros a lista.
	public void addError(String fieldName, String errorMessage) { 
		errors.put(fieldName, errorMessage);
	}
}
