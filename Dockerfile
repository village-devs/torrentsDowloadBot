FROM amazoncorretto:17
MAINTAINER georgyorlov.com

COPY target/*-jar-with-dependencies.jar app.jar
#mount volume to /files
RUN mkdir -p /files
VOLUME /files

ENTRYPOINT ["java","-jar","/app.jar"]
#compose for limit volume size