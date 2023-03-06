FROM openjdk:17-oracle
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
ARG JAVA_DEBUG_PORT=*:5005