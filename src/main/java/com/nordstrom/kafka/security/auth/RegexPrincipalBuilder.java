package com.nordstrom.kafka.security.auth;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.security.auth.AuthenticationContext;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.apache.kafka.common.security.auth.KafkaPrincipalBuilder;
import org.apache.kafka.common.security.authenticator.DefaultKafkaPrincipalBuilder;
import org.apache.kafka.common.utils.LogContext;
import org.slf4j.Logger;


/**
 * KafkaPrincipalBuilder that uses a regular expression to determine the principal.
 *
 */
public class RegexPrincipalBuilder implements KafkaPrincipalBuilder {
  public static final String KAFKA_PRINCIPAL_BUILDER_REGEX_ENV_VAR =
      "KAFKA_PRINCIPAL_BUILDER_REGEX";

  private static final Logger log = new LogContext().logger(RegexPrincipal.class);

  private final RegexPrincipal regexPrincipal;
  private final KafkaPrincipalBuilder kafkaPrincipalBuilder;

  // Create MetricName objects, passing in only the the 'mBeanName' with empty strings for 'group',
  // 'type', 'name', and 'scope'.
  // See
  // http://javadox.com/com.yammer.metrics/metrics-core/2.2.0/com/yammer/metrics/core/MetricName.html#MetricName(java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String)
  private final MetricName requestsName = new MetricName("kafka", "RegexPrincipalBuilder", "RequestsPerSec", "kafka.security",
      "kafka.security:type=RegexPrincipalBuilder,name=RequestsPerSec");
  private final MetricName errorsName = new MetricName("kafka", "RegexPrincipalBuilder", "ErrorsPerSec", "kafka.security",
      "kafka.security:type=RegexPrincipalBuilder,name=ErrorsPerSec");
  private final Meter requests =
      Metrics.newMeter(requestsName, "regex-principal-builder", TimeUnit.SECONDS);
  private final Meter errors =
      Metrics.newMeter(errorsName, "regex-principal-builder", TimeUnit.SECONDS);


  public RegexPrincipalBuilder() {
    super();

    kafkaPrincipalBuilder = new DefaultKafkaPrincipalBuilder(null, null);
    // NB: RegexPrincipal builder will throw an exception for a malformed regex.
    regexPrincipal = new RegexPrincipal(KAFKA_PRINCIPAL_BUILDER_REGEX_ENV_VAR);
  }

  public KafkaPrincipal build(AuthenticationContext authenticationContext) {
    requests.mark();
    try {
      if (authenticationContext == null)
        throw new NullPointerException();

      final KafkaPrincipal defaultPrincipal = kafkaPrincipalBuilder.build(authenticationContext);
      final String original = defaultPrincipal.getName();
      final String principal = regexPrincipal.principal(original);

      return new KafkaPrincipal(defaultPrincipal.getPrincipalType(), principal);
    } catch (Exception e) {
      errors.mark();
      log.error("Problem procesing RegexPrincipalBuilder request", e);
      return kafkaPrincipalBuilder.build(authenticationContext);
    }
  }

} // -RegexPrincipalBuilder
