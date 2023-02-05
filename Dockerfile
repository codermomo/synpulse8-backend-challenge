FROM eclipse-temurin:19-jdk-jammy as base
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src

FROM base as test
RUN ["./mvnw", "test"]

FROM base as dev
CMD ["./mvnw", "spring-boot:run"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:19-jdk-jammy as prod
EXPOSE 8080
COPY --from=build /app/target/s8challenge-*.jar /s8challenge.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/s8challenge.jar"]