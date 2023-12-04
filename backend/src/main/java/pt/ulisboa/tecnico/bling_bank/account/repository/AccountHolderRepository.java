package pt.ulisboa.tecnico.bling_bank.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {

    Optional<AccountHolder> findByHolderName(String holderName);

    Set<AccountHolder> findByHolderNameIn(Set<String> holderNames);

    boolean existsByHolderName(String holderName);
}
