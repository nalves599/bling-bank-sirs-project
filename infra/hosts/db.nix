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

  networking.firewall.allowedTCPPorts = [3306];

  services.mysql = {
    enable = true;
    package = pkgs.mariadb;
    initialScript = builtins.toFile "init.sql" ''
      CREATE DATABASE blingbank;
      GRANT ALL PRIVILEGES ON blingbank.* TO 'blingbank'@'dnt-sirs.com' IDENTIFIED BY 'Sup3rS3cr3tP4ssw0rd';
    '';
    settings = {
      # TODO: Fix SSL
      # mariadb = {
      #   "ssl_ca" = toString(../certs/CA.pem);
      #   "ssl_cert" = toString(../certs/db.crt);
      #   "ssl_key" = toString(../certs/db.key);
      # };
    };
  };
}
