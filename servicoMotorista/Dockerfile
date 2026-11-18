FROM maven:3.9-eclipse-temurin-21-alpine AS buildador

WORKDIR /build

COPY pom.xml .
COPY avaliacao/pom.xml avaliacao/pom.xml
COPY CsvParaJson/pom.xml CsvParaJson/pom.xml
COPY servicoMotorista/pom.xml servicoMotorista/pom.xml
COPY usuario/pom.xml usuario/pom.xml

COPY servicoMotorista/src servicoMotorista/src

RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -Dspotless.apply.skip=true -pl servicoMotorista

FROM eclipse-temurin:21-jre-alpine

WORKDIR /appmotorista

COPY --from=buildador /build/servicoMotorista/target/*.jar veiculo.jar

CMD ["java", "-jar", "veiculo.jar"]
