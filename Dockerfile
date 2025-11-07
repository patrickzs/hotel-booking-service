FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} hotel-booking-service.jar
ENTRYPOINT ["java", "-jar", "hotel-booking-service.jar"]
EXPOSE 8080