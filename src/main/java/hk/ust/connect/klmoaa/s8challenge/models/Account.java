package hk.ust.connect.klmoaa.s8challenge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(value="currency", allowSetters = true)
public class Account {
    @JsonIgnore
    private Client owner;
    @JsonProperty("IBAN")
    private String IBAN;
    @JsonProperty("currency")
    private Currency currency;
    @JsonIgnore
    private ArrayList<Transaction> transactions;

    // constructors
    public Account() {
        this.transactions = new ArrayList<>();
    }

    public Account(Client owner, String IBAN, Currency currency, ArrayList<Transaction> transactions) {
        this.owner = owner;
        this.IBAN = IBAN;
        this.currency = currency;
        this.transactions = transactions;
    }

    public Account (String IBAN, Currency currency) {
        this(null, IBAN, currency, new ArrayList<Transaction>());
    }

    public Account (Client owner, String IBAN, Currency currency) {
        this(owner, IBAN, currency, new ArrayList<Transaction>());
    }

    // accessors
    public Client owner() {
        return owner;
    }

    public String IBAN() {
        return IBAN;
    }

    public Currency currency() {
        return currency;
    }

    public ArrayList<Transaction> transactions() {
        return transactions;
    }

    // mutators
    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void addTransactions(ArrayList<Transaction> additionalTransactions) {
        transactions.addAll(additionalTransactions);
    }

    // other
    public String toString() {
        return String.format("Account[IBAN: %s]", IBAN);
    }
}
