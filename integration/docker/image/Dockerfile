FROM ubuntu
RUN apt-get update && apt-get upgrade -y && apt-get install openjdk-8-jre -y
COPY target/dda-smeagol-standalone.jar /app/dda-smeagol-standalone.jar
COPY integration/docker/image/smeagol.edn /app/smeago.edn

RUN java -jar /app/dda-smeagol-standalone.jar /app/smeagol.edn
