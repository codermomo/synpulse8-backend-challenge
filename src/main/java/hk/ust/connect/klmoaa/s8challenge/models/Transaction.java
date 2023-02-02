package hk.ust.connect.klmoaa.s8challenge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {

    @JsonProperty("id")
    private UUID id;
    @JsonUnwrapped
    private Money money;
    @JsonUnwrapped
    private Account account;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("description")
    private String description;

    // constructor
    public Transaction(UUID id, Money money, Account account, LocalDate date, String description) {
        this.id = id;
        this.money = money;
        this.account = account;
        this.date = date;
        this.description = description;
    }

    public Transaction(Money money, LocalDate date, String description) {
        this.money = money;
        this.date = date;
        this.description = description;
    }

    // accessors
    public UUID id() {
        return id;
    }

    public Money money() {
        return money;
    }

    public Account account() {
        return account;
    }

    public LocalDate date() {
        return date;
    }

    public String description() {
        return description;
    }

    String getFormattedTransactionDate() {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    // mutators
    public void registerAccount(Account account) {
        this.account = account;
    }

    public void setId(UUID id) { this.id = id; }

    public void setAccount(Account account) { this.account = account; }

    // other
    public String toString() {
        return String.format("\nTransaction[Date: %s; Amount: %f; Account: %s; Client: %s]",
                date.toString(), money.amount(), account.IBAN(), account.owner().id());
    }
}
