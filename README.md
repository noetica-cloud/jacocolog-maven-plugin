# JaCoCoLog

Inspired from [JaCoCo Gradle Plugin](https://gitlab.com/barfuin/gradle-jacoco-log).  
Maven plugin that logs test coverage calculated by the
[JaCoCo Maven Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html) to the Maven build log.
This plugin can also aggregate coverage information from multiple modules in a multi-module build.

The test coverage will be logged like this:

```
Test Coverage:
    - Class Coverage: 100%
    - Method Coverage: 100%
    - Branch Coverage: 81.2%
    - Line Coverage: 97.8%
    - Instruction Coverage: 95.3%
    - Complexity Coverage: 90.2%
```

Note: a coverage ratio can be "unknown" if there is no elements to cover (lines, classes, branches...)

#### Prerequisites

- Maven 3.8 or newer
- Java 8 or newer

&nbsp;

----------------------------------------------------------------------------------------------------------------------

## How to use

*jacocolog-maven-plugin* is published to the
[Maven Central Repository](https://mvnrepository.com/artifact/cloud.noetica.jacocolog/jacocolog-maven-plugin),
so you can just use it in your build like so:
```xml
<plugin>
  <groupId>cloud.noetica.jacocolog</groupId>
  <artifactId>jacocolog-maven-plugin</artifactId>
  <version>1.0.0</version>
  <!-- Important to add if you want to enable overall coverage logging -->
  <extensions>true</extensions>
</plugin>
```

## Details

### Configuration

The following represents configurable properties its default values:
```xml
<configuration>
  <overall>
    <!-- Log an overall coverage at the end of Maven session -->
    <enable>false</enable>
    <!-- Projects to includes (if none, then all reports will be used) -->
    <includes></includes>
  </overall>
  <!-- Jacoco execution files to include (project's related path regex) -->
  <includes>**/jacoco.exec</includes>
  <!-- Ratio's precision (greater than or equals to 0) -->
  <digits>2</digits>
  <!-- Counters to display -->
  <counters>CLASS,METHOD,BRANCH,LINE,INSTRUCTION,COMPLEXITY</counters>
</configuration>
```

### Integration with GitLab CI

GitLab CI can [parse the coverage
information](https://docs.gitlab.com/ci/testing/code_coverage) from the log file.
It can only deal with a single value, so you must choose one of the counters. Line or instruction coverage are popular
for that.

In your pipeline definition, add a `coverage` entry, with `Instruction` replaced with whatever your counter is.

**.gitlab-ci.yml:**
```yaml
coverage: '/    - Instruction Coverage: ([0-9.]+)%/'
```

&nbsp;

----------------------------------------------------------------------------------------------------------------------

## Development

In order to develop *maven-jacoco-log (JaCoCoLog)* and build it on your local machine, you need:

- Java 8 JDK ([download](https://adoptopenjdk.net/releases.html?variant=openjdk8))
- Maven, but we use the Maven Wrapper, so there is nothing to install for you
- You can also use DevContainer with the repository's commited [configuration file](.devcontainer/devcontainer.json)

The project is IDE agnostic. Just use your favorite IDE and make sure none of its control files get checked in.


## Status

*maven-jacoco-log (JaCoCoLog)* is currently in development.


## License

*maven-jacoco-log (JaCoCoLog)* is free software under the terms of the Apache License, Version 2.0.
Details in the [LICENSE](LICENSE) file.


## Contributing

Contributions of all kinds are very welcome! If you are going to spend a lot of time on your contribution, it may
make sense to raise an issue first and discuss your contribution. Thank you!
