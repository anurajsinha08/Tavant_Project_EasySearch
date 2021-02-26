package com.tavant.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tavant.client.model.Address;
import com.tavant.client.repository.AddressRepository;

@Service
public class AddressServiceImpl implements AddressService{

	@Autowired
	AddressRepository addressRepository;
	@Override
	public boolean addAddress(Address address) {
		
		Address res = addressRepository.save(address);
		return res!=null;
	}

}
