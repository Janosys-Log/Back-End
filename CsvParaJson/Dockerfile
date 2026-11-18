FROM maven:3.9-eclipse-temurin-21-alpine AS buildador

WORKDIR /build

COPY pom.xml .
COPY avaliacao/pom.xml avaliacao/pom.xml
COPY CsvParaJson/pom.xml CsvParaJson/pom.xml
COPY servicoMotorista/pom.xml servicoMotorista/pom.xml
COPY usuario/pom.xml usuario/pom.xml

COPY CsvParaJson/src CsvParaJson/src

RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -Dspotless.apply.skip=true -pl CsvParaJson

FROM eclipse-temurin:21-jre-alpine

WORKDIR /appCsvParaJson

COPY --from=buildador /build/CsvParaJson/target/*.jar CsvParaJson.jar

CMD ["java", "-jar", "CsvParaJson.jar"]
