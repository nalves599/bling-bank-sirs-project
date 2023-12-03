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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date; // DD/MM/YYYY

    private int value; // in cents

    private String description;

    @ManyToOne(optional = false)
    private Account account;

    public Movement(Date date, int value, String description, Account account) {
        this.date = date;
        this.value = value;
        this.description = description;
        this.account = account;
    }
}