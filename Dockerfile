FROM maven:3.6.0-jdk-8 as builder

WORKDIR /app

COPY . ./

RUN mvn -DskipTests clean install


FROM openjdk:8-jre
# copy application WAR (with libraries inside)
COPY --from=builder /app/web/target/*.war /app.war
# specify default command
CMD ["/usr/bin/java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]
