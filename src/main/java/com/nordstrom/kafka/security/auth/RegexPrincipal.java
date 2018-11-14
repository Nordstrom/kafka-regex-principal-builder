package com.nordstrom.kafka.security.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.common.utils.LogContext;
import org.slf4j.Logger;

public class RegexPrincipal {
  public static final String REGEX_DEFAULT = "(.+)"; // Matches entire string (pass-through).

  private static final Logger log = new LogContext().logger(RegexPrincipal.class);

  private final Pattern pattern;

  /**
   * A filter that applies a regular expression to a principal.
   *
   * @param environmentVariable Name of environment variable that contains the regular expression.
   */
  public RegexPrincipal(String environmentVariable) {
    String regex = System.getenv(environmentVariable);
    if (null == regex || regex.isEmpty()) {
      log.warn("Environment variable '{}' is {}, using default '{}' (matches entire string)",
          environmentVariable, regex, REGEX_DEFAULT);
      regex = REGEX_DEFAULT;
    }
    // This will throw PatternSyntaxException if regex is malformed.
    pattern = Pattern.compile(regex, Pattern.MULTILINE);
  }

  /**
   * Get the principal name as the first group matched to the regular expression.
   *
   * @param original String to process through regular expression.
   * @return First group if a match, original string otherwise.
   */
  public String principal(String original) {
    if (null != original && !original.isEmpty()) {
      final Matcher matcher = pattern.matcher(original);

      if (matcher.find()) {
        if (matcher.groupCount() > 0) {
          return matcher.group(1);
        }
      }
    }
    return original;
  }

}
