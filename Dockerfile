FROM eclipse-temurin:21-jdk
# ^ update the above to match your version
WORKDIR /src
COPY target/bookstore-0.0.1-SNAPSHOT.jar /src/bookstore.jar
#  ^ your .jar, you must create your jar first! Using maven - package
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bookstore.jar"]
# change the above entrypoint final argument to 'match' your jar