# Welcome to js2pets &middot; [![GitHub license](https://img.shields.io/github/license/lengors/js2pets?color=blue)](https://github.com/facebook/react/blob/main/LICENSE) [![javadoc](https://javadoc.io/badge2/io.github.lengors/js2pets/javadoc.svg?color=red)](https://javadoc.io/doc/io.github.lengors/js2pets) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lengors_js2pets&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lengors_js2pets)

Welcome to **js2pets**, a project aimed at extending the functionality of [jsonschema2pojos](https://github.com/joelittlejohn/jsonschema2pojo), which generates Java POJOs from JSON schemas. This library builds upon the capabilities of `jsonschema2pojos` to provide additional features and enhancements.

## Features

- **Custom Rule Factory**: Extend the capabilities of `jsonschema2pojo` by providing custom rules that suit your project needs.
- **Flexible Integration**: Easily integrate with the `jsonschema2pojo` Maven plugin by adding `js2pets` as a dependency.

## Getting Started

To use `js2pets` in your project, follow these steps:

### Prerequisites

- Java 8 or higher
- Maven or Gradle

### Installation

#### Maven

Add `js2pets` as a dependency in the `jsonschema2pojo` plugin configuration of your Maven project:

```xml
<plugin>
  <groupId>org.jsonschema2pojo</groupId>
  <artifactId>jsonschema2pojo-maven-plugin</artifactId>
  <version>${jsonschema2pojo.version}</version>
  <configuration>
    <includeConstructors>true</includeConstructors>
    <includeNoArgsConstructor>false</includeNoArgsConstructor>
    <customRuleFactory>io.github.lengors.js2pets.factories.EnhancedRuleFactory</customRuleFactory>
  </configuration>
  <executions>
    <execution>
      <id>generate-pojos</id>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>io.github.lengors</groupId>
      <artifactId>js2pets</artifactId>
      <version>${js2pets.version}</version>
    </dependency>
  </dependencies>
</plugin>
```

#### Gradle

To use `js2pets` with Gradle, configure your `build.gradle.kts` as follows:

```kotlin
plugins {
    id("org.jsonschema2pojo") version jsonschema2pojoVersion
}

buildscript {
    repositories {
        mavenLocal()
    }

    dependencies {
        classpath("io.github.lengors:js2pets:$js2petsVersion")
    }
}

jsonSchema2Pojo {
    setCustomRuleFactory(EnhancedRuleFactory.IncludeNoArgsConstructor::class.java)
    includeConstructors = true
}
```

## Usage

Once configured, the `js2pets` custom rules will automatically apply when generating Java classes from your JSON schemas. Customize your JSON schema as needed, and the generated Java classes will reflect the custom rules defined in `js2pets`.

### Example

Given the following JSON schema:

```json
{
  "type": "object",
  "properties": {
    "petName": {
      "type": "string"
    },
    "age": {
      "type": "integer"
    }
  }
}
```

The `jsonschema2pojo` plugin, enhanced with `js2pets`, will generate a Java class that includes the constructors but is able to leave out the no-args constructor.

## Documentation and Resources

For detailed guides and additional information, please refer to our [GitHub Wiki](https://github.com/lengors/js2pets/wiki).

If you wish to check the detailed API documentation, visit the [Javadoc](https://javadoc.io/doc/io.github.lengors/js2pets) page.

## Contributing

Contributions are welcome! Please refer to our [Contribution Guidelines](./CONTRIBUTING.md) for more information on how to get involved.

## License

This project is licensed under [The Unlicense](./LICENSE), which places it in the public domain.
