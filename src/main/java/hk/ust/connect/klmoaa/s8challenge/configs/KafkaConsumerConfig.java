package hk.ust.connect.klmoaa.s8challenge.configs;

import hk.ust.connect.klmoaa.s8challenge.models.Account;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // https://stackoverflow.com/questions/64723298/kafka-consumer-receives-message-if-set-group-id-to-none-but-it-doesnt-receive
    @Value("${random.uuid}")
    private String groupId;
//    @Value("${random.uuid}")
//    private String groupId2;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

//    @Bean
//    public Map<String, Object> accountConsumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId2);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        return props;
//    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Consumer<String, String> consumer() {
        Consumer<String, String> consumer = consumerFactory().createConsumer();
        // consumer.subscribe(Arrays.asList("transactions"));
        return consumer;
    }

//    @Bean
//    public ConsumerFactory<String, String> accountConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(accountConsumerConfigs());
//    }
//
//    @Bean
//    public Consumer<String, String> accountConsumer() {
//        Consumer<String, String> consumer = accountConsumerFactory().createConsumer();
//        consumer.subscribe(Arrays.asList("accounts"));
//        return consumer;
//    }
}