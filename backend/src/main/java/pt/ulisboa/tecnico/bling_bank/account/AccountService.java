package pt.ulisboa.tecnico.bling_bank.account;

import org.springframework.stereotype.Service;
import pt.ulislboa.tecnico.bling_bank.account.domain.Account;
import pt.ulislboa.tecnico.bling_bank.account.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    public Account createAccount(int balance, String currencyType) {
        Account account = new Account(balance, currencyType);
        accountRepository.save(account);
        return account;
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}
