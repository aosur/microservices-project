FROM openjdk:11
COPY "./target/config-server-0.0.1-SNAPSHOT.jar" "config-server.jar"
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "config-server.jar"]