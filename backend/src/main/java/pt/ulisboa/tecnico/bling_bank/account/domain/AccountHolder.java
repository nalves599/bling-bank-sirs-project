package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
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

    public long getId() { return this.id; }

    public String getHolderName() { return this.holderName; }

    public void setHolderName(String holderName) { this.holderName = holderName; }

    public Set<Account> getAccounts() { return this.accounts; }

    public void setAccounts(Set<Account> accounts) { this.accounts = accounts; }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    @Override
    public String toString() {
        return "AccountHolder{" +
               "id=" + id +
               ", holderName='" + holderName + '\'' +
               ", accounts=" + accounts +
               '}';
    }
}