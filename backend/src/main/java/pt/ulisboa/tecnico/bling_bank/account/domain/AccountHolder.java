package pt.ulisboa.tecnico.bling_bank.account.domain;


import jakarta.persistence.*;
import lombok.Data

@Data
@Entity
@Table(name = "accountHolders")
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountHolderName;

    @ManyToMany(mappedBy = "accountHolder")
    private Set<Account> accounts;

    public AccountHolder() {
    }

    public AccountHolder(String accountHolderName, Set<Account> accounts) {
        this.accountHolderName = accountHolderName;
        this.accounts = accounts;
    }
}