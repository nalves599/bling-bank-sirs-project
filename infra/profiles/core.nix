{
  inputs,
  pkgs,
  ...
}: {

  environment.systemPackages = with pkgs; [
    python3
    netcat-openbsd
    tcpdump
  ];

  nix = {
    registry = {
      nixpkgs.flake = inputs.nixpkgs;
    };

    # Generally useful nix option defaults
    settings = {
      keep-outputs = true;
      keep-derivations = true;
      fallback = true;
      experimental-features = ["nix-command" "flakes"];
    };
  };

  # Enable OpenSSH server
  services.openssh = {
    enable = true;
    settings = {
      PermitRootLogin = "prohibit-password";
      PasswordAuthentication = false;
      KbdInteractiveAuthentication = false;
    };
  };

  users.users.root = {
    password = "root";
    openssh.authorizedKeys.keys = [
      "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBN1G9PeJIPuyl4amUH7NovvQRBBKvKAO6ldjr6a0a0K" # Nuno
      "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAID+cijTKbz7SP3ZZsc1MdagXpolzJyq3I6VFBKvDyea7" # Tomas
    ];
  };

  networking = {
    firewall.enable = true;
    nameservers = ["1.1.1.1" "8.8.8.8"];
    domain = "dnt-sirs.com";
    hosts = {
      "10.69.0.1" = ["gateway-world"];
      "10.69.1.1" = ["gateway-priv"];
      "10.69.2.1" = ["gateway-pub"];

      "10.69.0.2" = ["gateway.dnt-sirs.com"];
      "10.69.1.2" = ["db.dnt-sirs.com"];
      "10.69.2.2" = ["dnt-sirs.com" "api.dnt-sirs.com" "www.dnt-sirs.com"];
    };
    useDHCP = false;
  };

  security.pki.certificates = let
    DNT-SIRS-CA = builtins.readFile ../certs/CA.pem;
  in [DNT-SIRS-CA];

  system.stateVersion = "23.11";
}
