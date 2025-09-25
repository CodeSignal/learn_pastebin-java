############################
# Frontend build stage
############################
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

############################
# Java build stage
############################
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY backend/pom.xml backend/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests -f backend/pom.xml dependency:go-offline
COPY backend/src/ backend/src/
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests -f backend/pom.xml package

############################
# Runtime image
############################
FROM eclipse-temurin:23-jre
WORKDIR /app
# Copy the Spring Boot fat jar (exclude the .original)
COPY --from=build /app/backend/target/*-SNAPSHOT.jar /app/app.jar
COPY --from=frontend /app/frontend/dist /app/frontend/dist

EXPOSE 3000
CMD ["java", "-jar", "/app/app.jar"]
