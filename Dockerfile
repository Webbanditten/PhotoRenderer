FROM openjdk:12
COPY ./dist /usr/src/app
WORKDIR /usr/src/app
EXPOSE 9000 9000
ENTRYPOINT ["java", "-Xmx500m", "-Xms300m", "-cp", "PhotoRenderer-1.0-SNAPSHOT.jar", "org.webbanditten.server.Server"]