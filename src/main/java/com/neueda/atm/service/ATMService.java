package com.neueda.atm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.neueda.atm.model.ATM;
import com.neueda.atm.repository.ATMRepository;
import com.neueda.atm.service.exception.atm.ValueOfNoteDuplicatedException;

@Service
public class ATMService {

	@Autowired
	private ATMRepository atmRepository;
	
	public List<ATM> findAll() {
	
		List<ATM> atm = atmRepository.findAll();
	
		return atm;
	}

	public ATM getByValue(Long value) {
		
		Optional<ATM> atmSave = this.atmRepository.findByValue(value);

		if (atmSave.isPresent()) {
			return atmSave.get();
		} else {
			throw new EmptyResultDataAccessException(1);
		}
	}

	public ATM getById(Long code) {
		
		Optional<ATM> atmSave = this.atmRepository.findById(code);

		if (atmSave.isPresent()) {
			return atmSave.get();
		} else {
			throw new EmptyResultDataAccessException(1);
		}
	}

	public ATM createATM(ATM atm) {
		
		checkIfRegistered(atm.getValue());
		
		ATM atmSaved = new ATM();
		atmSaved.setValue(atm.getValue());
		atmSaved.setAmount(atm.getAmount());
		atmSaved.setActive(atm.isActive());
		
		atmRepository.save(atmSaved);
				
		return atmSaved;
	}

	public void updatePropertyActive(Long code, boolean active) {
		
		ATM atm = this.getATMByCode(code);
		atm.setActive(active);
		
		atmRepository.save(atm);
	}
	
	public ATM updateATM(Long code, ATM atm) {
		
		ATM atmSaved = getATMByCode(code);
		BeanUtils.copyProperties(atm, atmSaved, "id");
		
		return atmRepository.save(atmSaved);
	}
	
	private ATM getATMByCode(Long code) {
		
		ATM atmSave = this.atmRepository.findById(code).orElseThrow(() -> new EmptyResultDataAccessException(1));
		
		return atmSave;
	}

	private ATM checkIfRegistered(Long value) {
		
		Optional<ATM> atmSave = this.atmRepository.findByValue(value);

		if (atmSave.isEmpty()) {
			return null;
		} else {
			throw new ValueOfNoteDuplicatedException();
		}
	}
	
}
