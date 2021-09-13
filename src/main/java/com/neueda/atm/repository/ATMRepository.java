package com.neueda.atm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neueda.atm.model.ATM;

public interface ATMRepository extends JpaRepository<ATM, Long> {

	public Optional<ATM> findByValue(long value);
	
}
