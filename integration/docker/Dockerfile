FROM ubuntu
RUN apt-get update && apt-get upgrade -y && apt-get install openjdk-8-jre apt-utils net-tools -y
COPY target/dda-smeagol-crate-0.1.0-SNAPSHOT-standalone.jar /app/dda-smeagol-crate.jar
COPY example-smeagol.edn /app/dda-smeagol-crate-config.edn

RUN java -jar /app/dda-smeagol-crate.jar /app/dda-smeagol-crate-config.edn
