# A39 BlingBank Project Report

## 1. Introduction

BlingBank provides an online banking platform, accessible via a web application.
The main functionalities are: account management, expense monitoring, and payments. The account management allows an efficient oversight of account balance. Expense monitoring shows the movements corresponding to expenses, in categories. Finally, payments allow a simple way to make bill payments.

### 1.1. Secure Documents
Our secure documents must ensure authenticity and confidentiality of the account data. Therefore, these properties are achieved by a custom library which will be explained in more detail in the following sections.

### 1.2. Infrastructure
BlingBank's infrastructure is composed of four servers: a gateway, a web server, a backend server and a database server.

### 1.3. Security Challenge
The security challenge consists of a new requirement: a new document format specifically for payment orders which must guarantee confidentiality, authenticity, and non-repudiation of each transaction. Additionally, accounts with mulitple owners require authorization and non-repudiation from all owners before the payment order is executed.
With all these requirements, the new document format for payment orders has the following structure:

TODO: Is this the document?
```json
{
    "account": "account-id",
    "amount": "amount",
    "currency": "currency",
    "date": "date",
    "description": "description",
    "requiredApprovals": "required-approvals",
    "givenApprovals": "given-approvals",
    "accepted": "accepted"
}
```

### 1.4 Project Structure
Our BlingBank project has the following structure that will be explained in more detail in the following sections.

[![Project Structure](img/uml.png)]

## 2. Project Development

### 2.1. Secure Document Format

#### 2.1.1. Design

BlingBank's cryptographic custom library needs to protect, check, and unprotect documents. The protection of a document is done in such way that it ensures confidentiality, integrity and authenticity. In order to achieve this the following structure was created.

##### Confidentiality

It was used AES (Advanced Encryption Standard) with a 256-bit key in CBC (Cipher Block Chaining) mode.
In order to use CBC mode, an IV (Initialization Vector) is needed.
AES-256 is considered to be secure by the NIST (National Institute of Standards and Technology), therefore it was chosen.
CBC mode is more secure than ECB (Electronic Code Book) mode, therefore it was chosen.

##### Integrity

The Library can accept either an HMAC (Hash-based Message Authentication Code) or a DS (Digital Signature).

The HMAC uses SHA-256 as the hash function.
In order to use HMAC, a key is needed.
The length of the key is equal to the length of the hash function (256 bits), which is considered to be secure.

The DS uses ECDSA (Elliptic Curve Digital Signature Algorithm) with the p521 curve.
p521 is considered to be secure by the NIST, therefore it was chosen.
In order to use a DS, a private key is needed.

##### Authenticity

Authenticity is achieved by using the also the HMAC or the DS.
With the HMAC, the document can be verified by the receiver, ensuring authenticity.

###### Non-Repudiation

By using a DS, the document can be signed with a private key and then verified with the corresponding public key.
This way, the document can be verified by anyone that has public key, ensuring non-repudiation.

###### Replay Attacks

By itself, the Library does not protect against replay attacks.
This is done by design. The Library is meant to be stateless, so it does not keep track of the documents that were already processed.
It is up to the user of the Library to keep track of the documents that were already processed.
This issue was discussed with the professor and it was agreed that this was not a issue as long as the verification is done by the third party using the library.
It was discussed that the CLI tool does not need to protect against replay attacks.
In the case of the backend, this implementation is done by the backend itself.

##### Document Structure

With all the properties mentioned above, we can now define how the library will protect the documents.

TODO: ADD img of the structure

Starting by the "outside" layer, we can get two parts: the IV and the protected data.

Since the IV is not sensitive data, it can be sent in plain text and it can be easily extracted from the protected data, since it has always the same length (16 bytes).

The protected data is encrypted with the AES key and using the IV.

The data is divided into seven parts:
- The first part is the content length.
- The second part is the content itself.
- The third part is the nonce length.
- The fourth part is the nonce itself.
- The fifth part is the HMAC or the DS length.
- The sixth part is the HMAC or the DS itself.

The HMAC or DS is calculated over the content and the nonce.

Note: The protected data is encoded to base64 in order to be sent as a string,
since JSON does not support raw bytes.

<details>
<summary>Comparasion of unprotected and protected file</summary>

<details>
<summary> Unprotected file </summary>

