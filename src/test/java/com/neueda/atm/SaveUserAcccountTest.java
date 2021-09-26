package com.neueda.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.neueda.atm.model.UserAccount;
import com.neueda.atm.repository.UserAccountRepository;
import com.neueda.atm.service.UserAccountService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SaveUserAcccountTest {

	@MockBean
	private UserAccountRepository userAccountRepository;
	
//	@Autowired
	@InjectMocks
	private UserAccountService userAccountService;

	@Test
//	@Timeout(value = 42, unit = TimeUnit.MINUTES)
	@DisplayName("Test to save a UserAccount using Mock across UserService")
	void createUserAccountTest() {
		
		UserAccount userAccount = newUserAccount();
		
//		when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
		
		UserAccount userAccountSaved = userAccountService.createUserAccount(userAccount);

		assertTrue(userAccountSaved.getBalance().equals(userAccount.getBalance()));
		assertEquals(userAccountSaved.getAccountNumber(), userAccount.getAccountNumber());
		System.out.println("Method createUserAccount is OK");
		
	}
	
	private UserAccount newUserAccount() {
		
		UserAccount userAccount = new UserAccount();
		userAccount.setAccountNumber(1223344556L);
		userAccount.setBalance(new BigDecimal(300));
		userAccount.setOverdraft(new BigDecimal(100));
		userAccount.setPin("2354");
		userAccount.setActive(true);
		return userAccount;
		
	}

}
