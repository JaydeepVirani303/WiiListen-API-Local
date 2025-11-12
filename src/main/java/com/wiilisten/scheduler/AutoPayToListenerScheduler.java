package com.wiilisten.scheduler;

import com.wiilisten.controller.api.admin.ApiV1AdminListenerController;
import com.wiilisten.entity.ListenerPaySchedulerConfig;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.repo.ListenerPaySchedulerConfigRepository;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.utils.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoPayToListenerScheduler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoPayToListenerScheduler.class);

    @Autowired
    ApiV1AdminListenerController apiV1AdminListener;

    @Autowired
    ServiceRegistry serviceRegistry;

    @Autowired
    ListenerPaySchedulerConfigRepository listenerPaySchedulerConfigRepository;

    @Scheduled(cron = "0 0 2 * * MON")
    public void processWeeklyPayouts() {
        ListenerPaySchedulerConfig activeConfig =
                listenerPaySchedulerConfigRepository.findByActiveTrue();
        if (!activeConfig.isActive()) {
            LOGGER.info("Weekly payout scheduler is disabled. Skipping job.");
            return;
        }

        LOGGER.info("Starting weekly payout job...");
        processPayouts(activeConfig.getFrequency());
    }

    // 🗓 Bi-weekly payout - 1st & 15th of every month at 2 AM
    @Scheduled(cron = "0 0 2 1,15 * *")
    public void processBiWeeklyPayouts() {
        ListenerPaySchedulerConfig activeConfig =
                listenerPaySchedulerConfigRepository.findByActiveTrue();
        if (!activeConfig.isActive()) {
            LOGGER.info("Bi-weekly payout scheduler is disabled. Skipping job.");
            return;
        }

        LOGGER.info("Starting bi-weekly payout job...");
        processPayouts(activeConfig.getFrequency());
    }

    private void processPayouts(String type) {
        List<ListenerProfile> listeners = serviceRegistry
                .getListenerProfileService()
                .getAllListenersWithEarnings();

        if (listeners == null || listeners.isEmpty()) {
            LOGGER.info("No listeners with earnings found for {} payout.", type);
            return;
        }

        LOGGER.info("Processing {} payout for {} listeners...", type, listeners.size());

        for (ListenerProfile listener : listeners) {
            IdRequestDto idRequestDto = new IdRequestDto();
            idRequestDto.setId(listener.getId());
            apiV1AdminListener.payEarningsManually(idRequestDto);
        }

        LOGGER.info("{} payout job completed.", type);
    }
}
