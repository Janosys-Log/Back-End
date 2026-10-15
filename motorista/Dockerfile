FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl motorista

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appmotorista/

COPY --from=buildador /build/motorista/target/*.jar motorista.jar

CMD ["java", "-jar", "motorista.jar"]

