package pt.ulisboa.tecnico.bling_bank.account.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;
import pt.ulisboa.tecnico.bling_bank.account.domain.Movement;
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createAccount(int balance, String currencyType, Set<String> holderNames) {
        Set<AccountHolder> accountHolders = accountHolderRepository.findByHolderNameIn(holderNames);
        if (accountHolders.size() != holderNames.size()) {
            throw new BlingBankException(ErrorMessage.ACCOUNT_HOLDER_NOT_FOUND);
        }

        Account account = new Account(balance, currencyType, accountHolders);
        accountRepository.save(account);

        for (AccountHolder accountHolder : accountHolders) {
            accountHolder.addAccount(account);
        }

        return getAccountJson(account).toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        return getAccountJson(account).toString();
    }

    private JSONObject getAccountJson(Account account) {
        JSONObject json = new JSONObject();
        json.put("id", account.getId());
        json.put("accountHolder", getAccountHolderJson(account));
        json.put("balance", account.getBalance());
        json.put("movements", getAccountsMovementsJson(account));
        return json;
    }

    private JSONArray getAccountHolderJson(Account account) {
        Set<AccountHolder> accountHolders = account.getAccountHolder();

        JSONArray array = new JSONArray();
        for (AccountHolder accountHolder : accountHolders) {
            JSONObject json = new JSONObject();
            json.put("id", accountHolder.getId());
            json.put("holderName", accountHolder.getHolderName());
            array.put(json);
        }
        return array;
    }

    private JSONArray getAccountsMovementsJson(Account account) {
        Set<Movement> movements = account.getMovements();

        JSONArray array = new JSONArray();
        for (Movement movement : movements) {
            JSONObject json = new JSONObject();
            json.put("id", movement.getId());
            json.put("date", movement.getDate());
            json.put("value", movement.getValue());
            json.put("description", movement.getDescription());
            array.put(json);
        }
        return array;
    }
}
