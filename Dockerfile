FROM openjdk:17.0.2-slim
WORKDIR /app
COPY keystore.p12 .
COPY target/SecureChat-0.0.1-SNAPSHOT.jar ./SecureChat.jar
ENTRYPOINT ["java","-jar","SecureChat.jar"]