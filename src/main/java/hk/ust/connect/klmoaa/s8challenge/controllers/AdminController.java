package hk.ust.connect.klmoaa.s8challenge.controllers;

import hk.ust.connect.klmoaa.s8challenge.constants.HttpStatusCode;
import hk.ust.connect.klmoaa.s8challenge.services.PublishRecordService;
import hk.ust.connect.klmoaa.s8challenge.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("request")
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private PublishRecordService publishRecordService;

    @GetMapping("/token/get")
    public ResponseEntity<String> getToken(@RequestParam(value = "client_id") String clientId) {
        try {
            return ResponseEntity.ok(tokenService.getToken(clientId));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatusCode.ResourceNotFound).body("No token generated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.InternalServerError).body("No token generated.");
        }
    }

    @GetMapping("/transactions/publish")
    public ResponseEntity<String> publishRecordsFromFile() {
        publishRecordService.publishAccounts("C:\\Users\\Alex\\Documents\\Code\\synpulse8-data-generation\\accounts_1_5.json");
        publishRecordService.publishAccounts("C:\\Users\\Alex\\Documents\\Code\\synpulse8-data-generation\\accounts_6_10.json");
        publishRecordService.publishTransactions("C:\\Users\\Alex\\Documents\\Code\\synpulse8-data-generation\\transactions_1_5.json");
        publishRecordService.publishTransactions("C:\\Users\\Alex\\Documents\\Code\\synpulse8-data-generation\\transactions_6_10.json");
        return ResponseEntity.ok("Published to Kafka");
    }
}