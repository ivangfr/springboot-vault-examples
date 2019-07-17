FROM openjdk:8-jre-alpine

LABEL maintainer="ivangfr@yahoo.com.br"

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar

ENV VAULT_HOST=vault CONSUL_HOST=consul CASSANDRA_HOST=cassandra

CMD ["java", "-jar", "/app.jar"]

EXPOSE 8080