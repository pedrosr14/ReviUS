FROM amazoncorretto:11
VOLUME /tmp
COPY /target/review-service.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=9003","-jar", "app.jar"]
EXPOSE 9003
