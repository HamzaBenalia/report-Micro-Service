FROM openjdk:17-alpine
ENV PORT=9091
COPY target/*.jar ./app-report.jar
EXPOSE 9091
ENTRYPOINT ["java","-jar","/app-report.jar"]
