package pt.ulisboa.tecnico.bling_bank.payment.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.payment.service.PaymentService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    @PostMapping("/payments/create")
    public String createPayment(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        Long accountId = json.getLong("accountId");
        int amount = json.getInt("amount");
        String description = json.getString("description");
        Date date = null;
        try {
            date = formatter.parse(json.getString("date"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return paymentService.createPayment(accountId, date, amount, description);

    }

    @GetMapping("/payments/{id}")
    public String getAccountPayments(@PathVariable Long id) {
        return paymentService.getAccountPayments(id);
    }

    @PostMapping("/payments/{id}/approve")
    public String approvePayment(@PathVariable Long id) {
        return paymentService.approvePayment(id);
    }

    @PostMapping("/payments/{id}/cancel")
    public String cancelPayment(@PathVariable Long id) {
        return paymentService.cancelPayment(id);
    }
}
