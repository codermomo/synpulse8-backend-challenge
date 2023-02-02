package hk.ust.connect.klmoaa.s8challenge.services;

import hk.ust.connect.klmoaa.s8challenge.constants.TransactionConstraints;
import hk.ust.connect.klmoaa.s8challenge.models.Currency;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ValidationService {
    public void validateMonth(int month) throws IllegalArgumentException {
        if (month <= 0 || month > 12) {
            throw new IllegalArgumentException(String.format(
                    "Request parameter month is out of bound. Expected within [1, 12] but received %d.", month
            ));
        }
    }

    public void validateYear(int year) throws IllegalArgumentException {
        if (year < TransactionConstraints.StartYearWithTransactionRecord ||
                year > TransactionConstraints.EndYearWithTransactionRecord) {
            throw new IllegalArgumentException(String.format(
                    "Request parameter year is out of bound. Expected within [%d, %d] but received %d.",
                    TransactionConstraints.StartYearWithTransactionRecord,
                    TransactionConstraints.EndYearWithTransactionRecord,
                    year
            ));
        }
    }

    public void validatePageSize(int pageSize) throws IllegalArgumentException {
        if (pageSize <= 0) {
            throw new IllegalArgumentException(String.format(
                    "Request parameter page is out of bound. Expected greater than 0 but received %d.", pageSize
            ));
        }
    }

    public Map<String, LocalDate> preparePeriodByMonth(int year, int month) throws IllegalArgumentException {
        validateMonth(month);
        validateYear(year);

        Map<String, LocalDate> period = new HashMap<>();
        LocalDate startDate = LocalDate.of(year, month, 1);

        period.put("start", startDate);
        period.put("end", startDate.withDayOfMonth(startDate.getMonth().length(startDate.isLeapYear())));

        return period;
    }

    public int preparePageSize(int pageSize) throws IllegalArgumentException {
        validatePageSize(pageSize);
        return pageSize;
    }

    public Currency prepareCurrency(String curencyString) throws IllegalArgumentException {
        return Currency.valueOf(curencyString);
    }
}
