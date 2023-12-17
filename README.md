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

For each machine, there is an initialization script with the machine name, with prefix `init-` and suffix `.sh`, that installs all the necessary packages and makes all required configurations in the a clean machine.

Inside each machine, use Git to obtain a copy of all the scripts and code.

```sh
$ git clone https://github.com/tecnico-sec/cxx...
```

*(above, replace with link to actual repository)*

Next we have custom instructions for each machine.

#### Machine 1

This machine runs ...

*(describe what kind of software runs on this machine, e.g. a database server (PostgreSQL 16.1))*

To verify:

```sh
$ setup command
```

*(replace with actual commands)*

To test:

```sh
$ test command
```

*(replace with actual commands)*

The expected results are ...

*(explain what is supposed to happen if all goes well)*

If you receive the following message ... then ...

*(explain how to fix some known problem)*

#### Machine ...

*(similar content structure as Machine 1)*

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

- [Java 11.0.16.1](https://openjdk.java.net/)
- [Maven 3.9.5](https://maven.apache.org/)
- ...

### Versioning

We use [SemVer](http://semver.org/) for versioning.  

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) for details.

*(switch to another license, or no license, as you see fit)*

----
END OF README
