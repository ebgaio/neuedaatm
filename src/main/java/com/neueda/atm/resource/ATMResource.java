package com.neueda.atm.resource;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neueda.atm.exceptionHandler.NeuedaExceptionHandler.Erro;
import com.neueda.atm.model.ATM;
import com.neueda.atm.service.ATMService;
import com.neueda.atm.service.exception.ValueOfNoteDuplicatedException;

@RestController
@RequestMapping("/atm")
public class ATMResource {
	
	@Autowired
	private ATMService atmService;

	@Autowired
	private MessageSource messageSource;
	
	// List values in ATM | localhost:8080/atm
	@GetMapping
	public List<ATM> listAll() {
		
		List<ATM> atm = atmService.findAll();
		
		return atm;
	}
	
	// List values in ATM especific value | localhost:8080/atm/value
	@GetMapping("/{value}")
	public ResponseEntity<ATM> getByValue(@PathVariable long value) {
		
		ATM atm = atmService.getByValue(value);
		
		return ResponseEntity.status(HttpStatus.OK).body(atm);
	}

	// Save a new Note in ATM | localhost:8080/atm
	@PostMapping()
	public ResponseEntity<ATM> createNote(@RequestBody ATM atm) {
		
		ATM atmSaved = atmService.createATM(atm);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(atmSaved);
	}
	
	// 	Update note to specific code(Id) - Active true/false | localhost:8080/atm/2/active
	@PutMapping("/{code}/active")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updatePropertyActive(@PathVariable Long code, @RequestBody boolean active) {
		
		atmService.updatePropertyActive(code, active);
	}

	// Update amount of Note in ATM using code(Id) | localhost:8080/useraccount/1/active
	@PutMapping("/{code}")
	public ResponseEntity<ATM> updateATM(@PathVariable Long code, @RequestBody ATM atm) {
		
		ATM atmSaved = atmService.updateATM(code, atm);
		
		return ResponseEntity.ok(atmSaved);
	}
	
    // Send message when AccountNumber was register before.
    @ExceptionHandler({ ValueOfNoteDuplicatedException.class })
    public ResponseEntity<Object> handlerValueOfNoteDuplicatedException(ValueOfNoteDuplicatedException ex) {
		String mesageUser = messageSource.getMessage("value.note-duplicate", null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
    }
}
