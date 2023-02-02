package hk.ust.connect.klmoaa.s8challenge.models;

import java.util.ArrayList;

public record Client (String id, ArrayList<Account> accounts) {
    public Client(String id) {
        this(id, new ArrayList<Account>());
    }

    // mutators

    public void addAccount(Account account) {
        accounts.add(account);
    }
    public void addAccounts(ArrayList<Account> additionalAccounts) {
        accounts.addAll(additionalAccounts);
    }

    // other

    public ArrayList<Currency> getAllCurrenciesUsed() {
        ArrayList<Currency> results = new ArrayList<>();
        accounts.forEach(account -> results.add(account.currency()));
        return results;
    }

    @Override
    public String toString() {
        return "Client[id=" + id + ", number of accounts=" + accounts.size() + "]";
    }
}
