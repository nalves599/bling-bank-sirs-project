USER=$(whoami)

function setup() {
  # Create bridges
  sudo ip link add br-priv type bridge
  sudo ip link add br-pub type bridge
  sudo ip link add br-world type bridge

  # Create TAP interfaces
  sudo ip tuntap add vm-gw-world mode tap user $USER
  sudo ip tuntap add vm-gw-priv mode tap user $USER
  sudo ip tuntap add vm-gw-pub mode tap user $USER
  sudo ip tuntap add vm-web-pub mode tap user $USER
  sudo ip tuntap add vm-db-priv mode tap user $USER

  # Setup world network
  sudo ip link set vm-gw-world master br-world
  sudo ip link set br-world up
  sudo ip addr add 10.69.0.1/24 dev br-world
  sudo ip link set vm-gw-world up

  # Setup public network
  sudo ip link set vm-gw-pub master br-pub
  sudo ip link set vm-web-pub master br-pub
  sudo ip link set br-pub up
  sudo ip link set vm-gw-pub up
  sudo ip link set vm-web-pub up

  # Setup private network
  sudo ip link set vm-gw-priv master br-priv
  sudo ip link set vm-db-priv master br-priv
  sudo ip link set br-priv up
  sudo ip link set vm-gw-priv up
  sudo ip link set vm-db-priv up

  # Allow access from host to VMs
  sudo ip route add 10.69.0.0/22 via 10.69.0.2

  # Add NAT rule
  sudo iptables -A POSTROUTING -s 10.69.0.2/32 ! -o br-world -j MASQUERADE -t nat
}

function destroy() {
  # Destroy TAP interfaces
  sudo ip link del vm-gw-world
  sudo ip link del vm-gw-priv
  sudo ip link del vm-gw-pub
  sudo ip link del vm-web-pub
  sudo ip link del vm-db-priv

  # Destroy bridges
  sudo ip link del br-priv type bridge
  sudo ip link del br-pub type bridge
  sudo ip link del br-world type bridge

  # Delete NAT rule
  sudo iptables -D POSTROUTING -s 10.69.0.2/32 ! -o br-world -j MASQUERADE -t nat
}


if [ "$1" == "setup" ]; then
  setup
elif [ "$1" == "destroy" ]; then
  destroy
else
  echo "Usage: $0 [setup|teardown]"
fi
