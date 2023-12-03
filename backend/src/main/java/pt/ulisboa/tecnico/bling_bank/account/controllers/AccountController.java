package pt.ulisboa.tecnico.bling_bank.account.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts/create")
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account.getBalance(), account.getCurrencyType());
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
