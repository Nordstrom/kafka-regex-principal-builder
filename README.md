# regex-principal-builder

A KafkaPrincipalBuilder that uses a regular expression to determine the authentication principal.

## Defining the regular expression

`RegexPrincipalBuilder` reads the environment variable `KAFKA_PRINCIPAL_BUILDER_REGEX` as a Java regular expression. If not present, a default of `(.+)`, which matches the entire string, is used.

## Supported Kafka versions:

The `RegExPrincipalBuilder` has been tested with kafka `v2.0.0` and `v2.0.1`(see build.gradle for dependencies)

## JMX Metrics
RegexPrincipalBuilder emits two JMX metrics:

```
kafka.security.RegexPrincipalBuilder
  RequestsPerSec
  ErrorsPerSec
```

## Running the demo

The demo runs a single-node zookeeper and kafka broker cluster with SASL_PLAINTEXT authentication.

The regular expression for resolving the principal name is `(.+)/(.+)` which will resolve just `my-principal` name for a naming convention of `<my-principal>/<my-user>`.  The regular expression is defined by setting KAFKA_PRINCIPAL_BUILDER_REGEX environment variable to a java regular expression (see docker-compose.yml).


### Build the jar.

```shell
$ gradle clean assemble
```

### Start the kafka cluster.

```shell
~/regex-principal-builder$ cd demos/docker
~/regex-principal-builder/demos/docker$ docker-compose up -d
```

### Run the test

`test-acls` will create a topic, producer/consumer users and set acls, then produce messages and finally, consume those message.

The consumer acls are set to allow it to read from all topics (ALLOW_CONSUMER). The producer acls are set using just the `principal` name to allow writing to the topic (ALLOW_PRODUCER). With the inclusion of `RegexPrincipalBuilder`, a producer is authenticated for write to a topic using just the principal as defined by the regular expression (e.g., `my-principal/my-user` will authenticate just on `my-principal`).

```shell
~/regex-principal-builder/demos/docker$ ./test-acls
```

### Tear-down kafka cluster

```shell
~/regex-principal-builder/demos/docker$ docker-compose down
```
