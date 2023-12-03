package pt.ulisboa.tecnico.bling_bank.account;

import org.springframework.stereotype.Service;
import pt.ulislboa.tecnico.bling_bank.account.domain.Account;
import pt.ulislboa.tecnico.bling_bank.account.domain.Movement;
import pt.ulislboa.tecnico.bling_bank.account.MovementsRepository;

@Service
public class MovementsService {

    @Autowired
    private MovementRepository MovementRepository;

    public List<Movement> getMovementsPerAccount(Long id) {
        return MovementRepository.findByAccountId(id);
    }
}
