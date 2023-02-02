package hk.ust.connect.klmoaa.s8challenge.constants;

import java.util.ArrayList;
import java.util.Arrays;

// Stores the constraints of this tech challenge for prototype purpose.
public class TransactionConstraints {

    public static final int StartYearWithTransactionRecord = 2013;
    public static final int EndYearWithTransactionRecord = 2022;
    public static final ArrayList<String> ClientIdSupported = new ArrayList<>(
            Arrays.asList(
                    "P-0000000001",
                    "P-0000000002",
                    "P-0000000003",
                    "P-0000000004",
                    "P-0000000005",
                    "P-0000000006",
                    "P-0000000007",
                    "P-0000000008",
                    "P-0000000009",
                    "P-0000000010"
            )
    );
}
