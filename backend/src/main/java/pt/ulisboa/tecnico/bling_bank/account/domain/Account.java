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

    public long getId() { return this.id; }

    public void setBalance(int balance) { this.balance = balance; }

    public int getBalance() { return this.balance; }

    public String getCurrencyType() { return this.currencyType; }

    public void setCurrencyType(String currencyType) { this.currencyType = currencyType; }

    public Set<AccountHolder> getAccountHolders() { return this.accountHolder; }

    public void addAccountHolder(AccountHolder accountHolder) {
        this.accountHolder.add(accountHolder);
    }

    public void removeAccountHolder(AccountHolder accountHolder) {
        this.accountHolder.remove(accountHolder);
    }

    public void setAccountHolder(Set<AccountHolder> accountHolder) { this.accountHolder = accountHolder; }

    public void setMovements(Set<Movement> movements) { this.movements = movements; }

    public Set<Movement> getMovements() { return this.movements; }

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }

    public void setPayments(Set<Payment> payments) { this.payments = payments; }

    public Set<Payment> getPayments() { return this.payments; }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
    }
}