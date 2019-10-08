FROM openjdk:8-jre-alpine

LABEL maintainer="ivangfr@yahoo.com.br"

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar

ENV VAULT_URI="http://vault:8200" CONSUL_HOST=consul CUSTOMER_MYSQL_HOST=mysql DISH_MYSQL_HOST=mysql-2

CMD ["java", "-jar", "/app.jar"]

EXPOSE 8080