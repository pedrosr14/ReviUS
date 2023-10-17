FROM amazoncorretto:11
VOLUME /tmp
COPY /target/search-service.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=9004","-jar", "app.jar"]
EXPOSE 9004