
FROM java:8

VOLUME /var/lib/docker-images

EXPOSE 8013

ADD /var/lib/jenkins/workspace/docker_soul/target/LoginService_Backend.jar LoginService_Backend.jar 

#ADD target/spring-boot-react-docker-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar spring-boot-react-docker-demo-0.0.1-SNAPSHOT.jar

#ENTRYPOINT ["java","-jar","spring-boot-react-docker-demo-0.0.1-SNAPSHOT"]


ENTRYPOINT ["java","-jar","LoginService_Backend"]




#ADD /Volumes/dev/sterling/java-user-login-service/user-login-server/build/libs/user-login-server-1.0.5-SNAPSHOT.jar /user-login-server-1.0.5-SNAPSHOT.jar
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /user-login-server-1.0.5-SNAPSHOT.jar"]


#failed: stat /var/lib/docker/tmp/docker-builder663214013/Volumes/dev/sterling/java-user-login-service/user-login-server/build/libs/user-login-server-1.0.5-SNAPSHOT.jar: no such file or directory

#ADD build/libs/user-login-server-1.0.5-SNAPSHOT.jar /user-login-server-1.0.5-SNAPSHOT.jar
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /user-login-server-1.0.5-SNAPSHOT.jar"]



