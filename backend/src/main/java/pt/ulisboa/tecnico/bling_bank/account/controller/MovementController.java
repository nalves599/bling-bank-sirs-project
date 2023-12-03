package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.bling_bank.account.MovementService;

@RestController
public class MovementController {

    @Autowired
    private MovementService movementService;

    /*
    @GetMapping("/accounts/{id}/movements")
    public List<Movement> getMovementsPerAccount(@PathVariable Long id) {
        return movementsService.getMovementsPerAccount(id);
    }
    
     */
}
