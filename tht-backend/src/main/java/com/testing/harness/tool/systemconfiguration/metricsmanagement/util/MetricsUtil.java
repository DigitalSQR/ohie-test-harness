package com.testing.harness.tool.systemconfiguration.metricsmanagement.util;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.SharedMetricRegistries;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 *
 * @author dhruv
 *
 */
@Component
public class MetricsUtil {

    public void initMetricsReports(Map<String, ByteArrayOutputStream> map)
            throws UnsupportedEncodingException {
        addRegistry("CustomUserDetailService", map);
        addRegistry("UserServiceRestController", map);
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
