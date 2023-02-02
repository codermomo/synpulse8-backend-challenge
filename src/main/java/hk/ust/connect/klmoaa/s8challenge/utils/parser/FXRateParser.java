package hk.ust.connect.klmoaa.s8challenge.utils.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hk.ust.connect.klmoaa.s8challenge.models.Currency;
import hk.ust.connect.klmoaa.s8challenge.models.FXRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class FXRateParser {

    private static Logger logger = LoggerFactory.getLogger(FXRateParser.class);

    public Map<LocalDate, Map<String, FXRate>> parse(String json, Currency base) {
        Map<LocalDate, Map<String, FXRate>> results = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Map<String, Object> map = objectMapper.readValue(
                    json, new TypeReference<Map<String, Object>>() {});

            Map<String, Map<String, Number>> monthRates = (Map<String, Map<String, Number>>) map.get("rates");
            results = new HashMap<>();
            for (var dayRates: monthRates.entrySet()) {
                LocalDate date = LocalDate.parse(dayRates.getKey());
                results.put(date, new HashMap<String, FXRate>());

                for (var singlePairRate: dayRates.getValue().entrySet()) {
                    results.get(date).put(
                            singlePairRate.getKey(),
                            new FXRate(Currency.valueOf(singlePairRate.getKey()), base, date,
                                    Double.valueOf(singlePairRate.getValue().toString()))
                    );
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Encountered exception: %s", e));
        }

        return results;
    }
}
