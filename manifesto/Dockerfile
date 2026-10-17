FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl manifesto

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appManifesto/

COPY --from=buildador /build/manifesto/target/*.jar manifesto.jar

CMD ["java", "-jar", "manifesto.jar"]

