package pt.ulisboa.tecnico.bling_bank.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.auth.domain.AuthHolder;

import java.util.Optional;

@Repository
@Transactional
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    @Query(value = "select * from auth_holders u where u.username = lower(:username)", nativeQuery = true)
    Optional<AuthHolder> findAuthUserByUsername(String username);
}
