package hk.ust.connect.klmoaa.s8challenge.services;

import hk.ust.connect.klmoaa.s8challenge.exceptions.ExternalAPIException;
import hk.ust.connect.klmoaa.s8challenge.models.Currency;
import hk.ust.connect.klmoaa.s8challenge.models.FXRate;
import hk.ust.connect.klmoaa.s8challenge.utils.parser.FXRateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FXRateService {

    private Logger logger = LoggerFactory.getLogger(FXRateService.class);
    private final String endpoint = "https://api.exchangerate.host/timeseries?";
    @Autowired
    private FXRateParser parser;

    @Bean
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private String InvokeEndpoint(
            LocalDate startDate, LocalDate endDate, ArrayList<Currency> currencies, Currency base) {

        String uri = String.format(
                "%sstart_date=%s&end_date=%s&base=%s&symbols=%s",
                endpoint, startDate.toString(), endDate.toString(), base.toString(),
                currencies.stream().map(Objects::toString).collect(Collectors.joining(",")));
        logger.info(String.format("Trying to invoke endpoint with URL %s", uri));

        String results = restTemplate().getForObject(uri, String.class);
        logger.info(String.format("Received response from URL %s: %s", uri, results));

        return results;
    }

    private Map<LocalDate, Map<String, FXRate>> parseEndpointResult(String json, Currency base) {

        logger.info(String.format("Trying to parse response: %s", json));

        Map<LocalDate, Map<String, FXRate>> results = parser.parse(json, base);

        logger.info(String.format("Parsed response %s successfully", json));

        return results;
    }

    public Map<LocalDate, Map<String, FXRate>> queryFXRates(
            LocalDate startDate, LocalDate endDate, ArrayList<Currency> currencies, Currency base)
            throws ExternalAPIException {

        try {
            Map<LocalDate, Map<String, FXRate>> results = parseEndpointResult(
                    InvokeEndpoint(startDate, endDate, currencies, base), base);

            // minimal null check to validate response from external api
            if (results == null ||
                    results.get(startDate) == null ||
                    results.get(startDate).get(currencies.get(0).toString()) == null
            ) {
                logger.error(String.format(
                        "Schema of external API response changed or request failed, received response: %s", results));
                throw new ExternalAPIException("Schema of external API response changed or request failed");
            }

            return results;

        } catch (Exception cause) {
            logger.error(String.format("Encountered exception: %s", cause));
            throw new ExternalAPIException(cause);
        }
    }

}
