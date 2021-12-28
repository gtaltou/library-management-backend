package com.taltou.library.customer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerDao extends JpaRepository<Customer, Integer> {

    public Customer findCustomerByEmailIgnoreCase(String email);

    public List<Customer> findCustomerByLastNameIgnoreCase(String lastName);

}
