package hk.ust.connect.klmoaa.s8challenge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Money (
        @JsonProperty("amount") double amount,
        @JsonProperty("currency") Currency currency) {
}
