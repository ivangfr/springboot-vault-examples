FROM cassandra:5.0.5

RUN echo "authenticator: PasswordAuthenticator" >> /etc/cassandra/cassandra.yaml
RUN echo "authorizer: org.apache.cassandra.auth.CassandraAuthorizer" >> /etc/cassandra/cassandra.yaml

CMD ["cassandra", "-f"]