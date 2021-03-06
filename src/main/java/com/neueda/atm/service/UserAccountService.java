package com.neueda.atm.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.neueda.atm.model.ATM;
import com.neueda.atm.model.AvailableAndBalanceAmount;
import com.neueda.atm.model.UserAccount;
import com.neueda.atm.repository.ATMRepository;
import com.neueda.atm.repository.UserAccountRepository;
import com.neueda.atm.service.exception.userAccount.AccountNumberInUseException;
import com.neueda.atm.service.exception.userAccount.AccountPINIsWrongException;
import com.neueda.atm.service.exception.userAccount.InsufficientFundsInAccountException;
import com.neueda.atm.service.exception.userAccount.InsufficientMoneyInATMException;

@Service
public class UserAccountService {
	
	@Autowired
	private ATMService atmService;
	
	@Autowired
	private ATMRepository atmRepository;
	
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	public UserAccount createUserAccount(UserAccount userAccount) {
		
		boolean checked = checkUserAccountByAccountNumber(userAccount.getAccountNumber());
		
		if (!checked) {
			userAccountRepository.save(userAccount);
		} else {
			throw new AccountNumberInUseException();
		}
		
		return userAccount;

	}

	public void updatePropertyActive(Long code, boolean active) {
		
		UserAccount userAccount = this.getUserAccountByCode(code);
		userAccount.setActive(active);
		
		userAccountRepository.save(userAccount);
		
	}
	
	public void updatePropertiesOfUserAccount(Long code, UserAccount userAccount) {
		
		UserAccount userAccountSaved = getUserAccountByCode(code);
		BeanUtils.copyProperties(userAccount, userAccountSaved, "id");
		
		userAccountRepository.save(userAccountSaved);
		
	}
	
	public String getBalanceByIdAndPIN(Long code, String pin) {
		
		UserAccount userAccountSaved = getUserAccountByCode(code);
		
		UserAccount accountNumber = checkPIN(userAccountSaved, pin);
		
		return accountNumber.getBalance().toString();

	}
	
	public UserAccount getByAccountNumerAndPIN(Long accountNumber, String pin) {
		
		UserAccount userAccountSaved = getUserAccountByAccountNumber(accountNumber);

		return checkPIN(userAccountSaved, pin);
		
	}

	public UserAccount getUserAccountByAccountNumber(Long accountNumber) {
		
		UserAccount userAccountSaved = this.userAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new EmptyResultDataAccessException(1));
		
		return userAccountSaved;
		
	}

	public List<AvailableAndBalanceAmount> getMoney(Long accountNumber, String pin, Long value) {
		
		UserAccount userAccountSaved = getUserAccountByAccountNumber(accountNumber);
		
		boolean checkPIN = checkPINIsCorrect(userAccountSaved, pin);
		
		if (!checkPIN) {
			throw new AccountPINIsWrongException();
		}
		
		Long balanceUser = userAccountSaved.getBalance().longValue(); // Value of Balance actual
		Long balanceOverdraft = userAccountSaved.getOverdraft().longValue(); // Value of Overdraft actual
		Long valueTotal = balanceUser + balanceOverdraft; // Value of Balance + Overdraft
		Long valuePlus = 0L; // Value used of the Overdraft
		Long balanceFinal = 0L; // Register value in Balance after withdrawal used
		boolean useOverdraft = false; // Used if UserAccount have value in Overdraft
		
		if (value > valueTotal) {
			throw new InsufficientFundsInAccountException();
		} else {
			if (value > balanceUser) {
				if (balanceOverdraft > 0) {
					valuePlus = value - balanceUser;
					balanceFinal = 0L;
					useOverdraft = true;
				}
			} else {
				balanceFinal = balanceUser - value;
			}
		}

		List<ATM> atmTotal = atmService.findAll();
		
		Long balanceNotes = this.getTotalValue(atmTotal);
			
		if (value > balanceNotes) {
			throw new InsufficientMoneyInATMException();
		}
		
		List<AvailableAndBalanceAmount> availables = showNotesDispense(value, atmTotal);
		
		if (useOverdraft) {
			userAccountSaved.setBalance(new BigDecimal(balanceFinal));
			userAccountSaved.setOverdraft(new BigDecimal(valuePlus));
			userAccountRepository.save(userAccountSaved);
		} else {
			userAccountSaved.setBalance(new BigDecimal(balanceFinal));
			userAccountRepository.save(userAccountSaved);
		}

		return availables;
		
	}
	
	private boolean checkUserAccountByAccountNumber(Long accountNumber) {
		
		return this.userAccountRepository.findByAccountNumber(accountNumber).isPresent();
		
	}

	private List<AvailableAndBalanceAmount> showNotesDispense(Long value, List<ATM> atmTotal) {
		
		Long noteValue = 0L;
		Long amountValue = 0L;
		int count = 0;

		List<ATM> atmTotalOrderedDesc = getTotalOrdered(atmTotal);
		
		List<AvailableAndBalanceAmount> availables = new  ArrayList<>();
		
		AvailableAndBalanceAmount amount = new AvailableAndBalanceAmount();

		for (ATM atm : atmTotalOrderedDesc) {
			
			noteValue = atm.getValue();
			amountValue = atm.getAmount();

			if (atm.isActive() && value > 0 && amountValue > 0) {
				if (value >= noteValue) {
					
					do{
						atm.setAmount(atm.getAmount() - 1);
						value -= noteValue;
						count++;
					}
					while(value >= noteValue && count < amountValue);
					
					amount.setAmount(Long.valueOf(count));
					amount.setValue(noteValue);
					availables.add(amount);
					amount = new AvailableAndBalanceAmount();
					count = 0;
				}
			}
		}
		
		atmRepository.saveAll(atmTotalOrderedDesc);
		
		return availables;
		
	}
	
	private List<ATM> getTotalOrdered(List<ATM> atmTotal) {
		
		List<ATM> sortedList = atmTotal.stream()
				.sorted(Comparator.comparingLong(ATM::getValue)
						.reversed())
				.collect(Collectors.toList());
		
		return sortedList;
		
	}

	private Long getTotalValue(List<ATM> atmTotal) {
		
		Long result = 0L;

		for (ATM atm : atmTotal) {
			if (atm.isActive()) {
				result += atm.getAmount() * atm.getValue();
			}
		}
		
		return result;
		
	}
	
	private UserAccount checkPIN(UserAccount userAccountSaved, String pin) {
		
		boolean checkPIN = checkPINIsCorrect(userAccountSaved, pin);
		
		if (checkPIN) {
			return userAccountSaved;
		} else {
			throw new AccountPINIsWrongException();
		}
		
	}
	
	private boolean checkPINIsCorrect(UserAccount userAccount, String pin) {
		
		if (userAccount.getPin().equals(pin)) {
			return true;
		}
		
		return false;
		
	}
	
	private UserAccount getUserAccountByCode(Long code) {
		
		UserAccount userAccountSave = this.userAccountRepository.findById(code).orElseThrow(() -> new EmptyResultDataAccessException(1));
		
		return userAccountSave;
		
	}

}
