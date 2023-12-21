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
    initialScript = builtins.toFile "init.sql" ''
      CREATE DATABASE blingbank;
      GRANT ALL PRIVILEGES ON blingbank.* TO 'blingbank'@'dnt-sirs.com' IDENTIFIED BY 'Sup3rS3cr3tP4ssw0rd';
    '';
    settings = {
      ssl_cert_file = toString(../certs/db.crt);
      ssl_ca_file = toString(../certs/ca.crt);
      ssl_key_file = toString(../certs/db.key);
    };
  };
}
