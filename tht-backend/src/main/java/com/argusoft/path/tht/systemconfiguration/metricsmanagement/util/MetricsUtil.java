package com.argusoft.path.tht.systemconfiguration.metricsmanagement.util;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.SharedMetricRegistries;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dhruv
 */
@Component
public class MetricsUtil {

    public void initMetricsReports(Map<String, ByteArrayOutputStream> map)
            throws UnsupportedEncodingException {
        addRegistry("CustomUserDetailService", map);
        addRegistry("UserRestController", map);
        addRegistry("AutomationTestProcessRestController", map);
    }

    private void addRegistry(
            String registryName,
            Map<String, ByteArrayOutputStream> map)
            throws UnsupportedEncodingException {
        map.put(registryName, new ByteArrayOutputStream());
        ConsoleReporter report
                = ConsoleReporter
                .forRegistry(
                        SharedMetricRegistries
                                .getOrCreate(
                                        registryName
                                ))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.SECONDS)
                .outputTo(new PrintStream(
                        map.get(registryName),
                        true,
                        "UTF-8"))
                .build();

        report.start(1, TimeUnit.HOURS);
    }

}
