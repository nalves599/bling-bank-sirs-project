package pt.ulisboa.tecnico.bling_bank.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.account.domain.Movement;

@Repository
@Transactional
public interface MovementRepository extends JpaRepository<Movement, Long> {
}
