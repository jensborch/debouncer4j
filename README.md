# Debouncer4j

Simple Java debouncer.

## Status

[![Build Status](https://travis-ci.com/jensborch/debouncer4j.svg?branch=master)](https://travis-ci.com/jensborch/debouncer4j)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.jensborch.debouncer4j%3Adebouncer4j&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.jensborch.debouncer4j%3Adebouncer4j)

[![codecov](https://codecov.io/gh/jensborch/debouncer4j/branch/master/graph/badge.svg?token=A2FSCN5BYS)](https://codecov.io/gh/jensborch/debouncer4j)

Currently under development.

## Building

The Debouncer4j is build using Maven.

To build the application run the following command:

```sh
./mvnw package
```

Install the application in your local maven repository (required for running locally)

```sh
./mvnw install
```

Run mutation tests:

```sh
./mvnw eu.stamp-project:pitmp-maven-plugin:run
```

Release to Maven central:

```sh
./mvnw release:clean release:prepare -Prelease
./mvnw release:perform -Prelease
````
