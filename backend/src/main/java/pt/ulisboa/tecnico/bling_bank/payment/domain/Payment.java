package pt.tecnico.ulisboa.bling_bank.payment.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import pt.tecnico.ulisboa.bling_bank.account.domain.Account;

import java.util.Date;

@Getter
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

    public Payment(Account account, Date date, int amount, String currencyType, String description, int requiredApprovals) {
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
        if (this.approvedApprovals < this.requiredApprovals) {
            this.accepted = false;
        }
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequiredApprovals() {
        return requiredApprovals;
    }

    public void setRequiredApprovals(int requiredApprovals) {
        this.requiredApprovals = requiredApprovals;
    }

    public int getApprovedApprovals() {
        return approvedApprovals;
    }

    public void setApprovedApprovals(int approvedApprovals) {
        this.approvedApprovals = approvedApprovals;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", account=" + account +
                ", date=" + date +
                ", amount=" + amount +
                ", currencyType='" + currencyType + '\'' +
                ", description='" + description + '\'' +
                ", requiredApprovals=" + requiredApprovals +
                ", approvedApprovals=" + approvedApprovals +
                ", accepted=" + accepted +
                '}';
    }
}