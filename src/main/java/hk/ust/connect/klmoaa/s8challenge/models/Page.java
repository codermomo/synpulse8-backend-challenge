package hk.ust.connect.klmoaa.s8challenge.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {

    @JsonProperty("transactions")
    private List<Transaction> transactions;
    @JsonProperty("page_size")
    private int pageSize;
    @JsonIgnore
    private Double debit = 0.0;

    public Page(List<Transaction> transactions, Map<LocalDate, Map<String, FXRate>> monthRates) {
        this.transactions = transactions;
        this.pageSize = transactions.size();

        for (var transaction: transactions) {
            double amount = convertQuoteToBaseAmount(transaction.money(),
                    monthRates.get(transaction.date()).get(transaction.money().currency().toString()));
            this.debit += amount;
        }
    }

    // accessors

    public double getDebit() {
        return debit;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonAnyGetter
    public Map<String, Double> getDebitCredit() {
        Map<String, Double> debitCredit = new HashMap<>();
        if (debit < 0) {
            debitCredit.put("credit", -debit);
        }
        else {
            debitCredit.put("debit", debit);
        }
        return debitCredit;
    }

    // other

    /*
    Convert quote currency to base currency, given that rate is in the format of base/quote.

    e.g. 100 GBP, USD/GBP = 0.78(GBP), i.e. 1 USD = 0.78 GBP. Then 100 GBP = (100/0.78) USD.
     */
    private double convertQuoteToBaseAmount(Money quote, FXRate rate) {
        return quote.amount() / rate.rate();
    }

    /*
    Given page size and other necessary info to ctor, create a list of pages.
     */
    public static ArrayList<Page> pageFactory(
            int pageSize, ArrayList<Transaction> transactions, Map<LocalDate, Map<String, FXRate>> monthRates) {
        ArrayList<Page> pages = new ArrayList<>();
        for (int i = 0; i <= transactions.size()/pageSize; ++i) {
            int startIndex = i * pageSize;
            int endIndex = (i + 1) * pageSize;
            endIndex = endIndex > transactions.size() ? transactions.size() : endIndex;

            pages.add(new Page(transactions.subList(startIndex, endIndex), monthRates));
        }

        return pages;
    }

    public String toString() {
        String result = String.format(
                "Page Size: %d; %s: %,.2f; Transactions:",
                pageSize,
                debit >= 0 ? "debit" : "credit",
                debit >= 0 ? debit : -debit
        );
        for (var transaction: transactions) {
            result += transaction.toString();
        }
        result = "Page[" + result + "]";
        return result;
    }
}
