package com.nordstrom.kafka.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegexPrincipalTests {
  // NB: KAFKA_PRINCIPAL_BUILDER_REGEX_ENV_VAR and MALFORMED_REGEX are environment variables
  // that must be set in the test run configuration.

  RegexPrincipal regex;

  @BeforeEach
  void setUp() throws Exception {
    regex = new RegexPrincipal(RegexPrincipalBuilder.KAFKA_PRINCIPAL_BUILDER_REGEX_ENV_VAR);
  }

  @AfterEach
  void tearDown() throws Exception {}

  @Test
  void testWithNoEnvUsesDefaultRegex() {
    regex = new RegexPrincipal("NOT_THE_DROIDS_YOURE_LOOKING_FOR");
    final String original = "bluest-heron/bob-the-builder/shiva-the-destroyer";
    final String principal = regex.principal(original);
    assertEquals(original, principal, "Principal does not match");
  }

  @Test
  void testWithProtonEnv() {
    final String original = "bluest-heron/bob-the-builder";
    final String principal = regex.principal(original);
    assertEquals("bluest-heron", principal, "Principal does not match");
  }

  @Test
  void testWithProtonEnvMatchesLastSlash() {
    final String original = "bluest-heron/bob-the-builder/shiva-the-destroyer";
    final String principal = regex.principal(original);
    assertEquals("bluest-heron/bob-the-builder", principal, "Principal does not match");
  }

  @Test
  void testNullPrincipalReturnsNull() {
    final String original = null;
    final String principal = regex.principal(original);
    assertEquals(original, principal, "Principal does not match");
  }

  @Test
  void testEmptyPrincipalReturnsEmpty() {
    final String original = "";
    final String principal = regex.principal(original);
    assertEquals(original, principal, "Principal does not match");
  }

  @Test
  void testMalformedRegex() {
    assertThrows(PatternSyntaxException.class, () -> {
      regex = new RegexPrincipal("MALFORMED_REGEX");
      regex.principal("bluest-heron/bob-the-builder");
    });
  }

}
