backend "consul" {
  address = "consul:8500"
  path = "vault/"
}

listener "tcp" {
  address = "0.0.0.0:8200"
  tls_disable = 1
}

max_lease_ttl = "5m"

default_lease_ttl = "2m"