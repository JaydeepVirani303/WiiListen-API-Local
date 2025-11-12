package com.wiilisten.repo;

import com.wiilisten.entity.ListenerPaySchedulerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ListenerPaySchedulerConfigRepository extends JpaRepository<ListenerPaySchedulerConfig, Long> {

    /**
     * Get the currently active scheduler configuration.
     */
    ListenerPaySchedulerConfig findByActiveTrue();

    /**
     * Deactivate all configurations (set is_active = false).
     */
    @Modifying
    @Transactional
    @Query("UPDATE ListenerPaySchedulerConfig c SET c.active = false WHERE c.active = true")
    void deactivateAll();

    /**
     * Activate a specific configuration by ID.
     */
    @Modifying
    @Transactional
    @Query("UPDATE ListenerPaySchedulerConfig c SET c.active = true WHERE c.id = :id")
    void activateById(Long id);

    /**
     * Get a configuration by type and frequency.
     * Example: findByTypeAndFrequency("AUTOMATIC", "WEEKLY")
     * For MANUAL, frequency can be null.
     */
    ListenerPaySchedulerConfig findByTypeAndFrequency(String type, String frequency);

    Optional<ListenerPaySchedulerConfig> findById(Long aLong);

    Optional<ListenerPaySchedulerConfig> findByType(String type);

}
