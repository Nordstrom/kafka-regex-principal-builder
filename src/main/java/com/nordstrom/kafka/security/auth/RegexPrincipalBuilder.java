package com.nordstrom.kafka.security.auth;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.security.auth.AuthenticationContext;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.apache.kafka.common.security.auth.KafkaPrincipalBuilder;
import org.apache.kafka.common.security.authenticator.DefaultKafkaPrincipalBuilder;
import org.apache.kafka.common.utils.Utils;


/**
 * KafkaPrincipalBuilder that uses a regular expression to determine the principal.
 *
 */
public class RegexPrincipalBuilder implements KafkaPrincipalBuilder {
  public static final String KAFKA_PRINCIPAL_BUILDER_REGEX_ENV_VAR =
      "KAFKA_PRINCIPAL_BUILDER_REGEX";

  private final RegexPrincipal regexPrincipal;
  private final KafkaPrincipalBuilder kafkaPrincipalBuilder;

  private final MetricName requestsName = new MetricName("", "", "", "",
      "kafka.security:type=RegexPrincipalBuilder,name=RequestsPerSec");
  private final MetricName errorsName = new MetricName("", "", "", "",
      "kafka.security:type=RegexPrincipalBuilder,name=ErrorsPerSec");
  private final Meter requests =
      Metrics.newMeter(requestsName, "regex-principal-builder", TimeUnit.SECONDS);
  private final Meter errors =
      Metrics.newMeter(errorsName, "regex-principal-builder", TimeUnit.SECONDS);


  public RegexPrincipalBuilder() {
    super();

    kafkaPrincipalBuilder = new DefaultKafkaPrincipalBuilder(null);
    regexPrincipal = new RegexPrincipal(KAFKA_PRINCIPAL_BUILDER_REGEX_ENV_VAR);
  }

  public KafkaPrincipal build(AuthenticationContext authenticationContext) {
    Utils.notNull(authenticationContext);

    requests.mark();
    final KafkaPrincipal defaultPrincipal = kafkaPrincipalBuilder.build(authenticationContext);
    final String original = defaultPrincipal.getName();
    final String principal = regexPrincipal.principal(original);

    return new KafkaPrincipal(defaultPrincipal.getPrincipalType(), principal);
  }

} // -RegexPrincipalBuilder
