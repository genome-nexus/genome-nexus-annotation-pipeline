FROM maven:3-openjdk-11

ENV GN_HOME=/genome-nexus-annotation-pipeline
COPY . $GN_HOME
WORKDIR $GN_HOME

RUN cp $GN_HOME/annotationPipeline/src/main/resources/application.properties.EXAMPLE $GN_HOME/annotationPipeline/src/main/resources/application.properties
RUN cp $GN_HOME/annotationPipeline/src/main/resources/log4j.properties.EXAMPLE $GN_HOME/annotationPipeline/src/main/resources/log4j.properties

# set log4j file in properties
RUN sed -i "s|log4j\.appender\.a\.File.*|log4j.appender.a.File = $GN_HOME/genome-nexus-annotation-pipeline.log|" $GN_HOME/annotationPipeline/src/main/resources/log4j.properties

ARG mvnprofiles=''
RUN mvn -DskipTests clean install $mvnprofiles

RUN mv $GN_HOME/annotationPipeline/target/annotationPipeline-*.jar $GN_HOME/annotationPipeline/target/annotationPipeline.jar

ENTRYPOINT ["java", "-jar", "/genome-nexus-annotation-pipeline/annotationPipeline/target/annotationPipeline.jar"]
