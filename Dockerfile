FROM maven:3.6-jdk-13
RUN mkdir /genome-nexus
ADD . /genome-nexus
WORKDIR /genome-nexus
RUN mvn -DskipTests clean install

FROM openjdk:13-slim
COPY --from=0 /genome-nexus/web/target/*.war /app.war
COPY --from=0 /genome-nexus/data/curiousCases/VUEs.json /data/curiousCases/VUEs.json
CMD ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
