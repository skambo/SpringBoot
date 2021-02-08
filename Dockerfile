FROM openjdk:11-jdk as gradle-builder

# Dockerfiles configuration
LABEL Name="springbootexample_api_build"
LABEL Version="1.0.0"
LABEL Maintainer="Sandra <swairimu2002@gmail.com>"

WORKDIR /app

# Download Gradle Wrapper
COPY gradlew gradlew.bat ./
COPY gradle ./gradle

RUN ./gradlew

COPY build.gradle.kts settings.gradle.kts bin/build.sh bin/wait-for-it.sh . ./

RUN ["chmod", "+x", "./build.sh"]
RUN ./build.sh

  # Image 2 - execution
FROM openjdk:11-jdk

  # Dockerfile configuration
LABEL Name="springbootexample_api_rc"
LABEL Version="1.0.0"
LABEL Maintainer="Sandra <swairimu2002@gmail.com>"

RUN apt-get update && apt-get install curl bash

WORKDIR /app

# Copy executable JAR
COPY --from=0 app/build/libs/springBootExample-1.0-SNAPSHOT.jar ./

COPY --from=0 app/bin/start.sh app/bin/wait-for-it.sh ./
RUN ["chmod", "+x", "start.sh"]

#COPY devops/scripts/set_datadog_agent_in_ecs.sh ./
# expose default port for Spring
EXPOSE 8080
