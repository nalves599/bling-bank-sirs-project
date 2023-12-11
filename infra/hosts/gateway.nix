{
  profiles,
  pkgs,
  ...
}: {
  imports = with profiles; [
    core
    microvm
  ];

  environment.systemPackages = with pkgs; [
    python3
    tcpdump
  ];

  microvm.interfaces = [
    {
      type = "tap";
      id = "vm-gw-world";
      mac = "52:00:00:00:00:01";
    }
    {
      type = "tap";
      id = "vm-gw-priv";
      mac = "52:00:00:00:00:02";
    }
    {
      type = "tap";
      id = "vm-gw-pub";
      mac = "52:00:00:00:00:03";
    }
  ];

  networking.defaultGateway.address = "10.69.0.1";

  networking.nat = {
    enable = true;
    externalInterface = "eth0";
    internalInterfaces = ["eth1" "eth2"];
    internalIPs = ["10.69.1.0/24" "10.69.2.0/24"];
  };

  networking.interfaces = {
    eth0.ipv4.addresses = [
      {
        address = "10.69.0.2";
        prefixLength = 24;
      }
    ];
    eth1.ipv4.addresses = [
      {
        address = "10.69.1.1"; # Priv Network
        prefixLength = 24;
      }
    ];
    eth2.ipv4.addresses = [
      {
        address = "10.69.2.1"; # Pub network
        prefixLength = 24;
      }
    ];
  };
}
