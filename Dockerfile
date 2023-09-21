FROM maven:3-eclipse-temurin-11
RUN mkdir /genome-nexus
ADD . /genome-nexus
WORKDIR /genome-nexus
RUN mvn -DskipTests clean install

FROM eclipse-temurin:11
COPY --from=0 /genome-nexus/web/target/*.war /app.war
CMD ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