```json
{
    "account": {
      "accountHolder": [
        "Alice"
      ],
      "balance": 872.22,
      "currency": "EUR",
      "movements": [
        {
          "date": "09/11/2023",
          "value": 1000,
          "description": "Salary"
        },
        {
          "date": "15/11/2023",
          "value": -77.78,
          "description": "Electricity bill"
        },
        {
          "date": "22/11/2023",
          "value": -50,
          "description": "ATM Withdrawal"
        }
      ]
    }
}
```
</details>

<details>
<summary> Protected file </summary>

```json
{
  "account": {
    "accountHolder": [
      "JphVj5rENm+JTNODC2A+qKqRI2oOMtesmLVOSM0lyOn7u8TgX8DzJes2eqB+XKwwDE/0KLGUQKf3SmQxcD4KbG1wSWDYy6t3Wa+cuZOeyzaXK877/YKQjM2mCA5UICFb0mjmaUJif3i6lW8NTtBvyQJJIk97ytfBCDQ4QDq5SOCTXl1k7/qOhLgtbP6CRDCX+86PME0be4J0WKYGxEh7UiSEto4EpJPZNl+vgKWM8z/vvVsly4OMuzgKFe+D9TRA"
    ],
    "balance": "A7RCdYBUpmP8WfgHy7isAAB9ekIyX2VR5bs+MyhhCFoXQL9w07oLyk62dFR1EVqy2xT5Z6QhzeOuPTAJCHT9UySRYTqb5eMnPJAnOkB43z2oZsNaG4PbWKlIRwgmx/DQn0FbgTve3OYRogZwqmB/pZ7V1NG4Q5D77mM8bJ7R8f7AtQG41pQzA3yca3wUnnr+nSKPL4IkHFtgo/Juw2i/i45/2GPzne6IPvaD6ttjI5WKcJZj9tACe9Dc0s5Rq01R",
    "currency": "7v6VZfTPexleY7tQdpwX9qwe9qY4mPJLNsJ/OQZzFNncmwDUwuDkgHRuEp+rDFpFbuI/2s6TiVB3NoXutS7o+DLqb0/ob+kXj+QiGIRR9xd3R2uK/R7XCljGg8ff2ieULUWOgZfsrCIcIJuHOqVJnBpZB6LVuw6J9hjZ4b6vE0J20ccgQpuDngBcThG3sipY35fz2A9+4iYd8u2juwiaSYELl0uolQ2MXHiZVX5EmZ8yrBxhNb1Y1z7fGcoqnzJE",
    "movements": [
      {
        "date": "q9GufmJMsGCKWsTzxIstiYY0IY6w8eaI2Jdhjzj8182L1R1BuBwg5jbrnq1j1/L5ajQR7dmPUrm3zFVQE+o42UjChKwwB13mkZyfDcvjUuGJhlZFV2Its42znu0a/RoxlSAwEUvLTIJB+77LFZwoMCmsv7gzNXOWSLQ9ebxva/pDa6SHbVaLS5/HdIb3Fg3kTnXJ8Tfe2EjkpabNvWcfywvAQ0Xoa1NMoyaU2h+oRf/dYNk1/9aEf0WCXyzwhegn",
        "value": "zkYnI2CUmJorG0UKv42T10UyZ3zojAUklPJEZc/VDJXkIODyc6rNL/PQq41c7vLj3TyZW5EhZVy+FD135ZhrvMs2WW/OIwW3o7tAU9eSIb9RB3YqFrEqeS+d9ghdumVxlkvb0XiM7IGjmRGnsYWRCvgdgryQqzSeNdDllEceLwGSeQxHPnunrOb5SpT3I7HceCNEu3yjG4SD65WKfIfgJWaA+nNivKzAmC+j6u4kG16u+10P1KVDIMoiIvOpc/E2",
        "description": "ZMpRkOUTPkB3o26pFgUWtMc3ENjAIX3by1eh2Y8tMgo0N10eDilTVPtWOrNRYXYX7H/0iDze0icX9qe/g0CMKpMM6PFMX446sLndQNeaCZJcp0nC6fFhPO7J1eXmdFVoCsWVggZpngdNgxkfI8xlog90PTsbMN3434ozkq5QfXkhgIt+vfDBWV3t3oZopebYlzHnyZhmHBFJStpeCLafaufwOqWCwCaP0CLFt7iBMax/wmpe33adLgM5y9NSs37w"
      },
      {
        "date": "rs2CFcoRggnxZOLHzMpTRVz85/zlBDianMF1qRT7BsioDFOuGJ86uN2jfxd7yEcdoNvpqOtAFzhxtFwFGCaqiY1wGI9p16CDyk4jFHt8ONz4amKh2aZ5Gv0jbuimoV7kzuVSP0YUZX3nDaUcE1XZdatfFNVLeUIeb5Rgr3zJeGLUltvZe07aukwF5Nbjpe7y5thXYE+i3n9D2HNtaqiw/HORtYKdtzHlj4jrdvN9+nh96YoZkN+MEL1jwyYemRKM",
        "value": "3Vd5EFNvm1y6dkDeoF8xS/YMLk1/Fd+IGzJZw0a+lAgtTewJUpNbj3MiPoWFA5UXYzQUcsM0HQbmeibihVNJqrUHRfnCfPsSJoY1wWHOTvp1OXwEW8mJFhpHi3Csp0CQKeVun3peaHng39Oa/TXQVqXEM5GhSucQEuV443ch/t3oQDq6gstmTCztyaxw1y0vz2x/efQ4rLKplQ90D6MWhI3DL5ZqrT4xuEpRAWTrSWJaTGdo+Gv9FzXqwU+liro7",
        "description": "gX6O0uD8E5BkhQi5mfvHuPGLVM6/YChf6JiZXCnAxfgeuY+MTMU2/dD2LgHSfPJjDeqgXIGRitmtsguC7loP900aNil4lJ34GuhLjqYx2/CMbEw5R80dL2/wwgLFEQ5nBR3iE7EKPrnSr0mL7YFS3leHnCDbhYvRoN6aGqZfNsrfCAkcj3t+AG3sXk1Qxs8EgyilYd9JAL9SZFKImnzQGxzOUCIwGKtuE6BeMGdHxTa65c+0dvNJf09GMABoFe4h"
      },
      {
        "date": "33n6FfXwsJb+GbXBOhOSgcLeKjk53iG2UcfDv1VU2hax2lPlxN/j4azyBi/WS7uzJJ7mmrmpY8TRBfMTIc4lmBp/jouzlFRDXekHxJTCXV034Q/kQkrioo4wbxgJ+b+OP72SHfHJN0G+jMwxhjo7d4pKOQnrb+hbcjLKcbOaIqDODygpanqlYBrnfdRhb3A/ofvWj7P8MKO/8t46/zDJzI31I0896tL+R9KJNtmWT+2zA89sOzeaVIgMhmTlX78t",
        "value": "elcr3qc1nKkhwXDaSY+lE3599QLt89JG7UXH4er1hh6azueAUiJ5tK7TVqnN1yPb22O4PMoX8Yfo/5En9qWNCsW06pDFZYnr+YthAMZY6lzrSGY21IRDCOaEHLtNenQk/zDQ1rrXjd5E3sXoJUCKQ4qUQJxUx1CKOedSMWoyVcxrPZ1Sy8rbcBY/osbQFWVSPGRmeM4/CJBDEMPVaSzBVURFWvgd9/JsJXkvvokMDI37T5fCJ5Hk4baYHTRtxeMz",
        "description": "xjdcFehm+zQ+uA5evM4h0/Wd1zwVvr9X5LQljVWmQHGR6Zz/UtF4rYAb/6XKKTtEuAuZtRCSfp+C6gQPQXIhMveYRsKAb7IhDos9ZVihRPoXOFlTA51NQcb+oTZ9SiRxSXLADLbMI3oaT3BD4TpSNohwMvwvwyCI7Dn7G/2kROl98pf3S30gUelj1WbtYncG24QnF18gAr680xag0ka2npp0Qd9qRlVwZI6NLiSWO2zlP2Ra+u+a+L2ht8yvBtGE"
      }
    ]
  }
}
```
</details>
</details>

