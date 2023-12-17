# A39 BlingBank Project Read Me

## Team

| Number | Name              | User                             | E-mail                              |
| -------|-------------------|----------------------------------| ------------------------------------|
| 99202  | Diogo Melita     | <https://github.com/d-melita>   | <mailto:diogo.melita@tecnico.ulisboa.pt>   |
| 99291  | Nuno Alves      | <https://github.com/nalves599>     | <mailto:nuno.f.alves@tecnico.ulisboa.pt>     |
| 99341  | Tomás Esteves  | <https://github.com/Pesteves2002> | <mailto:tomasesteves2002@tecnico.ulisboa.pt> |

![Diogo Melita](img/diogo.png) ![Nuno Alves](img/nuno.png) ![Tomás Esteves](img/tomas.png)

## Contents

This repository contains documentation and source code for the *Network and Computer Security (SIRS)* project.

The [REPORT](REPORT.md) document provides a detailed overview of the key technical decisions and various components of the implemented project.
It offers insights into the rationale behind these choices, the project's architecture, and the impact of these decisions on the overall functionality and performance of the system.

This document presents installation and demonstration instructions.

## Installation

To see the project in action, it is necessary to setup a virtual environment, with 3 TODO: networks and 4 machines.

The following diagram shows the networks and machines:

*(include a text-based or an image-based diagram)*

### Prerequisites

All the virtual machines are based on: Linux 64-bit, nixos-23.05 and were ran using QEMU.

[Download NixOS](https://nixos.org/download)
[Download QEMU](https://qemu.org/download)

NixOS was chosen due to its ease of use and its ability to be easily configured and replicated.
With one simple configuration file, we can easily replicate the same environment in multiple machines.
The different machines will only differ with the network configuration and which services are running.

QEMU was chosen due to the fact that it is faster than most other virtual box solutions (since it has direct kernel integration), and for our purposes, it is more than enough (since we are not using any graphical interface)

#### Docker

Firstly the group wanted to use Docker to run the different services, in a way to avoid using virtual machines.
However, this approach was not allowed (due to the easy configuration of Docker, which would not be a challenge for the project).
In a real world scenario, Docker would be the best option, since it is easy to configure and replicate, and it is also very lightweight.

### Machine configurations

In order to install the multiple machines, a guide was provided [here](https://github.com/tecnico-sec/a39-diogo-nuno-tomas/tree/master/infra).

#### Machine 1 (Gateway)

This machine runs the gateway service, which is responsible for routing the messages between the different networks.

The expected results are a properly configured machine, with the gateway service running.
Allowing the other machines to communicate with each other.

##### Common Problems

It seems that the gateway may not work as intended, if the docker service in the host machine is running.
Disabling it, seems to fix the problem.

The first installation of the machine may take a while, since it needs to download all the dependencies.

#### Machine 2 (Web Server)

This machine runs the web server service, which is responsible for serving the web application.

The expected results are a properly configured machine, with the web server service running.
If configured correctly, alongside the gateway, the web application should be accessible from the host machine.

##### Common Problems

The first installation of the machine may take a while, since it needs to download all the dependencies.

#### Machine 3 (Backend/API)

This machine runs the backend service, which is responsible for serving the API.

The expected results are a properly configured machine, with the backend service running.
If configured correctly, alongside the gateway, the API should be accessible from the host machine.

#### Machine 4 (Database)

This machine runs the database service, which is responsible for storing the data.

The expected results are a properly configured machine, with the database service running.
If configured correctly, alongside the gateway, the backend should be able to connect to the database.

## Demonstration

Now that all the networks and machines are up and running, ...

*(give a tour of the best features of the application; add screenshots when relevant)*

```sh
$ demo command
```

*(replace with actual commands)*

*(IMPORTANT: show evidence of the security mechanisms in action; show message payloads, print relevant messages, perform simulated attacks to show the defenses in action, etc.)*

This concludes the demonstration.

## Additional Information

### Links to Used Tools and Libraries

- [NixOS](https://nixos.org/)
- [QEMU](https://qemu.org/)
- [Node.js](https://nodejs.org/en/)
- [Typescript](https://www.typescriptlang.org/)
- [Vue.js](https://vuejs.org/)
- [Vuetify](https://vuetifyjs.com/en/)
- [Express](https://expressjs.com/)
- [MongoDB](https://www.mongodb.com/)
- [Shamir Secret Sharing TS](https://www.npmjs.com/package/shamirs-secret-sharing-ts)

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) for details.

----
END OF README
