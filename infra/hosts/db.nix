{
  profiles,
  pkgs,
  ...
}: {
  imports = with profiles; [
    core
    microvm
  ];

  microvm.interfaces = [
    {
      type = "tap";
      id = "vm-db-priv";
      mac = "52:00:00:00:01:01";
    }
  ];

  networking.defaultGateway.address = "10.69.1.1";

  networking.interfaces = {
    eth0.ipv4.addresses = [
      {
        address = "10.69.1.2";
        prefixLength = 24;
      }
    ];
  };

  networking.firewall.allowedTCPPorts = [5432];

  services.postgresql = {
    enable = true;
    enableTCPIP = true;
    initialScript = builtins.toFile "init.sql" ''
      CREATE DATABASE blingbank;
      CREATE USER blingbank WITH PASSWORD 'blingbank';
      GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO blingbank;
    '';
    authentication = ''
      host  all  all 10.69.2.2/32 password
    '';
    settings = {
      ssl_cert_file = toString(../certs/db.crt);
      ssl_ca_file = toString(../certs/ca.crt);
      ssl_key_file = toString(../certs/db.key);
    };
  };
}
