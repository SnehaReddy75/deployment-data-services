package com.slack.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Created by sneha.dontireddy on 8/5/18.
 */
@Repository
public interface EventRepository extends JpaRepository<EventEntity, String> {
    List<EventEntity> findAll();

    List<EventEntity> findByEngineer(String engineerName);

    @Query("SELECT e FROM EventEntity e WHERE e.date BETWEEN :startTime AND :endTime ORDER BY e.date ")
    List<EventEntity> getEventsInTimeRange(@Param("startTime") long startTime,@Param("endTime") long endTime);

    @Query("SELECT DISTINCT(e.engineer) FROM EventEntity e WHERE e.date BETWEEN :startTime AND :endTime")
    List<String> getContributors(@Param("startTime") long startTime,@Param("endTime") long endTime);
}
