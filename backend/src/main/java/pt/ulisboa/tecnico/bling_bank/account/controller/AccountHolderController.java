package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.account.service.AccountHolderService;

@RestController
public class AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;

    @PostMapping("/holders/create")
    public JSONObject createAccountHolder(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        String holderName = json.getString("holderName");
        return accountHolderService.createAccountHolder(holderName);
    }

    @GetMapping("/holders/{id}")
    public JSONObject getAccountHolder(@PathVariable Long id) {
        return accountHolderService.getAccountHolder(id);
    }

    @GetMapping("/holders")
    public JSONObject getAccountHolders() { return accountHolderService.getAccountHolders(); }
}
