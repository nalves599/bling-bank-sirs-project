package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.bling_bank.crypto.IntEncryptor;
import pt.ulisboa.tecnico.bling_bank.crypto.StringEncryptor;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Convert(converter = IntEncryptor.class)
    @Column(name = "balance", columnDefinition = "varchar(255)")
    private int balance; // in cents

    @Convert(converter = StringEncryptor.class)
    @Column(name = "currency_type")
    private String currencyType;

    @ManyToMany(mappedBy = "accounts")
    private Set<AccountHolder> accountHolder;

    @OneToMany
    private Set<Movement> movements;

    public Account(int balance, String currencyType, Set<AccountHolder> accountHolder) {
        this.balance = balance;
        this.currencyType = currencyType;
        this.accountHolder = accountHolder;
        this.movements = Set.of();
    }

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }
}