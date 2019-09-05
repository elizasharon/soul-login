
FROM java:8

VOLUME /var/lib/docker-images

EXPOSE 8013

ADD /var/lib/jenkins/workspace/docker_soul/target/LoginService_Backend.jar LoginService_Backend.jar 

#ADD target/spring-boot-react-docker-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar spring-boot-react-docker-demo-0.0.1-SNAPSHOT.jar

#ENTRYPOINT ["java","-jar","spring-boot-react-docker-demo-0.0.1-SNAPSHOT"]


ENTRYPOINT ["java","-jar","LoginService_Backend"]

