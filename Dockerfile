FROM maven:3-eclipse-temurin-21
RUN mkdir /genome-nexus
ADD . /genome-nexus
WORKDIR /genome-nexus
# Build the parent project first
# This is necessary to ensure that the parent POM is built before the child modules.
RUN mvn install -N -DskipTests
RUN mvn -DskipTests clean install

FROM eclipse-temurin:21
COPY --from=0 /genome-nexus/web/target/*.war /app.war
CMD ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
