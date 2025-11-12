package com.wiilisten.service.impl;

import com.wiilisten.entity.ListenerPaySchedulerConfig;
import com.wiilisten.repo.ListenerPaySchedulerConfigRepository;
import com.wiilisten.service.ListenerPaySchedulerConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ListenerPaySchedulerConfigServiceImpl implements ListenerPaySchedulerConfigService {

    private final ListenerPaySchedulerConfigRepository configRepository;

    public ListenerPaySchedulerConfigServiceImpl(ListenerPaySchedulerConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public ListenerPaySchedulerConfig getActiveConfig() {
        return configRepository.findByActiveTrue();
    }

    @Transactional
    public void activateConfig(Long id) {
        configRepository.deactivateAll();
        configRepository.activateById(id);
    }

    public Optional<ListenerPaySchedulerConfig> findById(Long id) {
        return configRepository.findById(id);
    }

    public ListenerPaySchedulerConfig save(ListenerPaySchedulerConfig config) {
        return configRepository.save(config);
    }

    public ListenerPaySchedulerConfig findByTypeAndFrequency(String type, String frequency) {
        return configRepository.findByTypeAndFrequency(type, frequency);
    }

    public ListenerPaySchedulerConfig findByType(String type) {
        return configRepository.findByType(type).orElse(null);
    }

    @Override
    public List<ListenerPaySchedulerConfig> getAllSchedulerConfigs() {
        return configRepository.findAll();
    }


}

