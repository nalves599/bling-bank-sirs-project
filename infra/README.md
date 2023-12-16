# Setup

## Requirements:
- Nix
- QEMU

## How to run?

1. Add the following entries to `/etc/hosts` file:
```
10.69.0.2	gateway.dnt-sirs.com
10.69.2.2	dnt-sirs.com www.dnt-sirs.com api.dnt-sirs.com
10.69.1.2	db.dnt-sirs.com
```

2. Configure the network inside of the host machine.
```
./configure.sh setup
```

You might need to enable IPv4 forwarding (`echo "1" > /proc/sys/net/ipv4/ip_forward`). Run this command inside a root shell.

3. Start the VMs. To do that, you need to run the commands bellow in individual terminals.
```
nix run .#vm-gateway
nix run .#vm-web
nix run .#vm-db
```

4. Import the CA certificate (`certs/CA.pem`).

Now the VMs should be up and running and you should be able to access the web application at `https://dnt-sirs.com`.

## How to stop?

If you want to stop all the VMs lauched you just need to kill the process by closing the terminals.
And to destroy all the network setup made before, you should run:
```
./configure.sh destroy
```
