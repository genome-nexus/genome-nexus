FROM maven:3.5.4
RUN mkdir /genome-nexus
ADD . /genome-nexus
WORKDIR /genome-nexus
RUN mvn -DskipTests clean install

FROM openjdk:8-jre
COPY --from=0 /genome-nexus/web/target/*.war /app.war
CMD ["/usr/bin/java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
