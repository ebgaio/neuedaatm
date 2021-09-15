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
import com.neueda.atm.service.exception.userAccount.AccountNumberInUseException;
import com.neueda.atm.service.exception.userAccount.AccountPINIsWrongException;
import com.neueda.atm.service.exception.userAccount.InsufficientFundsInAccountException;
import com.neueda.atm.service.exception.userAccount.InsufficientMoneyInATMException;

@ControllerAdvice
public class userAccountExceptionHandler {

	@Autowired
	private MessageSource messageSource;
	
    // Send message when AccountNumber was register before.
    @ExceptionHandler({ AccountNumberInUseException.class })
    public ResponseEntity<Object> handlerAccountNumberInUseException(AccountNumberInUseException ex) {
		String mesageUser = messageSource.getMessage("accountnumber.in-use", null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
    }
    
    // Send message when PIN is wrong to access data of account.
    @ExceptionHandler({ AccountPINIsWrongException.class })
    public ResponseEntity<Object> handlerAccountPINIsWrongException(AccountPINIsWrongException ex) {
		String mesageUser = messageSource.getMessage("error.pin-wrong", null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
    }
    
    // Send message when funds is insufficient in account.
    @ExceptionHandler({ InsufficientFundsInAccountException.class })
    public ResponseEntity<Object> handlerInsufficientFundsInAccountException(InsufficientFundsInAccountException ex) {
		String mesageUser = messageSource.getMessage("account.insufficient-funds", null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
    }
    
    // Send message when funds is insufficient in ATM.
    @ExceptionHandler({ InsufficientMoneyInATMException.class })
    public ResponseEntity<Object> handlerInsufficientMoneyInATMException(InsufficientMoneyInATMException ex) {
		String mesageUser = messageSource.getMessage("atm.insufficient-funds", null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
    }
}
