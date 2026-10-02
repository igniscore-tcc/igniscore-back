package com.igniscore.api.repository;

import com.igniscore.api.model.Address;
import com.igniscore.api.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findByClient(Client client);
}