package hk.ust.connect.klmoaa.s8challenge.utils.parser;

import hk.ust.connect.klmoaa.s8challenge.models.Currency;
import hk.ust.connect.klmoaa.s8challenge.models.Money;
import hk.ust.connect.klmoaa.s8challenge.models.Transaction;

import java.time.LocalDate;

public class TransactionRecord {
    public String IBAN; // not recommended to instantiate account from a transaction record
    public double amount;
    public String currency;
    public String date;
    public String description;

    public Transaction convert() {
        return new Transaction(new Money(amount, Currency.valueOf(currency)), LocalDate.parse(date), description);
    }

    // accessors
    public String IBAN() {
        return IBAN;
    }
}