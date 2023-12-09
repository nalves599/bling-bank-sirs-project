package pt.ulisboa.tecnico.bling_bank.account.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountHolderRepository;
import pt.ulisboa.tecnico.bling_bank.util.BlingBankException;
import pt.ulisboa.tecnico.bling_bank.util.ErrorMessage;

@Service
public class AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createAccountHolder(String holderName) {
        if (accountHolderRepository.findById(holderName).isPresent()) {
            throw new BlingBankException(ErrorMessage.ACCOUNT_HOLDER_ALREADY_EXISTS);
        }

        AccountHolder accountHolder = new AccountHolder(holderName);
        accountHolderRepository.save(accountHolder);
        return getAccountHolderJson(accountHolder).toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAccountHolder(String holderName) {
        AccountHolder accountHolder = accountHolderRepository.findById(holderName).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_HOLDER_NOT_FOUND));

        return getAccountHolderJson(accountHolder).toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAccountHolders() {
        JSONObject json = new JSONObject();
        JSONArray accountHolders = new JSONArray();

        for (AccountHolder accountHolder : accountHolderRepository.findAll()) {
            accountHolders.put(getAccountHolderJson(accountHolder));
        }

        json.put("accountHolders", accountHolders);
        return json.toString();
    }

    private JSONObject getAccountHolderJson(AccountHolder accountHolder) {
        JSONObject json = new JSONObject();
        json.put("holderName", accountHolder.getHolderName());
        return json;
    }
}
