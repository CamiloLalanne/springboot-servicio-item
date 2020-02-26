FROM openjdk:8
VOLUME /tmp
EXPOSE 8005
ADD ./target/servicio-item.jar servicio-item-image.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","-Dspring.cloud.config.uri=http://config-server:8888","/servicio-item-image.jar"]