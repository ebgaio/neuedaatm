package com.neueda.atm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neueda.atm.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
	
	public Optional<UserAccount> findByAccountNumber(Long number);

    @Query(value = "FROM UserAccount")
    public Page<UserAccount> listAllUseAccount(Pageable pageable);

}
