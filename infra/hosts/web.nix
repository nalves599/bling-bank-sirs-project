{
  profiles,
  pkgs,
  ...
}: {
  imports = with profiles; [
    core
    microvm
    webserver
  ];

  environment.systemPackages = with pkgs; [
    postgresql
    nodejs_20
    nodePackages.prisma
    tmux
    neovim
  ];

  microvm.interfaces = [
    {
      type = "tap";
      id = "vm-web-pub";
      mac = "52:00:00:00:02:01";
    }
  ];

  microvm.volumes = [
    {
      size = 5024;
      image = "web-data.img";
      mountPoint = "/mnt/data";
    }
  ];

  microvm.mem = 1024;

  networking.defaultGateway.address = "10.69.2.1";

  networking.interfaces = {
    eth0.ipv4.addresses = [
      {
        address = "10.69.2.2";
        prefixLength = 24;
      }
    ];
  };

  environment.variables = with pkgs; {
    PRISMA_MIGRATION_ENGINE_BINARY="${prisma-engines}/bin/migration-engine";
    PRISMA_QUERY_ENGINE_BINARY="${prisma-engines}/bin/query-engine";
    PRISMA_QUERY_ENGINE_LIBRARY="${prisma-engines}/lib/libquery_engine.node";
    PRISMA_INTROSPECTION_ENGINE_BINARY="${prisma-engines}/bin/introspection-engine";
    PRISMA_FMT_BINARY="${prisma-engines}/bin/prisma-fmt";

    PORT="8080";
    DATABASE_URL="postgresql://blingbank:blingbank@db.dnt-sirs.com:5432/blingbank";

    VITE_API_URL="https://api.dnt-sirs.com";
  };

  services.nginx.virtualHosts."api.dnt-sirs.com" = {
    forceSSL = true;
    sslCertificateKey = ../certs/web.key;
    sslCertificate = ../certs/web.crt;
    sslTrustedCertificate = ../certs/CA.pem;
    locations."/" = {
      proxyPass = "http://localhost:8080";
    };
  };

  services.nginx.virtualHosts."dnt-sirs.com" = {
    serverAliases = ["www.dnt-sirs.com"];
    forceSSL = true;
    sslCertificateKey = ../certs/web.key;
    sslCertificate = ../certs/web.crt;
    sslTrustedCertificate = ../certs/CA.pem;
    locations."/" = {
      proxyPass = "http://localhost:8085";
    };
  };

}
