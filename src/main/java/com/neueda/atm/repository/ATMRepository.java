package com.neueda.atm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neueda.atm.model.ATM;

public interface ATMRepository extends JpaRepository<ATM, Long> {

	public Optional<ATM> findById(Long value);
	
	public Optional<ATM> findByValue(Long value);

	
}
