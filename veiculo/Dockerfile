FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl veiculo

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appmotorista/

COPY --from=buildador /build/veiculo/target/*.jar veiculo.jar

CMD ["java", "-jar", "veiculo.jar"]