#### 2.1.2. Implementation

We decided to use typescript as the programming language for the library.
Typescript allows us to use the library in both the frontend and the backend, allowing the client to use it in the browser without needing an external tool.
The library is transparent to the user, the user might not even know that the library is being used.

Some cryptographic libraries were used in order to implement the library, such as crypto (webcrypto).

Even though typescript has types, the use of raw bytes is not easily done.
A lot of fight and experimentation was needed in order to get the library to work with raw bytes.

Tests were also implemented, using jest, in order to ensure that the library was working as intended.
This was a very important step, since it allowed us to find bugs and fix them.

We followed a very modular approach, in order to make the library as flexible as possible.
The multiple functions allow the user that uses it to choose for example, if either a HMAC or a DS is used, use a custom nonce or choose the nonce verifcation function.
This was done without compromising the security of the library.

In the case of the protect/unprotect functions, the library does not care about the content that is being protected: the library receives bytes and returns bytes.

If it exists, it can be protected!

### 2.2. Infrastructure

#### 2.2.1. Network and Machine Setup

##### Virtual Machines

NixOS was chosen due to its ease of use and its ability to be easily configured and replicated.
With one simple configuration file, we can easily replicate the same environment in multiple machines.
The different machines will only differ with the network configuration and which services are running.

QEMU was chosen due to the fact that it is faster than most other virtual box solutions (since it has direct kernel integration), and for our purposes, it is more than enough (since we are not using any graphical interface)

