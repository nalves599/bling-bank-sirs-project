package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.account.service.AccountService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts/create")
    public String createAccount(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        int balance = json.getInt("balance");
        String currency = json.getString("currency");
        Set<String> holders = json.getJSONArray("holders").toList().stream().map(Object::toString).collect(Collectors
            .toSet());
        return accountService.createAccount(balance, currency, holders);

    }

    @GetMapping("/accounts/{id}")
    public String getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @GetMapping("/accounts/holder/{holderName}")
    public String getAccountsFromHolder(@PathVariable String holderName) {
        return accountService.getAccountsFromHolder(holderName);
    }
}
