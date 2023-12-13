package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "account_holders")
public class AccountHolder implements UserDetails {

    @Id
    @Column(name = "holder_name")
    private String holderName;

    private String holderPassword;

    @ManyToMany()
    @JoinTable(
        name = "accountHolders_accounts",
        joinColumns = @JoinColumn(name = "accountHolder_id"),
        inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> accounts;

    @Enumerated(EnumType.STRING)
    private Role role;

    public AccountHolder(String holderName) {
        this.holderName = holderName;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() { return holderPassword; }

    @Override
    public String getUsername() { return holderName; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}