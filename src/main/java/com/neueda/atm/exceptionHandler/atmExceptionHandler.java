package com.neueda.atm.exceptionHandler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.neueda.atm.exceptionHandler.NeuedaExceptionHandler.Erro;
import com.neueda.atm.service.exception.atm.ValueOfNoteDuplicatedException;

@ControllerAdvice
public class atmExceptionHandler {

	@Autowired
	private MessageSource messageSource;
    
    // Send message when AccountNumber was register before.
    @ExceptionHandler({ ValueOfNoteDuplicatedException.class })
    public ResponseEntity<Object> handlerValueOfNoteDuplicatedException(ValueOfNoteDuplicatedException ex) {
		
    	String message = "value.note-duplicate";
		
    	return errorMessages(message,ex);
    	
    }
    
    // Method that manipulate the messages using Erro class.
	private ResponseEntity<Object> errorMessages(String message, Exception ex) {
		String mesageUser = messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
	}
	
}
