FROM maven:3.9-eclipse-temurin-21-alpine AS buildador

WORKDIR /build

COPY pom.xml .
COPY avaliacao/pom.xml avaliacao/pom.xml
COPY CsvParaJson/pom.xml CsvParaJson/pom.xml
COPY servicoMotorista/pom.xml servicoMotorista/pom.xml
COPY usuario/pom.xml usuario/pom.xml

COPY avaliacao/src avaliacao/src

RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -Dspotless.apply.skip=true -pl avaliacao

FROM eclipse-temurin:21-jre-alpine

WORKDIR /appAvaliacao

COPY --from=buildador /build/avaliacao/target/*.jar avaliacao.jar

CMD ["java", "-jar", "avaliacao.jar"]
