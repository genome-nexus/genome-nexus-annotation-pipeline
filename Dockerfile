# Multi-stage build
# Stage-0
FROM openjdk:21-jdk-slim as build

# Build args
ARG mvnprofiles=''

# ENV variables
ENV GN_HOME=/genome-nexus-annotation-pipeline
ENV GN_RESOURCES=$GN_HOME/annotationPipeline/src/main/resources

# Update and install dependencies
RUN apt-get update && apt-get -y install \
    maven \ 
    && apt-get clean

# Add source files
COPY . $GN_HOME

# Configure log4j file in properties
RUN cp $GN_RESOURCES/log4j.properties.console.EXAMPLE $GN_RESOURCES/log4j.properties

# Maven build
WORKDIR $GN_HOME
RUN mvn -DskipTests clean install $mvnprofiles

# Stage-1
FROM openjdk:21-jdk-slim

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
