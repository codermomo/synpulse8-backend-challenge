package hk.ust.connect.klmoaa.s8challenge.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hk.ust.connect.klmoaa.s8challenge.models.Account;
import hk.ust.connect.klmoaa.s8challenge.models.Client;
import hk.ust.connect.klmoaa.s8challenge.models.Transaction;
import hk.ust.connect.klmoaa.s8challenge.utils.parser.TransactionParser;
import hk.ust.connect.klmoaa.s8challenge.utils.parser.TransactionRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

@Service
public class PublishRecordService {

    private Logger logger = LoggerFactory.getLogger(PublishRecordService.class);
    @Autowired
    private KafkaTemplate<String, String> accountKafkaTemplate;

    public void publishAccounts(String filename) {

        String topicName = "accounts";
        logger.info(String.format("Start publishing account records to Kafka topic %s", topicName));

        try {
            String json = Files.readString(
                    Paths.get(filename));

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, ArrayList<Account>> clients = objectMapper.readValue(
                    json, new TypeReference<Map<String, ArrayList<Account>>>() {});

            for (var entry: clients.entrySet()) {
                accountKafkaTemplate.send(topicName, entry.getKey(), objectMapper.writeValueAsString(entry));
            }

            logger.info(String.format("Published account records to Kafka topic %s successfully", topicName));
        } catch (Exception e) {
            logger.error("Encountered exception: %s", e);
        }
    }

    public void publishTransactions(String filename) {

        String prefix = "transactions";
        logger.info("Start publishing transaction records to various Kafka topics");

        try {
            String json = Files.readString(
                    Paths.get(filename));

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Map<String, Map<String, TransactionRecord>> transactionTopics = objectMapper.readValue(
                    json, new TypeReference<Map<String, Map<String, TransactionRecord>>>() {});

            for (var accountMonthlyTransactions: transactionTopics.entrySet()) {
                for (var transactionEntry: accountMonthlyTransactions.getValue().entrySet()) {
                    accountKafkaTemplate.send(
                            (prefix+"|"+accountMonthlyTransactions.getKey()).replace("|", "."),
                            transactionEntry.getKey(),
                            objectMapper.writeValueAsString(transactionEntry)
                    );
                }
            }

            logger.info("Published transaction records to various Kafka topics successfully");

        } catch (Exception e) {
            logger.error("Encountered exception: %s", e);
        }
    }
}
