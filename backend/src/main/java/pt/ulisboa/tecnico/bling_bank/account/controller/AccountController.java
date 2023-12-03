package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.account.service.AccountService;

import java.util.Set;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts/create")
    public JSONObject createAccount(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        int balance = json.getInt("balance");
        String currency = json.getString("currency");
        Set<String> holders = Set.of(json.getString("holderName"));
        return accountService.createAccount(balance, currency, holders);

    }

    @GetMapping("/accounts/{id}")
    public JSONObject getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

}
