FROM openjdk:8-jre-alpine
# copy application WAR (with libraries inside)
COPY web/target/*.war /app.war
# specify default command
CMD ["/usr/bin/java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "/app.war"]