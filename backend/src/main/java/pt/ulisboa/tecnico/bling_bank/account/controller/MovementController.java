package pt.ulisboa.tecnico.bling_bank.account.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.bling_bank.account.service.MovementService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class MovementController {

    @Autowired
    private MovementService movementService;

    @PostMapping("/movements/create")
    public String createMovement(@RequestBody String body) {
        JSONObject json = new JSONObject(body);

        // Get the date string from the JSON object
        String dateString = json.getString("date");

        try {
            // Parse the date string using SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateString);

            int value = json.getInt("value");
            String description = json.getString("description");
            Long accountId = json.getLong("accountId");

            return movementService.createMovement(date, value, description, accountId);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parsing exception as needed
            return "Error parsing date";
        }
    }

    @GetMapping("/movements/{id}")
    public String getMovement(@PathVariable Long id) {
        return movementService.getMovement(id);
    }

    @GetMapping("/movements/account/{id}")
    public String getMovementByAccount(@PathVariable Long id) {
        return movementService.getMovementByAccount(id);
    }
}
