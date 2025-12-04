#FROM openjdk:17
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} hotel-booking-service.jar
#ENTRYPOINT ["java", "-jar", "hotel-booking-service.jar"]
#EXPOSE 8080


# ----- Giai đoạn 1: Build (Biên dịch) -----
# Sử dụng image có sẵn Maven và JDK 17 để build project
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

# Tạo thư mục làm việc
WORKDIR /app

# Copy file pom.xml và tải dependencies (tối ưu cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# Copy toàn bộ code và build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ----- Giai đoạn 2: Runtime (Chạy) -----
# Sử dụng một image JRE (Java Runtime) siêu nhẹ
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy file .jar đã build từ Giai đoạn 1
COPY --from=builder /app/target/*.jar app.jar

# Port mà Spring Boot chạy bên trong container (Coolify sẽ tự map)
EXPOSE 8080

# Lệnh để chạy ứng dụng
# Coolify sẽ tự động inject biến $PORT, nhưng chúng ta vẫn cần khai báo $JAVA_OPTS
# cho các biến môi trường
CMD ["java", "-jar", "app.jar"]