# Multi-stage build
FROM maven:3-eclipse-temurin-21 as build

# ENV variables
ENV GN_HOME=/genome-nexus-annotation-pipeline
ENV GN_RESOURCES=$GN_HOME/annotationPipeline/src/main/resources

# Add source files
COPY . $GN_HOME
WORKDIR $GN_HOME

# Configure log4j file in properties
RUN cp $GN_RESOURCES/log4j.properties.console.EXAMPLE $GN_RESOURCES/log4j.properties

# Maven build
RUN mvn clean install -DskipTests -q

# Stage-1
FROM eclipse-temurin:21

ENV GN_HOME=/genome-nexus-annotation-pipeline
ENV GN_RESOURCES=$GN_HOME/annotationPipeline/src/main/resources
ENV GN_TARGET=$GN_HOME/annotationPipeline/target

# Update and install dependencies
RUN apt-get update && apt-get -y install \ 
    procps \
    && apt-get clean

# Copy artifact from build-stage
COPY annotationPipeline/target/annotationPipeline-*.jar $GN_TARGET/annotationPipeline.jar
COPY $GN_HOME/scripts $GN_HOME/scripts
COPY annotationPipeline/src/main/resources/application.properties.EXAMPLE $GN_RESOURCES/application.properties

ENV PATH="${PATH}:${GN_HOME}/scripts"

WORKDIR $GN_TARGET

CMD ["java", "-jar", "annotationPipeline.jar"]
