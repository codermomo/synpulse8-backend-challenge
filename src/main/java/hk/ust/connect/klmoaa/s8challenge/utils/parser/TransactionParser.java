package hk.ust.connect.klmoaa.s8challenge.utils.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hk.ust.connect.klmoaa.s8challenge.models.Account;
import hk.ust.connect.klmoaa.s8challenge.models.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class TransactionParser {

    private static Logger logger = LoggerFactory.getLogger(TransactionParser.class);

    public static Transaction parse(ConsumerRecord<String, String> record, Account account) {
        Transaction result = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Map<String, TransactionRecord> transactions = objectMapper.readValue(
                    record.value(), new TypeReference<Map<String, TransactionRecord>>() {});
            result = transactions.get(record.key()).convert();
            result.setId(UUID.fromString(record.key()));
            // register account to the transaction (if the account is owned by the client)
            result.setAccount(account);
        } catch (Exception e) {
            logger.error(String.format("Encountered exception: %s", e));
        }
        return result;
    }
}
