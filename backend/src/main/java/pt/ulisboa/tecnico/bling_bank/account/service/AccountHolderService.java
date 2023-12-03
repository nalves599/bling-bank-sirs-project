package pt.ulisboa.tecnico.bling_bank.account.service;

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
        if (accountHolderRepository.existsByHolderName(holderName)) {
            throw new BlingBankException(ErrorMessage.ACCOUNT_HOLDER_ALREADY_EXISTS);
        }

        AccountHolder accountHolder = new AccountHolder(holderName);
        accountHolderRepository.save(accountHolder);
        JSONObject json = new JSONObject();
        json.put("id", accountHolder.getId());
        json.put("holderName", accountHolder.getHolderName());
        return json.toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAccountHolder(Long id) {
        AccountHolder accountHolder = accountHolderRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_HOLDER_NOT_FOUND));

        JSONObject json = new JSONObject();
        json.put("id", accountHolder.getId());
        json.put("holderName", accountHolder.getHolderName());
        json.put("accounts", accountHolder.getAccounts());
        return json.toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAccountHolders() {
        JSONObject json = new JSONObject();
        json.put("accountHolders", accountHolderRepository.findAll());
        return json.toString();
    }
}
