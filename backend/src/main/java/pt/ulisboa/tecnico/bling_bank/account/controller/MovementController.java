package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.account.service.MovementService;

import java.util.Date;

@RestController
public class MovementController {

    @Autowired
    private MovementService movementService;

    @PostMapping("/movements/create")
    public JSONObject createMovement(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        Date date = new Date(json.getString("date"));
        int value = json.getInt("value");
        String description = json.getString("description");
        Long accountId = json.getLong("accountId");
        return movementService.createMovement(date, value, description, accountId);
    }

    @GetMapping("/movements/{id}")
    public JSONObject getMovement(@PathVariable Long id) {
        return movementService.getMovement(id);
    }

    @GetMapping("/movements/account/{id}")
    public JSONObject getMovementByAccount(@PathVariable Long id) {
        return movementService.getMovementByAccount(id);
    }
}
