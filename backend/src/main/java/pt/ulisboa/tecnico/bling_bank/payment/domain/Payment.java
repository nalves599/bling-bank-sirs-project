package pt.ulisboa.tecnico.bling_bank.payment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import pt.ulisboa.tecnico.bling_bank.account.domain.Account;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    private Account account;

    @Column(name = "date")
    private Date date; // DD/MM/YYYY

    @Column(name = "amount")
    private int amount; // in cents

    @Column(name = "currencyType")
    private String currencyType;

    @Column(name = "description")
    private String description;

    @Column(name = "requiredApprovals")
    private int requiredApprovals;

    @Column(name = "approvedApprovals")
    private int approvedApprovals;

    @Column(name = "accepted")
    private boolean accepted;

    public Payment(Account account, Date date, int amount, String currencyType, String description,
        int requiredApprovals) {
        this.account = account;
        this.date = date;
        this.amount = amount;
        this.currencyType = currencyType;
        this.description = description;
        this.requiredApprovals = requiredApprovals;
        this.approvedApprovals = 1; // when a payment is created, the creator approves it
        if (this.approvedApprovals == this.requiredApprovals) {
            this.accepted = true;
        } else {
            this.accepted = false;
        }
    }

    public void addApproval() {
        this.approvedApprovals++;
        if (this.approvedApprovals == this.requiredApprovals) {
            this.accepted = true;
        }
    }

    public void removeApproval() {
        this.approvedApprovals--;
    }
}