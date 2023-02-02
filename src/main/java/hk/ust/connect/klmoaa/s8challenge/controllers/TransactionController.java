package hk.ust.connect.klmoaa.s8challenge.controllers;

import hk.ust.connect.klmoaa.s8challenge.models.Client;
import hk.ust.connect.klmoaa.s8challenge.services.TokenService;
import hk.ust.connect.klmoaa.s8challenge.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Scope("request")
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TokenService tokenService;

    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getPaginatedListByMonth(
            @RequestParam(value = "year") int year,
            @RequestParam(value = "month") int month,
            @RequestParam(value = "page_size", required = false, defaultValue = "100") int pageSize,
            @RequestParam(value = "base_currency", required = false, defaultValue = "USD") String baseCurrencyString,
            @RequestHeader("Authorization") String tokenString
    ) {
        // Prerequisite: Authenticated (passed the filterChain), so that client.id() is valid
        // The client is supposed to retrieve the transaction records of all his/her accounts (but not others')

        // Decode JWT in header
        String token = tokenString.substring("Bearer ".length());
        Client client = new Client(tokenService.getClientIdFromToken(token));

        return transactionService.getPaginatedListByMonth(client, year, month, pageSize, baseCurrencyString);
    }
}
