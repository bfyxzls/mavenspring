FROM fabric8/java-jboss-openjdk8-jdk:1.4
COPY spring-demo-b.jar /deployments/app.jar
ENV URL="http://127.0.0.1:18082/manage/status"
EXPOSE 8082 18082
# docker build ./src/main/docker -t maven-spring-b
# docker run -p 8082:8082 maven-spring-b