package pt.ulisboa.tecnico.bling_bank.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.payment.domain.Payment;

@Repository
@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}