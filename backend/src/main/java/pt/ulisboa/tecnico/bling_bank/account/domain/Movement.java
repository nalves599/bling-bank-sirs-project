package pt.ulisboa.tecnico.bling_bank.account.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.bling_bank.crypto.IntEncryptor;
import pt.ulisboa.tecnico.bling_bank.crypto.StringEncryptor;

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

    @Convert(converter = IntEncryptor.class)
    @Column(name = "value", columnDefinition = "varchar(255)")
    private int value; // in cents

    @Convert(converter = StringEncryptor.class)
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