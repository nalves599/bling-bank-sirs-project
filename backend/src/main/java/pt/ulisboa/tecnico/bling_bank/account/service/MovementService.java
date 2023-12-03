package pt.ulisboa.tecnico.bling_bank.account.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;
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

    public JSONObject createMovement(Date date, int value, String description, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        Movement movement = new Movement(date, value, description, account);
        movementRepository.save(movement);
        return getJsonObject(movement);
    }

    public JSONObject getMovement(Long id) {
        Movement movement = movementRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.MOVEMENT_NOT_FOUND));

        return getJsonObject(movement);
    }

    public JSONObject getMovementByAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        JSONObject json = new JSONObject();
        json.put("movements", account.getMovements());
        return json;
    }

    private JSONObject getJsonObject(Movement movement) {
        JSONObject json = new JSONObject();
        json.put("id", movement.getId());
        json.put("date", movement.getDate());
        json.put("value", movement.getValue());
        json.put("description", movement.getDescription());
        json.put("account", movement.getAccount());
        return json;
    }

}
