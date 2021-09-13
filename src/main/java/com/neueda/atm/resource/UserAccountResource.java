package com.neueda.atm.resource;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.neueda.atm.model.AvailableAndBalanceAmount;
import com.neueda.atm.model.UserAccount;
import com.neueda.atm.repository.UserAccountRepository;
import com.neueda.atm.service.UserAccountService;
import com.neueda.atm.service.exception.AccountNumberInUseException;
import com.neueda.atm.service.exception.AccountPINIsWrongException;
import com.neueda.atm.service.exception.InsufficientFundsInAccountException;
import com.neueda.atm.service.exception.InsufficientMoneyInATMException;

@RestController
@RequestMapping("/useraccount")
public class UserAccountResource {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private UserAccountService userAccountService;
	
	@Autowired
	private MessageSource messageSource;
	
	// List Users Account | localhost:8080/useraccount/users
	@GetMapping
	public List<UserAccount> listAll() {
		
		return userAccountRepository.findAll();
	}
	
	// List Users Account especific user number | localhost:8080/useraccount/123456789
	@GetMapping("{accountnumber}")
	public ResponseEntity<UserAccount> getUserAccountByAccountNumber(@PathVariable long accountnumber) {
		
		UserAccount userAccount = userAccountService.getUserAccountByAccountNumber(accountnumber);
		
		return ResponseEntity.status(HttpStatus.OK).body(userAccount);
	}
	
	// List balance of a client per code(Id) and check if PIN is correct | localhost:8080/useraccount/balance/1/1234
	@GetMapping("/balancecode/{code}/{pin}")
	public String listBalanceById(@PathVariable Long code, @PathVariable String pin) {
		
		String balance = userAccountService.getBalanceByIdAndPIN(code, pin);
		
		return balance;
		
	}

	// List balance of a client per code(Id) and check if PIN is correct | localhost:8080/useraccount/123456789/1234
	@GetMapping("/balanceuser/{accountnumber}/{pin}")
	public ResponseEntity<UserAccount> listBalanceByIdAndPIN(@PathVariable long accountnumber, @PathVariable String pin) {
		
		UserAccount userAccount = userAccountService.getByAccountNumerAndPIN(accountnumber, pin);
		
		return ResponseEntity.status(HttpStatus.OK).body(userAccount);
	}

	// Take a money (Value) of the AccountNumber using PIN
	@GetMapping("/takemoney/{accountnumber}/{pin}/{value}")
	public List<AvailableAndBalanceAmount> getMoney(@PathVariable long accountnumber, @PathVariable String pin, @PathVariable long value) {
	
		List<AvailableAndBalanceAmount> availableAndBalanceAmounts = userAccountService.getMoney(accountnumber, pin, value);
				
		return availableAndBalanceAmounts;
	}
	
	// Save a new account | localhost:8080/useraccount
	@PostMapping()
	public ResponseEntity<UserAccount> createAccount(@RequestBody UserAccount userAccount) {
		
		UserAccount userAccountSaved = userAccountService.createUserAccount(userAccount);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(userAccountSaved);
	}
	
	// 	Update account to specific code(Id) - Active true/false | localhost:8080/useraccount/2/active
	@PutMapping("/{code}/active")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updatePropertyActive(@PathVariable Long code, @RequestBody boolean active) {
		
		userAccountService.updatePropertyActive(code, active);
	}
	
	// Update datas of account to specific code(Id) | localhost:8080/useraccount/1 
	@PutMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updatePropertiesOfUserAccount(@PathVariable Long code, @RequestBody UserAccount userAccount) {
		
		userAccountService.updatePropertiesOfUserAccount(code, userAccount);
	}
	
	// Delete account by code(Id) | localhost:8080/useraccount/3
	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long code) {
		
		userAccountRepository.deleteById(code);
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
    
    // Send message when AccountNumber was register before.
    @ExceptionHandler({ AccountNumberInUseException.class })
    public ResponseEntity<Object> handlerAccountNumberInUseException(AccountNumberInUseException ex) {
		String mesageUser = messageSource.getMessage("accountnumber.in-use", null, LocaleContextHolder.getLocale());
		String mesageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mesageUser, mesageDeveloper));
		return ResponseEntity.badRequest().body(erros);
    }
}