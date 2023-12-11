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
    python3
    tcpdump
    mysql
  ];

  microvm.interfaces = [
    {
      type = "tap";
      id = "vm-web-pub";
      mac = "52:00:00:00:02:01";
    }
  ];

  networking.defaultGateway.address = "10.69.2.1";

  networking.interfaces = {
    eth0.ipv4.addresses = [
      {
        address = "10.69.2.2";
        prefixLength = 24;
      }
    ];
  };

  services.nginx.virtualHosts."dnt-sirs.com" = {
    serverAliases = ["www.dnt-sirs.com" "api.dnt-sirs.com"];
    forceSSL = true;
    sslCertificateKey = ../certs/web.key;
    sslCertificate = ../certs/web.crt;
    sslTrustedCertificate = ../certs/CA.pem;
  };
}
