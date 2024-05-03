# Multi-stage build
FROM maven:3-eclipse-temurin-21 as build

# Build args
ARG MAVEN_OPTS=-DskipTests 

# ENV variables
ENV GN_HOME=/genome-nexus-annotation-pipeline
ENV GN_RESOURCES=$GN_HOME/annotationPipeline/src/main/resources

# Add source files
COPY . $GN_HOME
WORKDIR $GN_HOME

# Configure log4j file in properties
RUN cp $GN_RESOURCES/log4j.properties.console.EXAMPLE $GN_RESOURCES/log4j.properties

# Maven build
RUN mvn ${MAVEN_OPTS} clean install -q

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
COPY --from=build $GN_TARGET/annotationPipeline-*.jar $GN_TARGET/annotationPipeline.jar
COPY --from=build $GN_RESOURCES/application.properties.EXAMPLE $GN_RESOURCES/application.properties

WORKDIR $GN_HOME/annotationPipeline/target

CMD ["java", "-jar", "annotationPipeline.jar"]
