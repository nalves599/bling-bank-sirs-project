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

BlingBank's cryptographic custom library needs to protect, check, and unprotect documents. The protection of a document is done in such way that it ensure confidentiality, integrity and authenticity. In order to achieve this the following structure was created.

##### Confidentiality

It was used AES(Advanced Encryption Standard) with a 256-bit key in CBC(Cipher Block Chaining) mode.
In order to use CBC mode, an IV(Initialization Vector) is needed.

##### Integrity

The Library can accept either an HMAC(Hash-based Message Authentication Code) or a DS(digital signature).

The HMAC uses SHA-256 as the hash function.
In order to use HMAC, a key is needed.

The DS uses ECDSA(Elliptic Curve Digital Signature Algorithm) with the p521 curve.
In order to use a DS, a private key is needed.

##### Authenticity

Authenticity is achieved by using a DS.
By using a DS, the document can be signed with a private key and then verified with the corresponding public key.
This way, the document can be verified by anyone with the public key, allowing for non-repudiation.

###### Replay Attacks

By itself, the Library does not protect against replay attacks.
This is done by design. The Library is meant to be stateless, so it does not keep track of the documents that were already processed.
It is up to the user of the Library to keep track of the documents that were already processed.

##### Document Structure

With all the properties mentioned above, we can now define how the library will protect the documents.

Starting by the "outside" layer, we can get two parts: the iv and the protected data.

Since the iv is not sensitive data, it can be sent in plain text.
And it can be easily extracted from the protected data, since it is the first 16 bytes.

The protected data is encrypted with the iv and with a aes key.

Before encrypting, the data is divided into multiple parts.
The first part is the content length.
The second part is the content itself.
The third part is the nonce length.
The fourth part is the nonce itself.
The fifth part is the HMAC or DS length.
The sixth part is the HMAC or DS itself.

The HMAC or DS is calculated over the content and the nonce.

TODO: ADD JSON comparison


TODO: ADD img of the structure

#### 2.1.2. Implementation

(_Detail the implementation process, including the programming language and cryptographic libraries used._)

(_Include challenges faced and how they were overcome._)

### 2.2. Infrastructure

#### 2.2.1. Network and Machine Setup

(_Provide a brief description of the built infrastructure._)

(_Justify the choice of technologies for each server._)

#### 2.2.2. Server Communication Security

(_Discuss how server communications were secured, including the secure channel solutions implemented and any challenges encountered._)

(_Explain what keys exist at the start and how are they distributed?_)

### 2.3. Security Challenge

#### 2.3.1. Challenge Overview

(_Describe the new requirements introduced in the security challenge and how they impacted your original design._)

#### 2.3.2. Attacker Model

(_Define who is fully trusted, partially trusted, or untrusted._)

(_Define how powerful the attacker is, with capabilities and limitations, i.e., what can he do and what he cannot do_)

#### 2.3.3. Solution Design and Implementation

(_Explain how your team redesigned and extended the solution to meet the security challenge, including key distribution and other security measures._)

(_Identify communication entities and the messages they exchange with a UML sequence or collaboration diagram._)  

## 3. Conclusion

(_State the main achievements of your work._)

(_Describe which requirements were satisfied, partially satisfied, or not satisfied; with a brief justification for each one._)

(_Identify possible enhancements in the future._)

(_Offer a concluding statement, emphasizing the value of the project experience._)

## 4. Bibliography

(_Present bibliographic references, with clickable links. Always include at least the authors, title, "where published", and year._)

----
END OF REPORT
