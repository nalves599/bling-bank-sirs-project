package pt.ulisboa.tecnico.bling_bank.account.domain;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int balance;

    private String currencyType;

    public Account() {
    }

    public Account(int balance, String currencyType) {
        this.balance = balance;
    this.currencyType = currencyType;
    }
}