##### Docker

Firstly the group wanted to use Docker to run the different services, in a way to avoid using virtual machines.
However, this approach was not allowed (due to the easy configuration of Docker, which would not be a challenge for the project).
In a real world scenario, Docker would be the best option, since it is easy to configure and replicate, and it is also very lightweight.

(_Provide a brief description of the built infrastructure._)

(_Justify the choice of technologies for each server._)

##### Technologies Used

As said previously, the library, the CLI, the backend and the frontend were all developed in Typescript.
This was done in order to have a single language for the whole project, allowing us to easily share code between the different parts of the project.
At the first weeks of the project, the development was being made in Java, which was easier to implement the cryptographic library. The problem with Java was that it was not possible to use the library in the frontend. It seemed odd for a bank website to not be able to show the accounts information at the browser. A possible approach was downloading the protected file and use another tool to unprotect it. This seemed like a bad approach, since it would be a bad user experience. By having Typescript, all the project spoke the same language, allowing for the user to see in a secure way the information about his accounts.

The database was implemented using MongoDB, which is a NoSQL database. This was done in order to have a more flexible database, allowing us to easily change the structure of the database without having to change the code.

#### 2.2.2. Server Communication Security

At the start, the server has a Master Key that it stores. This solution was implemented in order to avoid the complexity of having to distribute keys. This master key is used to encrypt the account keys hold by the server in the database. This solution is used to simulate an account manager on the bank side. In this case, as this account manager is the server itself, it needs to have access to the account keys. The optimal solution would be to have a separate server that would hold the account keys and would be responsible for the encryption and decryption of the account keys. This solution would be more secure as it would be more difficult to compromise the account keys. However, this solution would also be more complex to implemenhet and would require more resources.

At the moment of registration, a user accesses the registration page where he inputs his email and username. The server will receive this data and will generate a shared-secret and sends it to the user email. This shared secret is stored in the database encrypted with the Master Key. 

Once again, this solution is not optimal as the email can be compromised. However, in the real world, in order to open an account, the client would need to go in person to the bank where he would physically get the security codes. In this case, the email is used as a substitute for the physical presence of the user and it is assumed that this environment is secure and that the email is not compromised. After this, the server stores on the database the user e-mail and the hash of the password in order to guarantee that the password is not compromised in case of a DB leak. Additionally, in the real world there is no concept of registration without an being the holder of an account at a bank. In our BlingBank system, it is possible for a user to register and never open an account. This solution was implemented as it would be necessary for the account holders to have security codes to access their accounts which is done on registration. Since every user is allowed to have multiple accounts at BlingBank, this solution helps us to facilitate the navigation of the user on the system, allowing him to easily access his accounts info.

At the moment of login, the user inputs his email. Upon receiving this request, the server will generate a challenge based on the user-server shared-secret and returns it to the user encrypted with the shared-secret. The client will then answer the challenge and will return the answer encrypted with the shared-secret. If the answer is correct and accepted by the server, this will generate a Session Key and a JWT Token and will return this two elements to the client but the Session Key will first be encrypted by the shared-secret. Finally, after the client receives the session key and the JWT token, it will send its public key to the server encrypted with the session key and the server will store the client's public key encrypted with the shared-secret on the database. If there is an existing entry on this login table to this user, it will first be deleted before a new one is added.

We opted for this complex round of communication in order to ensure that the user who is authenticating is who he says he is and to be able to safely change all keys necessary for future possible operations.

After the moment of login, it is now possible to access all functionalities of BlingBank, where we can create accounts and access their info, view the movements of an account, create and authorize payment orders and view the payment orders of an account.

