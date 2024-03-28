package edu.java;

import edu.java.service.LinkUpdater;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class LinkUpdaterScheduler {

    private LinkUpdater linkUpdater;

    @Scheduled(fixedDelayString = "#{@scheduler.interval.toMillis()}")
    public void update() {
        linkUpdater.update();
        log.info("Произошло обновление");

    }
}

