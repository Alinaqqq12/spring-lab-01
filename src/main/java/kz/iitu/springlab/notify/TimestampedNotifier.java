package kz.iitu.springlab.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component("timestamped")
@Order(3)
public class TimestampedNotifier implements Notifier {

    private static final Logger log =
            LoggerFactory.getLogger(TimestampedNotifier.class);

    @PostConstruct
    void init() {
        log.info("TimestampedNotifier initialized");
    }

    @Override
    public String send(String message) {
        return Instant.now().toString() + " " + message;
    }

    @Override
    public String channel() {
        return "timestamped";
    }
}