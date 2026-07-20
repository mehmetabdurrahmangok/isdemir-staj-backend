# 1. Aşama: Derleme (Build) - Kodu alır ve .jar dosyası üretir
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
# Önce kütüphaneleri indir
RUN ./mvnw dependency:go-offline -B
# Sonra kodu kopyala ve derle
COPY src src
RUN ./mvnw clean package -DskipTests

# 2. Aşama: Çalıştırma (Run) - Sadece derlenen .jar dosyasını alıp çalıştırır (Daha hafiftir)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]