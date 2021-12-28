package com.taltou.library.loan;


import com.taltou.library.book.Book;
import com.taltou.library.customer.Customer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class LoanId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3912193101593832821L;

    private Book book;

    private Customer customer;

    private LocalDateTime creationDateTime;

    public LoanId() {
        super();
    }

    public LoanId(Book book, Customer customer) {
        super();
        this.book = book;
        this.customer = customer;
        this.creationDateTime = LocalDateTime.now();
    }

    @ManyToOne
    public Book getBook() {
        return book;
    }

    public void setBook(Book bbok) {
        this.book = bbok;
    }

    @ManyToOne
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name = "CREATION_DATE_TIME")
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((book == null) ? 0 : book.hashCode());
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoanId other = (LoanId) obj;
        if (book == null) {
            if (other.book != null)
                return false;
        } else if (!book.equals(other.book))
            return false;
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        return true;
    }

}
