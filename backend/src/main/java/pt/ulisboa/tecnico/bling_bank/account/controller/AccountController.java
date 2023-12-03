package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.account.AccountService;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts/create")
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account.getBalance(), account.getCurrencyType());
    }

    /*
    
    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }
    
     */
}
