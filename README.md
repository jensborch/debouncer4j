# Debouncer4j

Simple Java debouncer.

## Status

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
