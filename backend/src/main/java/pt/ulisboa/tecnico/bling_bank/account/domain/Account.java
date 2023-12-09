package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "balance")
    private int balance; // in cents

    @Column(name = "currencyType")
    private String currencyType;

    @ManyToMany(mappedBy = "accounts")
    private Set<AccountHolder> accountHolder;

    @OneToMany
    private Set<Movement> movements;

    @OneToMany
    private Set<Payment> payments;

    public Account(int balance, String currencyType, Set<AccountHolder> accountHolder) {
        this.balance = balance;
        this.currencyType = currencyType;
        this.accountHolder = accountHolder;
        this.movements = Set.of();
        this.payments = Set.of();
    }

    public void addAccountHolder(AccountHolder accountHolder) {
        this.accountHolder.add(accountHolder);
    }

    public void removeAccountHolder(AccountHolder accountHolder) {
        this.accountHolder.remove(accountHolder);
    }

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
    }
}