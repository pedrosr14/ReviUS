FROM amazoncorretto:11
VOLUME /tmp
COPY /target/eureka-server.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=8761","-jar", "app.jar"]
EXPOSE 8761