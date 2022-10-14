FROM maven:3.8.5-openjdk-18 as mavenBuild
WORKDIR /app
COPY pom.xml .
# Copy all other project files and build project
COPY src src
RUN mvn install -Dmaven.test.skip -B


# Now create a minimal JRE using jdeps & jlink
FROM openjdk:18-jdk-alpine AS jre-build
WORKDIR /app

COPY --from=mavenBuild /app/target/dependency-jars ./lib
COPY --from=mavenBuild /app/target/*.jar ./app.jar

ENV JAVA_OPTS ""
CMD [ "java", "-jar" , "app.jar" ]