package hk.ust.connect.klmoaa.s8challenge.services;

import hk.ust.connect.klmoaa.s8challenge.constants.HttpStatusCode;
import hk.ust.connect.klmoaa.s8challenge.exceptions.ExternalAPIException;
import hk.ust.connect.klmoaa.s8challenge.exceptions.KafkaException;
import hk.ust.connect.klmoaa.s8challenge.models.*;
import hk.ust.connect.klmoaa.s8challenge.utils.parser.AccountParser;
import hk.ust.connect.klmoaa.s8challenge.utils.parser.TransactionParser;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

    private Logger logger = LoggerFactory.getLogger(TransactionService.class);
    @Autowired
    private ValidationService validationService;
    @Autowired
    private FXRateService fxRateService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private Consumer<String, String> consumer;

    /*
    Entry point of API.
     */
    public ResponseEntity<Map<String, Object>>  getPaginatedListByMonth(
            Client client, int year, int month, int pageSize, String baseCurrencyString
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validate and prepare parameter
            Map<String, LocalDate> period = validationService.preparePeriodByMonth(year, month);
            pageSize = validationService.preparePageSize(pageSize);
            Currency baseCurrency = validationService.prepareCurrency(baseCurrencyString);

            // Get accounts and transactions
            ArrayList<Transaction> transactions = getByMonth(client, period.get("start"), period.get("end"));

            // Transform to paginated list
            Map<LocalDate, Map<String, FXRate>> monthRates =
                    fxRateService.queryFXRates(
                            period.get("start"), period.get("end"), client.getAllCurrenciesUsed(), baseCurrency);
            ArrayList<Page> pages = Page.pageFactory(pageSize, transactions, monthRates);

            // Build response body
            response.put("client_id", client.id());
            response.put("num_pages", pages.size());
            response.put("pages", pages);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", e);
            return ResponseEntity.status(HttpStatusCode.BadRequest).body(response);
        } catch (KafkaException e) {
            response.put("error", e);
            return ResponseEntity.status(HttpStatusCode.InternalServerError).body(response);
        } catch (ExternalAPIException e) {
            response.put("error", e);
            return ResponseEntity.status(HttpStatusCode.BadGateway).body(response);
        }
    }

    /*
    It will first retrieve the list of accounts of the client, then get the transactions.
     */
    public ArrayList<Transaction> getByMonth(
            Client client, LocalDate startDateOfMonth, LocalDate endDateOfMonth)
            throws KafkaException {

        try {
            if (client.accounts().isEmpty()) {
                // poll account records from Kafka and
                // register accounts to the client object
                client.addAccounts(getAllAccounts(client));
            }

            return getTransactionsByClient(client, startDateOfMonth, endDateOfMonth);
        } catch (Exception cause) {
            logger.error(String.format("Encountered exception: %s", cause));
            throw new KafkaException(cause);
        }
    }

    /*
    Given a client, get all his/ her accounts and register itself to them if necessary.
     */
    public ArrayList<Account> getAllAccounts(Client client) {

        logger.info(String.format("Start getting all accounts for client %s", client.id()));

        ArrayList<ConsumerRecord<String, String>> records = pollAccounts(
                consumer,"accounts", client.id());

        // parse and convert the type of the list of records into list of accounts
        ArrayList<Account> accounts = new ArrayList<>();
        records.forEach(record ->
                accounts.addAll(AccountParser.parse(record))
        );

        // register client to the accounts
        for (var account: accounts) {
            account.setOwner(client);
        }

        logger.info(String.format("Client %s has account(s): [%s]", client.id(), accounts));

        return accounts;
    }

    /*
    Given a client and a period, retrieve all transactions associated with the client's accounts.
     */
    public ArrayList<Transaction> getTransactionsByClient(Client client, LocalDate startDate, LocalDate endDate)
            throws KafkaException {

        logger.info(String.format("Start getting all transaction for client %s from %s to %s",
                client.id(), startDate.toString(), endDate.toString()));

        if (client.accounts().isEmpty()) {
            logger.error(String.format("Exception: The given client %s has no accounts", client.id()));
            throw new KafkaException("The given client has no accounts");
        }

        ArrayList<Transaction> results = new ArrayList<>();
        LocalDate[] period = {startDate, endDate};

        for (var account: client.accounts()) {
            if (account.transactions().isEmpty()) {
                ArrayList<Transaction> transactions = pollTransactionsByAccount(consumer, "transactions",
                        account, period);
                account.addTransactions(transactions);
                if (account.currency() == null && !account.transactions().isEmpty()) {
                    account.setCurrency(account.transactions().get(0).money().currency());
                }
            }
            results.addAll(account.transactions());
        }

        logger.info(String.format("Client %s has %d transactions in total", client.id(), results.size()));

        return results;
    }

    /*
    Given a client, poll all his/her accounts from Kafka.
     */
    public ArrayList<ConsumerRecord<String, String>> pollAccounts(
            Consumer<String, String> consumer, String topicName, String clientId) {

        consumer.subscribe(Arrays.asList(topicName));
        consumer.seekToBeginning(consumer.assignment());
        logger.info(String.format("Start polling accounts of client %s from Kafka topic %s", clientId, topicName));

        ArrayList<ConsumerRecord<String, String>> results = new ArrayList<>();
        ConsumerRecords<String, String> records = null;

        do {
            // extract
            records = pollOnce(consumer);
            // filter
            records.records(topicName).forEach(record -> {
                if (record.key().equals(clientId)) {
                    results.add(record);
                }
            });
        } while (!records.isEmpty());

        logger.info(String.format("Polled accounts of client %s from Kafka topic %s", clientId, topicName));
        consumer.unsubscribe();

        return results;
    }

    /*
    Given an account, poll all its transaction record within the month.
     */
    public ArrayList<Transaction> pollTransactionsByAccount(
            Consumer<String, String> consumer, String prefix, Account account, LocalDate[] period) {

        ArrayList<Transaction> results = new ArrayList<>();
        ArrayList<String> topicName = new ArrayList<String>();
        topicName.add(
                String.format("%s.%s.%d.%d", prefix, account.IBAN(), period[0].getYear(), period[0].getMonthValue())
        );

        consumer.subscribe(topicName);
        consumer.seekToBeginning(consumer.assignment());
        logger.info(String.format(
                "Start polling transactions of account %s from Kafka topic %s", account.IBAN(), topicName.get(0)));

        ConsumerRecords<String, String> records = null;
        do {
            // extract
            records = pollOnce(consumer);
            // parse and filter
            for (var record: records.records(topicName.get(0))) {
                Transaction transaction = TransactionParser.parse(record, account);
                if (!transaction.date().isAfter(period[1]) && !transaction.date().isBefore(period[0])) {
                    results.add(transaction);
                }
            }
        } while (!records.isEmpty());

        logger.info(String.format(
                "Polled transactions of account %s from Kafka topic %s", account.IBAN(), topicName.get(0)));
        consumer.unsubscribe();

        return results;
    }

    /*
    Poll Kafka messages from topics subscribed by consumer.
     */
    public ConsumerRecords<String, String> pollOnce(Consumer<String, String> consumer) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        return records;
    }
}
