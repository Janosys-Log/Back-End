FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl agregado

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appAgregado/

COPY --from=buildador /build/agregado/target/*.jar Agregado.jar

CMD ["java", "-jar", "Agregado.jar"]

