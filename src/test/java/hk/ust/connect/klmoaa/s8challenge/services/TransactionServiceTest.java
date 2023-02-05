package hk.ust.connect.klmoaa.s8challenge.services;

//import hk.ust.connect.klmoaa.s8challenge.models.*;
//import hk.ust.connect.klmoaa.s8challenge.models.Currency;
//import hk.ust.connect.klmoaa.s8challenge.utils.parser.TransactionParser;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.common.TopicPartition;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyCollection;
//import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
//
//    // Mocks
//    @InjectMocks
//    private TransactionService service;
//    @Mock
//    private Logger logger;
//    @Mock
//    private ValidationService validationService;
//    @Mock
//    private FXRateService fxRateService;
//    @Mock
//    private KafkaTemplate<String, String> kafkaTemplate;
//    @Mock
//    private Consumer<String, String> consumer;
//
//    // Data
//
//    @Test
//    void getPaginatedListByMonth() {
//    }
//
//    @Test
//    void getByMonth() {
//    }
//
//    @Test
//    void getAllAccounts() {
//    }
//
//    @Test
//    void getTransactionsByClient() {
//    }
//
//    @Test
//    void pollAccounts() {
//    }
//
//    @Test
//    void pollTransactionsByAccount() {
//        // Arrange
//        String prefix = "transactions", clientId = "P-0123456789", IBAN = "CH-1234-1111";
//        Currency currency = Currency.USD;
//        Client owner = new Client(clientId);
//        Account account = new Account(owner, IBAN, currency);
//        owner.addAccount(account);
//        LocalDate[] period = {LocalDate.of(2022, 10, 1), LocalDate.of(2022, 10, 31)};
//
//        Map<TopicPartition, List<ConsumerRecord<String, String>>> map = new HashMap<>();
//        TopicPartition topicPartition = new TopicPartition("mytopic", 0);
//        ConsumerRecord<String, String> dummyRecord = new ConsumerRecord<String, String>(
//                "mytopic", 0, 0, "mykey", "myvalue");
//        map.put(topicPartition, Arrays.asList(dummyRecord));
//        ConsumerRecords<String, String> firstRecords = new ConsumerRecords<>(map);
//
//        doReturn(firstRecords).doReturn(ConsumerRecords.<String, String>empty())
//                .when(consumer).poll(any(Duration.class));
//        UUID uuid = UUID.randomUUID();
//        Transaction transaction = new Transaction(
//                uuid,
//                new Money(12345, Currency.USD),
//                account,
//                period[0],
//                "I win money"
//        );
//
//        try (MockedStatic mocked = mockStatic(TransactionParser.class)) {
//            mocked.when(() -> TransactionParser.parse(dummyRecord, account)).thenReturn(transaction);
//
//            // Act
//            ArrayList<Transaction> results = service.pollTransactionsByAccount(consumer, prefix, account, period);
//        }
//
//    }
//
//    @Test
//    void pollOnce() {
//        // trivial to test
////        Map<TopicPartition, ConsumerRecord<String, String>> map = new HashMap<>();
////        map.put(new TopicPartition("mytopic", 0),
////                new ConsumerRecord<String, String>());
////        ConsumerRecords<String, String> records = new ConsumerRecords<>();
////        when(consumer.poll(any(Duration.class))).thenReturn();
//    }
}