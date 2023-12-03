package pt.ulisboa.tecnico.bling_bank.account.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;
import pt.ulisboa.tecnico.bling_bank.account.domain.Movement;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountRepository;
import pt.ulisboa.tecnico.bling_bank.account.repository.MovementRepository;
import pt.ulisboa.tecnico.bling_bank.util.BlingBankException;
import pt.ulisboa.tecnico.bling_bank.util.ErrorMessage;

import java.util.Date;

@Service
public class MovementService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovementRepository movementRepository;

    public String createMovement(Date date, int value, String description, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        Movement movement = new Movement(date, value, description, account);
        movementRepository.save(movement);

        JSONObject json = new JSONObject();
        json.put("movement", getMovementJson(movement));
        json.put("account", getAccountJson(movement.getAccount()));

        return json.toString();
    }

    public String getMovement(Long id) {
        Movement movement = movementRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.MOVEMENT_NOT_FOUND));

        JSONObject json = new JSONObject();
        json.put("movement", getMovementJson(movement));
        json.put("account", getAccountJson(movement.getAccount()));

        return json.toString();
    }

    public String getMovementByAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        JSONObject json = new JSONObject();
        JSONArray movements = new JSONArray();
        for (Movement movement : account.getMovements()) {
            movements.put(getMovementJson(movement));
        }

        json.put("movements", movements);
        json.put("account", getAccountJson(account));

        return json.toString();
    }

    private JSONObject getMovementJson(Movement movement) {
        JSONObject json = new JSONObject();

        JSONObject m = new JSONObject();
        m.put("id", movement.getId());
        m.put("date", movement.getDate());
        m.put("value", movement.getValue());
        m.put("description", movement.getDescription());
        json.put("movement", m);

        return json;
    }

    private JSONObject getAccountJson(Account account) {
        JSONObject json = new JSONObject();
        json.put("id", account.getId());
        json.put("balance", account.getBalance());
        json.put("currencyType", account.getCurrencyType());
        json.put("accountHolder", getAccountHolderJson(account));
        return json;
    }

    private JSONArray getAccountHolderJson(Account account) {
        JSONArray array = new JSONArray();
        for (AccountHolder accountHolder : account.getAccountHolder()) {
            JSONObject json = new JSONObject();
            json.put("id", accountHolder.getId());
            json.put("holderName", accountHolder.getHolderName());
            array.put(json);
        }
        return array;
    }

}
