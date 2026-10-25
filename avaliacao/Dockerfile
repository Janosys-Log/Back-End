FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl avaliacao

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appAvaliacao/

COPY --from=buildador /build/avaliacao/target/*.jar avaliacao.jar

CMD ["java", "-jar", "avaliacao.jar"]

