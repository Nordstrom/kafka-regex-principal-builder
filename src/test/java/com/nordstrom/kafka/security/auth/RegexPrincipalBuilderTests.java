package com.nordstrom.kafka.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegexPrincipalBuilderTests {

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testNullContextThrowsException() {
    assertThrows(NullPointerException.class, () -> {
      RegexPrincipalBuilder builder = new RegexPrincipalBuilder();
      builder.build(null);
    });

    // Check metrics
    MetricsRegistry registry = Metrics.defaultRegistry();
    Map<MetricName, Metric> metrics = registry.allMetrics();

    MetricName requestsName = new MetricName("", "", "", "",
        "kafka.security:type=RegexPrincipalBuilder,name=RequestsPerSec");
    Meter requests = (Meter) metrics.get(requestsName);
    assertEquals(1, requests.count(), "RequestsPerSec metric count does not match");

    MetricName errorsName = new MetricName("", "", "", "",
        "kafka.security:type=RegexPrincipalBuilder,name=ErrorsPerSec");
    Meter errors = (Meter) metrics.get(errorsName);
    assertEquals(1, errors.count(), "ErrorsPerSec metric count does not match");
  }

}
