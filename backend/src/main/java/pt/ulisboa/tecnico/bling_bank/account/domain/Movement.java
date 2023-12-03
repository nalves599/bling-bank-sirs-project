package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Data
import java.util.Date;

@Data
@Entity
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private int value;

    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Movement() {
    }

    public Movement(Date date, int value, String description, Account account) {
        this.date = date;
        this.value = value;
        this.description = description;
        this.account = account;
    }
}