package com.slack.controller;

import com.slack.deploymentdataservice.DeploymentDataService;
import com.slack.model.Event;
import com.slack.model.Summary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Set;

/**
 * Created by sneha.dontireddy on 8/3/18.
 */
@Slf4j
@RestController
@RequestMapping("/deployment/events")
public class DeploymentDataController {

    @Autowired
    DeploymentDataService deploymentDataService;

    @GetMapping(value = "/")
    public ResponseEntity getEventsInTimeRange(@QueryParam("startTime") String startTime,
                                               @QueryParam("endTime") String endTime) {

        log.info("Received request to retrieve deployment events from {} - {}", startTime,endTime);
        if(!StringUtils.isEmpty(startTime) && StringUtils.isEmpty(startTime)){
            return ResponseEntity.badRequest().body("Missing startTime query param");
        }else if(StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(startTime)){
            return ResponseEntity.badRequest().body("Missing endTime query param");
        }

        List<Event> events = deploymentDataService.getEventsInTimeRange(startTime,endTime);
        if(events != null){
            if(events.isEmpty()){
                log.info("No events found for the time range {} - {}", startTime, endTime);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }else{
                log.info("Returning deployment events for the time range {} - {}", startTime, endTime);
                return ResponseEntity.ok(events);
            }
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/engineer/{engineer}")
    public ResponseEntity getEvents(@PathVariable("engineer") String engineer) {
        log.info("Received request to retrieve deployment events performed by engineer: {}", engineer);
        List<Event> events = deploymentDataService.getEventsByEngineer(engineer);

        if(events != null){
            if(events.isEmpty()){
                log.info("No events found for the engineer {}", engineer);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }else{
                log.info("Returning deployment events performed by engineer: {}", engineer);
                return ResponseEntity.ok(deploymentDataService.getEventsByEngineer(engineer));
            }
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/daily-summary")
    public ResponseEntity getDailySummary() {
        log.info("Received request to retrieve daily summary of deployment events");
        Summary summary = deploymentDataService.getDailySummary();

        if(summary != null){
            log.info("Returning daily summary of deployment events");
            return ResponseEntity.ok(summary);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/engineers")
    public ResponseEntity getEngineers() {
        log.info("Received request to retrieve list of engineers");
        Set<String> engineers = deploymentDataService.getEngineersList();

        if(engineers != null){
            log.info("Returning list of engineers");
            return ResponseEntity.ok(deploymentDataService.getEngineersList());
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
