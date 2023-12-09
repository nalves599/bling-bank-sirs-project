package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "date")
    private Date date; // DD/MM/YYYY

    @Column(name = "value")
    private int value; // in cents

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    private Account account;

    public Movement(Date date, int value, String description, Account account) {
        this.date = date;
        this.value = value;
        this.description = description;
        this.account = account;
    }

    public Long getId() { return this.id; }

    public Date getDate() { return this.date; }

    public void setDate(Date date) { this.date = date; }

    public int getValue() { return this.value; }

    public void setValue(int value) { this.value = value; }

    public String getDescription() { return this.description; }

    public void setDescription(String description) { this.description = description; }

    public Account getAccount() { return this.account; }

    public void setAccount(Account account) { this.account = account; }

    @Override
    public String toString() {
        return "Movement{" +
               "id=" + id +
               ", date=" + date +
               ", value=" + value +
               ", description='" + description + '\'' +
               ", account=" + account +
               '}';
    }
}