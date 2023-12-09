package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Setter
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
}