{
  description = "SIRS 23 - Infrastructure";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.05";

    microvm.url = "github:astro/microvm.nix";
    microvm.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = {nixpkgs, ...} @ inputs: let
    lib = nixpkgs.lib.extend (self: super:
      import ./lib {
        inherit inputs profiles pkgs nixosConfigurations;
        lib = self;
      });

    overlays = lib.rnl.mkOverlays ./overlays;
    pkgs = lib.rnl.mkPkgs overlays;
    nixosConfigurations = lib.rnl.mkHosts ./hosts;
    profiles = lib.rnl.mkProfiles ./profiles;

    packages.x86_64-linux = lib.rnl.mkMicroVmHosts nixosConfigurations;
  in {
    inherit nixosConfigurations overlays packages;

    formatter.x86_64-linux = nixpkgs.legacyPackages.x86_64-linux.alejandra;
  };
}
