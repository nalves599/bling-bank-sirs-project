package pt.ulisboa.tecnico.bling_bank.account.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountHolderRepository;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountRepository;
import pt.ulisboa.tecnico.bling_bank.util.BlingBankException;
import pt.ulisboa.tecnico.bling_bank.util.ErrorMessage;

import java.util.Set;

@Service
public class AccountService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    public JSONObject createAccount(int balance, String currencyType, Set<String> holderNames) {
        Set<AccountHolder> accountHolders = accountHolderRepository.findByHolderNameIn(holderNames);
        if (accountHolders.size() != holderNames.size()) {
            throw new BlingBankException(ErrorMessage.ACCOUNT_HOLDER_NOT_FOUND);
        }

        Account account = new Account(balance, currencyType, accountHolders);
        accountRepository.save(account);
        return getJsonObject(account);
    }

    public JSONObject getAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        return getJsonObject(account);
    }

    private JSONObject getJsonObject(Account account) {
        JSONObject json = new JSONObject();
        json.put("id", account.getId());
        json.put("accountHolder", account.getAccountHolder());
        json.put("balance", account.getBalance());
        json.put("currency", account.getCurrencyType());
        json.put("movements", account.getMovements());
        return json;
    }
}
