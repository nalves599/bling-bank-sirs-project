package pt.ulisboa.tecnico.bling_bank.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "auth_holders",
        indexes = {
                @Index(name = "auth_holders_indx_0", columnList = "username")
        })
public abstract class AuthUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private AccountHolder user;

    private String password;

    @Column(unique = true)
    private String username;

    protected AuthUser() {
    }

    protected AuthUser(AccountHolder user, String username) {
        setUser(user);
        setUsername(username);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public AccountHolder getUser() {
        return user;
    }

    public void setUser(AccountHolder user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.toLowerCase() : null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAuthUser(this);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void remove() {
        user.setAuthUser(null);
        setUser(null);
    }
}