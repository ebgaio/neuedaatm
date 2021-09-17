package com.neueda.atm.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neueda.atm.model.AvailableAndBalanceAmount;
import com.neueda.atm.model.UserAccount;
import com.neueda.atm.repository.UserAccountRepository;
import com.neueda.atm.service.UserAccountService;

@RestController
@RequestMapping("/useraccount")
public class UserAccountResource {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private UserAccountService userAccountService;
	
	// List Users Account | localhost:8080/useraccount
	@GetMapping
	public Page<UserAccount> listAllUserAccount(Pageable pageable) {
		
		Page<UserAccount> listAllUserAccount =  userAccountRepository.listAllUseAccount(pageable);
		
		return listAllUserAccount;
	}
	
	// List Users Account especific user number | localhost:8080/useraccount/123456789
	@GetMapping("{accountnumber}")
	public ResponseEntity<UserAccount> getUserAccountByAccountNumber(@PathVariable long accountnumber) {
		
		UserAccount userAccount = userAccountService.getUserAccountByAccountNumber(accountnumber);
		
		return ResponseEntity.status(HttpStatus.OK).body(userAccount);
	}

	// List balance of a client per code(Id) and check if PIN is correct | localhost:8080/useraccount/balance/1
	@GetMapping("/balancecode/{code}")
	public String listBalanceById(@PathVariable Long code, @RequestBody String pin) {
		
		String balance = userAccountService.getBalanceByIdAndPIN(code, pin);
		
		return balance;
		
	}
	
	// List balance of a client per code(Id) and check if PIN is correct | localhost:8080/useraccount/123456789/1234
	@GetMapping("/balanceuser/{accountnumber}")
	public ResponseEntity<UserAccount> listBalanceByIdAndPIN(@PathVariable long accountnumber, @RequestBody String pin) {
		
		UserAccount userAccount = userAccountService.getByAccountNumerAndPIN(accountnumber, pin);
		
		return ResponseEntity.status(HttpStatus.OK).body(userAccount);
	}

	// Take a money (Value) of the AccountNumber using PIN | localhost:8080/useraccount/takemoney/123456789/600
	@GetMapping("/takemoney/{accountnumber}/{value}")
	public List<AvailableAndBalanceAmount> takeMoney(@PathVariable long accountnumber, @RequestBody String pin, @PathVariable long value) {
	
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
	
}
