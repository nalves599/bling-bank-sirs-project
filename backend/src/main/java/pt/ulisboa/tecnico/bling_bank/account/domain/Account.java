package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int balance; // in cents

    private String currencyType;

    @ManyToMany
    private Set<AccountHolder> accountHolder;

    @OneToMany
    private Set<Movement> movements;

    public Account(int balance, String currencyType, Set<AccountHolder> accountHolder) {
        this.balance = balance;
        this.currencyType = currencyType;
        this.accountHolder = accountHolder;
        this.movements = Set.of();
    }
}