package com.neueda.atm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neueda.atm.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
	
	public Optional<UserAccount> findByAccountNumber(Long number);

}
