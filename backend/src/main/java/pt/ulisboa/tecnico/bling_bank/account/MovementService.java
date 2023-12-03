package pt.ulisboa.tecnico.bling_bank.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovementService {

    @Autowired
    private pt.ulisboa.tecnico.bling_bank.account.repository.MovementRepository MovementRepository;

    /*
    public List<Movement> getMovementsPerAccount(Long id) {
        return MovementRepository.findByAccountId(id);
    }
    
     */
}
