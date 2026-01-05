backend "consul" {
  address = "consul:8500"
  path = "vault/"
}

listener "tcp" {
  address = "0.0.0.0:8200"
  tls_disable = 1
}

# The default_lease_ttl and max_lease_ttl properties are defined while creating database roles in the scripts "setup-spring*.sh"
# default_lease_ttl = "2m"
# max_lease_ttl = "5m"
