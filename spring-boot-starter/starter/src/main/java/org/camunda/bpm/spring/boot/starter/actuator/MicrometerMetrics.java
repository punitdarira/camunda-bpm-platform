package org.camunda.bpm.spring.boot.starter.actuator;

import jakarta.annotation.PostConstruct;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.management.MetricIntervalValue;
import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MicrometerMetrics {

    @Autowired
    MeterRegistry registry;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    CamundaBpmProperties camundaBpmProperties;

    @PostConstruct
    public void init() {
        Map<String, Long> metricsMap = new HashMap<>();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {

            List<MetricIntervalValue> metricsList = processEngine.getManagementService()
                    .createMetricsQuery().interval(1);
            System.out.println("metricsList");
            System.out.println(metricsList);
            if(metricsList.isEmpty()){
                return;
            }
            metricsList.forEach(metric ->{
                if(!metricsMap.containsKey(metric.getName())) {
                    Gauge.builder("camunda." + metric.getName(),metricsMap, map -> map.get(metric.getName()))
                            .register(registry);
                }
                metricsMap.put(metric.getName(), metric.getValue());
            });
        }, 0, camundaBpmProperties.getMetrics().getActuator().getInterval(), TimeUnit.SECONDS);
    }
}
