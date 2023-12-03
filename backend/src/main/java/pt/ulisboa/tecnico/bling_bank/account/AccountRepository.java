package pt.ulisboa.tecnico.bling_bank.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {
}
