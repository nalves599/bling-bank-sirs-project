package pt.ulisboa.tecnico.bling_bank.account.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovementsController {

    @Autowired
    private MovementsService movementsServiceService;

    @GetMapping("/accounts/{id}/movements")
    public List<Movements> getMovementsPerAccount(@PathVariable Long id) {
        return movementsService.getMovementsPerAccount(id);
    }
}
