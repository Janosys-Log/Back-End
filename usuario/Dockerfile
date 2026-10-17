FROM maven:3.9-eclipse-temurin-21-alpine as buildador

WORKDIR build/

COPY . . 

RUN mvn clean package -DskipTests -pl usuario

FROM  eclipse-temurin:21-jre-alpine

WORKDIR appusuario/

COPY --from=buildador /build/usuario/target/*.jar usuario.jar

CMD ["java", "-jar", "usuario.jar"]

