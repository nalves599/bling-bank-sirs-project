package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "accountHolders")
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String holderName;

    @ManyToMany()
    private Set<Account> accounts;

    public AccountHolder(String holderName) {
        this.holderName = holderName;
        this.accounts = Set.of(new Account(0, "EUR", Set.of(this)));
    }
}