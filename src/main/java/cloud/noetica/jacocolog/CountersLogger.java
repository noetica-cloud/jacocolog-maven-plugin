package cloud.noetica.jacocolog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

public class CountersLogger {

    private static final NumberFormat NUMBER_FORMAT_LOCALE = NumberFormat.getInstance(Locale.getDefault());

    private final Log log;

    private final int digits;

    private final Set<CounterEntity> counters;

    public CountersLogger(Log log, int digits, Set<CounterEntity> counters) {
        this.log = log;
        this.digits = digits;
        this.counters = counters;
    }

    public void log(JacocoCounters coverage) {
        log.info("Test Coverage:");
        for (CounterEntity type : counters) {
            ICounter counter = coverage.getCounters().get(type);
            String value = Optional.of(counter)
                    .filter(c -> c.getTotalCount() > 0)
                    .map(c -> format(c.getCoveredRatio()) + "%")
                    .orElse("unknown");
            log.info("    - " + toCounterString(type) + " Coverage: " + value);
            log.debug("     Covered : " + counter.getCoveredCount() + ", Total : " + counter.getTotalCount());
        }
    }

    private String format(final double value) {
        return NUMBER_FORMAT_LOCALE.format(
                BigDecimal.valueOf(value * 100).setScale(digits, RoundingMode.FLOOR));
    }

    private String toCounterString(CounterEntity counter) {
        switch (counter) {
            case CLASS:
                return "Class";
            case METHOD:
                return "Method";
            case BRANCH:
                return "Branch";
            case LINE:
                return "Line";
            case INSTRUCTION:
                return "Instruction";
            case COMPLEXITY:
                return "Complexity";
            default:
                return "Unknown";
        }
    }
}
