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

### CLI - Command Line Interface
To easily demonstrate that the library is working as expected, a CLI was created. Here we can protect, check, and unprotect documents following the requirements of the project. To do so, we must first setup this environment following these steps:

1. Install the library in the machine that will be used to run the CLI
```sh
$ cd library
$ npm i
$ npm run build
$ npm pack
```

2. Install cli dependencies
```sh
$ cd ../cli
$ npm i
$ npm run build
```

Now that the environment is ready, we can start the demonstration. On the `cli` directory we can see that we have a sub-direcotry called `files`. In this directory we can find the keys necessary to perform the operations as well as the input file that will be used to test the library.

The following commands should be run in the `cli` directory.

#### Help
To see the available commands and their usage, we can run the following command:
```sh
$ ./blingbank help
```

#### Protect
To protect a file, we can run the following command:
```sh
$ ./blingbank protect files/input.json files/protected.json files/aes.key files/priv.key
```
As we can see after running the `help` command, the `protect` command takes 4 arguments:
- `input.json` - The file that will be protected
- `protected.json` - The file that will be created with the protected data
- `aes.key` - The file that contains the secret key that will be used to encrypt the data
- `priv.key` - The file that contains the private key that will be used to sign the data

After performing this command, we can see that the `protected.json` file was created and it contains the protected data.
We can see that only the multiple values of the json file are protected, and not the file as a whole.

#### Check
To check if a file's data is protected or not, we can run the following command:
```sh
$ ./blingbank check files/protected.json files/aes.key files/pub.key
```
As we can see after running the `help` command, the `check` command takes 3 arguments:
- `protected.json` - The file that we want to check
- `aes.key` - The file that contains the secret key that will be used to decrypt the data
- `pub.key` - The file that contains the public key that will be used to verify the signature of the data

After performing this command, a message will be displayed informing if the data is protected or not. In this case, the message will be true since the input is the output of the `protect` command. If we change the file to be checked to `files/input.json` and run the command again, the message will be false since the data is not protected.

#### Unprotect
To unprotect a file, we can run the following command:
```sh
$ ./blingbank unprotect files/protected.json files/output.json files/aes.key files/pub.key
```

As we can see after running the `help` command, the `unprotect` command takes 4 arguments:
- `protected.json` - The file that we want to unprotect
- `output.json` - The file that will be created with the unprotected data
- `aes.key` - The file that contains the secret key that will be used to decrypt the data
- `pub.key` - The file that contains the public key that will be used to verify the signature of the data

After performing this command, we can see that the `output.json` file was created and it contains the unprotected data. Since we are using the same keys as before and the input file is the output of the `protect` command, the data will be the same as the input file. We can check this by running the following command:
```sh
$ diff files/input.json files/output.json
```
This command will not output anything since the files are the same.

#### Automatization

All of these commands can be run automatically using the script `demonstration.sh` under the `cli/sripts` directory. To do so, we must first change the permissions of the file to be executable:
```sh
$ chmod +x demonstration.sh
```
After that, we can run the script:
```sh
$ ./demonstration.sh
```
This script will run all the commands described above and will output the results of each command.

#### Security Mechanisms
To demonstrate the security mechanisms, we will perform simulated attacks to the data. To do so, we will use the `protected.json` file that was created in the `protect` command. For instance, if we add a char at the beginning of the protected value of the field "balance" and run the `check` command, a message will be displayed informing that the file is not protected.

If we even try to unprotect the file, an error will be thrown saying `Could not unprotect file files/protected.json: The operation failed for an operation-specific reason` which is due to the file being tampered with.

These instructions above can be ran automatically using the script `attack.sh` under the `cli/sripts` directory. To do so, we must first change the permissions of the file to be executable:
```sh
$ chmod +x attack.sh
```

After that, we can run the script:
```sh
$ ./attack.sh
```

Note: This script assumes that the demonstration script was already ran, so it uses the `protected.json` file that was created in the `protect` command. Additionally, it is imporant to have the `jq` command installed in the machine, since it is used to edit the json file.

### Web Application - TODO

Assuming that the machines are properly configured after following the installation instructions, we can now demonstrate the web application.


*(give a tour of the best features of the application; add screenshots when relevant)*

```sh
$ demo command
```

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

For additional tools and libraries, check the dependecies installed in the various `package.json` files.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) for details.

----
END OF README
