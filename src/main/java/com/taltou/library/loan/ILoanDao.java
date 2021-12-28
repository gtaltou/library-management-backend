package com.taltou.library.loan;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ILoanDao extends JpaRepository<Loan, Integer> {

    public List<Loan> findByEndDateBefore(LocalDate maxEndDate);

    @Query(   "SELECT lo "
            + "FROM Loan lo "
            + "INNER JOIN lo.pk.customer c "
            + "WHERE UPPER(c.email) = UPPER(?1) "
            + "   AND lo.status = ?2 ")
    public List<Loan> getAllOpenLoansOfThisCustomer(String email, LoanStatus status);

    @Query(   "SELECT lo "
            + "FROM Loan lo "
            + "INNER JOIN lo.pk.book b "
            + "INNER JOIN lo.pk.customer c "
            + "WHERE b.id =	?1 "
            + "   AND c.id = ?2 "
            + "   AND lo.status = ?3 ")
    public Loan getLoanByCriteria(Integer bookId, Integer customerId, LoanStatus status);
}
