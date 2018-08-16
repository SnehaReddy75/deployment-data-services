package com.slack.deploymentdataservice;

import com.slack.model.Event;
import com.slack.model.Summary;
import com.slack.persistence.EventEntity;
import com.slack.persistence.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sneha.dontireddy on 8/3/18.
 */
@Slf4j
@Service
public class DeploymentDataService {

    @Autowired
    EventRepository eventRepository;

    /**
     * @return Set of Engineers
     */
    public Set<String> getEngineersList(){
        try{
            Set<String> engineerSet = new HashSet<>();
            List<EventEntity> eventEntityList = eventRepository.findAll();

            for(EventEntity eventEntity:eventEntityList){
                engineerSet.add(eventEntity.getEngineer());
            }

            return engineerSet;
        }
        catch (Exception e){
            log.error("Error occurred when retrieving engineers", e);
            return null;
        }
    }

    /**
     * Return events from startTime to endTimed. If both are not provided, then return events of current day
     * @param startTime
     * @param endTime
     * @return list of events
     */
    public List<Event> getEventsInTimeRange(String startTime, String endTime){

        try{
            List<Event> eventList = new ArrayList<>();
            List<EventEntity> eventEntityList;

            if(!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
                long startEpoch = startDateTime.toEpochSecond(ZoneOffset.UTC);

                LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);
                long endEpoch = endDateTime.toEpochSecond(ZoneOffset.UTC);

                eventEntityList = eventRepository.getEventsInTimeRange(startEpoch,endEpoch);
            }else {
                ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
                long timeStamp = utcTime.toInstant().toEpochMilli()/1000;
                long todayMidnight = utcTime.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
                eventEntityList =  eventRepository.getEventsInTimeRange(timeStamp,todayMidnight);
            }

            //TODO: Parallel stream
            for(EventEntity eventEntity : eventEntityList){
                ZonedDateTime utcDate = Instant.ofEpochSecond(eventEntity.getDate()).atZone(ZoneOffset.UTC);
                Event event = new Event(eventEntity.getId(),eventEntity.getEngineer(),eventEntity.getAction(),
                        utcDate, eventEntity.getSha());
                eventList.add(event);
            }

            return eventList;
        }catch(Exception e){
                log.error("Error occurred when retrieving events", e);
                return null;
        }
    }

    public Summary getDailySummary(){

        try{
            ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
            long timeStamp = utcTime.toInstant().toEpochMilli()/1000;
            long todayMidnight = utcTime.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);

            List<EventEntity> eventEntityList = eventRepository.getEventsInTimeRange(todayMidnight,timeStamp);

            int totalStaged = 0;
            int totalDeployed = 0;

            for(EventEntity eventEntity: eventEntityList){
                if(eventEntity.getAction().equals("stage")){
                    totalStaged++;
                }else{
                    totalDeployed++;
                }
            }

            Summary summary = new Summary();
            summary.setTotalDeployed(totalDeployed);
            summary.setTotalStaged(totalStaged);
            if(totalStaged > 0){
                summary.setPercentOfStagedToDeployed((totalDeployed/totalStaged)*100);
            }
            summary.setContributors(eventRepository.getContributors(todayMidnight,timeStamp));

            return summary;
        }
        catch (Exception e){
            log.error("Error occurred when retrieving daily summary", e);
            return null;
        }
    }

    /**
     * @param engineerName
     * @return List of events performed by an engineer
     */
    public List<Event> getEventsByEngineer(String engineerName){
        try{
            List<EventEntity> eventEntityList = eventRepository.findByEngineer(engineerName);
            List<Event> eventList = new ArrayList<>();

            for(EventEntity eventEntity : eventEntityList){
                ZonedDateTime utcDate = Instant.ofEpochSecond(eventEntity.getDate()).atZone(ZoneOffset.UTC);
                Event event = new Event(eventEntity.getId(), eventEntity.getEngineer(), eventEntity.getAction(),utcDate,eventEntity.getSha());
                eventList.add(event);
            }

            return eventList;
        }
        catch (Exception e){
            log.error("Error occurred when retrieving events for engineer: {}", engineerName, e);
            return null;
        }
    }
}

