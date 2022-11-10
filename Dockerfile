FROM maven:3.8.6-eclipse-temurin-11
RUN mkdir /genome-nexus
ADD . /genome-nexus
WORKDIR /genome-nexus
RUN mvn -DskipTests clean install

FROM eclipse-temurin:11
COPY --from=0 /genome-nexus/web/target/*.war /app.war
COPY --from=0 /genome-nexus/web/src/main/resources/VUEs.json /web/src/main/resources/VUEs.json
CMD ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
