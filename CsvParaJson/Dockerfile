FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl CsvParaJson

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appCsvParaJson/

COPY --from=buildador /build/CsvParaJson/target/*.jar CsvParaJson.jar

CMD ["java", "-jar", "CsvParaJson.jar"]

