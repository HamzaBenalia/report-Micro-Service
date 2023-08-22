FROM openjdk:17-alpine
WORKDIR /opt/report
ENV PORT=9091
EXPOSE 9091
COPY target/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
