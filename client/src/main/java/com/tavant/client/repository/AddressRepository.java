package com.tavant.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tavant.client.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{

}
