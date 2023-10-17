FROM amazoncorretto:11
VOLUME /tmp
COPY /target/gateway-service.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=9001","-jar", "app.jar"]
EXPOSE 9001