package com.taltou.library.customer;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ICustomerService {

    public Customer saveCustomer(Customer customer);

    public Customer updateCustomer(Customer customer);

    public void deleteCustomer(Integer customerId);

    public boolean checkIfIdexists(Integer id);

    public Customer findCustomerByEmail(String email);

    public List<Customer> findCustomerByLastName(String lastName);

    public Customer findCustomerById(Integer customerId);

    public Page<Customer> getPaginatedCustomersList(int begin, int end);

}


