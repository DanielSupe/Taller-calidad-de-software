package edu.unac.repository;

import edu.unac.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByDeviceId(Long deviceId);
}
