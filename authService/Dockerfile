FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/build/libs/*.jar app.jar

EXPOSE 9002

ENTRYPOINT ["java","-jar","app.jar"]