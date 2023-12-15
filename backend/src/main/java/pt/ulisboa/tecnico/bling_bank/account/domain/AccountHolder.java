package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accountHolders")
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "holderName")
    private String holderName;

    @ManyToMany()
    @JoinTable(
        name = "accountHolders_accounts",
        joinColumns = @JoinColumn(name = "accountHolder_id"),
        inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> accounts;

    public AccountHolder(String holderName) {
        this.holderName = holderName;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }
}