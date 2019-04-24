# regex-principal-builder

A KafkaPrincipalBuilder that uses a regular expression to determine the authentication principal.

## Defining the regular expression

`RegexPrincipalBuilder` reads the environment variable `KAFKA_PRINCIPAL_BUILDER_REGEX` as a Java regular expression. If not present, a default of `(.+)`, which matches the entire string, is used.

## Supported Kafka versions:

The `RegexPrincipalBuilder` has been checked for compatibility with Kafka 2.0.0 - 2.2.0. See table below for the appropriate version to use:

|Kafka version|Connector version|
|:-|:-|
|2.1|1.0|
|2.2|2.0|

## JMX Metrics
RegexPrincipalBuilder emits two JMX metrics:

```
kafka.security.RegexPrincipalBuilder
  RequestsPerSec
  ErrorsPerSec
```

## Running the demo

The demo runs a single-node zookeeper and Kafka broker cluster with SASL_PLAINTEXT authentication.

In `docker-compose.yml`, the regular expression for resolving the principal name is `KAFKA_PRINCIPAL_BUILDER_REGEX: "(.+)\\..+"` which will resolve to just `my-principal` name for a naming convention of `<my-principal>.<my-user>`.


### Build the jar.

```shell
$ gradle clean assemble
```

### Start the Kafka cluster.

```shell
~/regex-principal-builder$ cd demos/docker
~/regex-principal-builder/demos/docker$ docker-compose up -d
```

### Run the test

`test-acls` will create a topic, producer/consumer users and set acls, then produce messages and finally, consume those message.

The consumer acls are set to allow it to read from all topics (ALLOW_CONSUMER). The producer acls are set using just the `principal` name to allow writing to the topic (ALLOW_PRODUCER). With the inclusion of `RegexPrincipalBuilder`, a producer is authenticated for write to a topic using just the principal as defined by the regular expression (e.g., `my-principal.my-user` will authenticate just on `my-principal`).

```shell
~/regex-principal-builder/demos/docker$ ./test-acls
```

### Tear-down Kafka cluster

```shell
~/regex-principal-builder/demos/docker$ docker-compose down
```
