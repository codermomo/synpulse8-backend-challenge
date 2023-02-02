package hk.ust.connect.klmoaa.s8challenge.models;

import java.time.LocalDate;

// e.g. USD/JPY = 100 means 1 USD = 100 JPY,
// where USD is the base currency, and JPY is the quote currency
public record FXRate (Currency quote, Currency base, LocalDate date, double rate) {
}
