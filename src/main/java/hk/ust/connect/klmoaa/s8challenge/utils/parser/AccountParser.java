package hk.ust.connect.klmoaa.s8challenge.utils.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hk.ust.connect.klmoaa.s8challenge.models.Account;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class AccountParser {

    private static Logger logger = LoggerFactory.getLogger(AccountParser.class);

    public static ArrayList<Account> parse(ConsumerRecord<String, String> record) {
        ArrayList<Account> accounts = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // The map only consist one entry
            Map<String, ArrayList<Account>> client = objectMapper.readValue(record.value(), new TypeReference<Map<String, ArrayList<Account>>>() {});
            accounts.addAll(client.get(record.key()));
        } catch (Exception e) {
            logger.error(String.format("Encountered exception: %s", e));
        }
        return accounts;
    }
}
