FROM amazoncorretto:11
VOLUME /tmp
COPY /target/user-service.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=9002","-jar", "app.jar"]
EXPOSE 9002
