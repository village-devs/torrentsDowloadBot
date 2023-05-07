FROM amazoncorretto:17
MAINTAINER georgyorlov.com
COPY target/*-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
