package pt.ulisboa.tecnico.bling_bank.payment.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.bling_bank.account.domain.Account;
import pt.ulisboa.tecnico.bling_bank.account.domain.Movement;
import pt.ulisboa.tecnico.bling_bank.payment.domain.Payment;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountRepository;
import pt.ulisboa.tecnico.bling_bank.payment.repository.PaymentRepository;
import pt.ulisboa.tecnico.bling_bank.util.BlingBankException;
import pt.ulisboa.tecnico.bling_bank.util.ErrorMessage;

import java.util.Set;
import java.util.Date;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createPayment(long accountId, Date date, int amount, String description) {
        Account account = accountRepository.findById(accountId).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        int requiredApprovals = account.getAccountHolder().size();
        String currencyType = account.getCurrencyType();

        Payment payment = new Payment(account, date, amount, currencyType, description, requiredApprovals);

        return getPaymentJson(payment).toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAccountPayments(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.ACCOUNT_NOT_FOUND));

        Set<Payment> payments = account.getPayments();

        return getAccountPaymentsJson(account).toString();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String approvePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
            () -> new BlingBankException(ErrorMessage.PAYMENT_NOT_FOUND));

        payment.addApproval();

        if (payment.isAccepted()) {
            Account account = payment.getAccount();
            account.setBalance(account.getBalance() - payment.getAmount());
            account.addPayment(payment);
            account.addMovement(new Movement(payment.getDate(), payment.getAmount(), payment.getDescription(),
                account));
        }

        return getPaymentJson(payment).toString();
    }

    private JSONObject getPaymentJson(Payment payment) {
        JSONObject json = new JSONObject();
        json.put("id", payment.getId());
        json.put("account", payment.getAccount().getId());
        json.put("date", payment.getDate().toString());
        json.put("amount", payment.getAmount());
        json.put("currencyType", payment.getCurrencyType());
        json.put("description", payment.getDescription());
        json.put("requiredApprovals", payment.getRequiredApprovals());
        json.put("approvedApprovals", payment.getApprovedApprovals());
        json.put("approved", payment.isAccepted());
        return json;
    }

    private JSONArray getAccountPaymentsJson(Account account) {
        Set<Payment> payments = account.getPayments();

        JSONArray array = new JSONArray();
        for (Payment payment : payments) {
            JSONObject json = getPaymentJson(payment);
            array.put(json);
        }
        return array;
    }
}
