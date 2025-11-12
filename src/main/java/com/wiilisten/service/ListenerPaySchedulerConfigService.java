package com.wiilisten.service;

import com.wiilisten.entity.ListenerPaySchedulerConfig;

import java.util.List;
import java.util.Optional;

public interface ListenerPaySchedulerConfigService {

    ListenerPaySchedulerConfig getActiveConfig();

    void activateConfig(Long id);

    Optional<ListenerPaySchedulerConfig> findById(Long id);

    ListenerPaySchedulerConfig save(ListenerPaySchedulerConfig config);

    ListenerPaySchedulerConfig findByTypeAndFrequency(String type, String frequency);

    ListenerPaySchedulerConfig findByType(String type);

    List<ListenerPaySchedulerConfig> getAllSchedulerConfigs();


}

