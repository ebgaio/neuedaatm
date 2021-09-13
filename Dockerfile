FROM openjdk:8

WORKDIR /app

COPY target/neueda-atm-0.0.1-SNAPSHOT.jar /app/neueda-atm.jar

ENTRYPOINT [ "java", "-jar", "neueda-atm.jar",]