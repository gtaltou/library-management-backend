package com.taltou.library.customer;


import java.time.LocalDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Customer Model")
public class CustomerDTO implements Comparable<CustomerDTO>{

    @ApiModelProperty(value = "Customer id")
    private Integer id;

    @ApiModelProperty(value = "Customer first name")
    private String firstName;

    @ApiModelProperty(value = "Customer last name")
    private String lastName;

    @ApiModelProperty(value = "Customer job")
    private String job;

    @ApiModelProperty(value = "Customer address")
    private String address;

    @ApiModelProperty(value = "Customer email")
    private String email;

    @ApiModelProperty(value = "Customer creation date in the system")
    private LocalDate creationDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(CustomerDTO o) {
        return this.lastName.compareToIgnoreCase(o.getLastName());
    }

}