Starting with the creation of an account, the user will be able to select the account holders of that account with the constraint that he needs to be one of them. This is not an optimal solution as this allows any user with bad intentions to create an account with other users that don't want to do so. However, this is an online bank and due to the time frame we assumed that the users registered and authenticated are not well intenteded as in the real-world, in order to open an account with multiple holders, all of them would need to go to the bank in person. With this in mind, we proceed to the next step where the client will send this data to the server encrypted with the session key along side with his valid JWT Token. Upon receiving this request, the server will generate N+1 Shamir Keys, being N the number of account holders. The other extra key is the server's key for that account as the server will work as an account manager. This key will then be stored on the database encrypted with the Master Key. The other N shamir keys will be send by email to the respective users encrypted with the shared-secret of each user.

We chose to used the Shamir Keys method as it allows for more security. With N+1 keys, it is necessary to have at least 2 keys to access the account data. This means that the server will not be able to access the accounts' info alone and therefore we prevent the confidentiality of the data in the case the server is compromised.

To access an account's movements or payments, the authenticated user will first need to select the account he wants to access. For this, a simple get with the user ID and the JWT Token is made to the server which will return all accounts which the user is a holder of. After this, the user will select the account he wants to access and will send the account ID along side with his shamir key encrypted with the session key plus the JWT Token (the message sent is: accountID+E(UserShamirKey, SessionKey) + JWT Token). When the server receives this request, it will get its shamir key for this account from the database and will retrieve the account info from the database using these 2 shamir keys. The server will then return the account info encrypted with the session key to the client which will decrypt it and display it to the user.

To create a payment order, the same procedure as the creation of an account is followed. - TODO COMPLEMENT

Finnaly, to sign a payment, i.e. to approve a payment order, the client will transfer an hash of the payment and then use the CLI to sign it. After this is done, it will send the signed hash to the server encrypted with the session key. The server will then verify the signature and if it is valid, it will update the payment order on the database with the new signature. When a payment has all necessary signatures, it will be executed and the server will update the account movements and the account balance.

(_Explain what keys exist at the start and how are they distributed?_)

### 2.3. Security Challenge

#### 2.3.1. Challenge Overview

The security challenge consists of a new requirement: a new document format specifically for payment orders which must guarantee confidentiality, authenticity, and non-repudiation of each transaction. Additionally, accounts with mulitple owners require authorization and non-repudiation from all owners before the payment order is executed. To achieve this, and particular the non-repudiation of each transaction, we need to add new keys to the system, namely the users' asymmetric keys. This keys are required to sign the payment orders and to verify the signatures. 

(_Describe the new requirements introduced in the security challenge and how they impacted your original design._)

#### 2.3.2. Attacker Model

The authenticated users are considered fully trusted as they have the ability to create accounts with multiple holders and can select who this holders are. The server is considered partially trusted as it has access to the account keys and can decrypt the account info. However, it is not able to access the accounts' info alone as it needs the users' keys. The attacker is considered untrusted as he does not have access to the users' keys and therefore cannot access the accounts' info.

(_Define who is fully trusted, partially trusted, or untrusted._)

(_Define how powerful the attacker is, with capabilities and limitations, i.e., what can he do and what he cannot do_)

TODO - explain how the attacker can compromise the system - maybe related to the infrastructure?

#### 2.3.3. Solution Design and Implementation

For the server to able to have the user's public key, it was necessary to update the login phase to include a phase where the user sends his public key to the server and for the latter to store this information.

Regarding the structure of BlingBank, not much change. The accounts were extendended to hold a list of payments and the notion of payment was created. Since when a payment is approved, it is executed and therefore transforms into a movement, the new secure document format for payment orders was created in way that it could be easily transformed into a movement. This way, the server can easily transform a payment into a movement and update the account movements and balance.

The new secure document format for payment orders has the following structure:
```json
{
    "account": "account-id",
    "amount": "amount",
    "currency": "currency",
    "date": "date",
    "description": "description",
    "requiredApprovals": "required-approvals",
    "givenApprovals": "given-approvals",
    "accepted": "accepted"
}
```

(_Explain how your team redesigned and extended the solution to meet the security challenge, including key distribution and other security measures._)

(_Identify communication entities and the messages they exchange with a UML sequence or collaboration diagram._)  

## 3. Conclusion

(_State the main achievements of your work._)

(_Describe which requirements were satisfied, partially satisfied, or not satisfied; with a brief justification for each one._)

(_Identify possible enhancements in the future._)

(_Offer a concluding statement, emphasizing the value of the project experience._)

## 4. Bibliography

(_Present bibliographic references, with clickable links. Always include at least the authors, title, "where published", and year._)

https://www.nist.gov/

----
END OF REPORT
