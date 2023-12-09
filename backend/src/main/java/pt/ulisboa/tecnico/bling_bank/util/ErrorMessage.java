package pt.ulisboa.tecnico.bling_bank.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    ACCOUNT_HOLDER_NOT_FOUND("Account holder not found"),
    ACCOUNT_HOLDER_ALREADY_EXISTS("Account holder already exists"),

    ACCOUNT_NOT_FOUND("Account not found"),
    MOVEMENT_NOT_FOUND("Movement not found");

    PAYMENT_NOT_FOUND("Payment not found");

    private final String label;
}
