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

BlingBank's cryptographic custom library needs to protect, check, and unprotect documents. The protection of a document is done in such way that it ensures confidentiality, integrity and authenticity. In order to achieve this the following structure was created.

##### Confidentiality

It was used AES (Advanced Encryption Standard) with a 256-bit key in CBC (Cipher Block Chaining) mode.
In order to use CBC mode, an IV (Initialization Vector) is needed.
AES-256 is considered to be secure by the NIST (National Institute of Standards and Technology), therefore it was chosen.
CBC mode is more secure than ECB (Electronic Code Book) mode, therefore it was chosen.

##### Integrity

The Library can accept either an HMAC (Hash-based Message Authentication Code) or a DS(Digital Signature).

The HMAC uses SHA-256 as the hash function.
In order to use HMAC, a key is needed.
The length of the key is equal to the length of the hash function (256 bits), which is considered to be secure.

The DS uses ECDSA(Elliptic Curve Digital Signature Algorithm) with the p521 curve.
p521 is considered to be secure by the NIST, therefore it was chosen.
In order to use a DS, a private key is needed.

##### Authenticity

Authenticity is achieved by using a DS.
By using a DS, the document can be signed with a private key and then verified with the corresponding public key.
This way, the document can be verified by anyone with the public key, ensuring non-repudiation.

###### Replay Attacks

By itself, the Library does not protect against replay attacks.
This is done by design. The Library is meant to be stateless, so it does not keep track of the documents that were already processed.
It is up to the user of the Library to keep track of the documents that were already processed.
This issue was discussed with the professor and it was agreed that this was not a issue as long as the verification is done by a third party using the library.
It was discussed that the CLI tool does not need to protect against replay attacks.
In the case of the backend, this implementation is done by the backend itself.

##### Document Structure

With all the properties mentioned above, we can now define how the library will protect the documents.

TODO: ADD img of the structure

Starting by the "outside" layer, we can get two parts: the iv and the protected data.

Since the iv is not sensitive data, it can be sent in plain text.
And it can be easily extracted from the protected data, since it has always the same length (16 bytes).

The protected data is encrypted with the aes key and using the iv.

The data is divided into multiple parts.
The first part is the content length.
The second part is the content itself.
The third part is the nonce length.
The fourth part is the nonce itself.
The fifth part is the HMAC or the DS length.
The sixth part is the HMAC or the DS itself.

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
      "balance": "872.22",
      "currency": "EUR",
      "movements": [
        {
          "date": "09/11/2023",
          "value": "1000",
          "description": "Salary"
        },
        {
          "date": "15/11/2023",
          "value": "-77.78",
          "description": "Electricity bill"
        },
        {
          "date": "22/11/2023",
          "value": "-50",
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
Typescript allows us to use the library in both the frontend and the backend.
Allowing for the client to use it in the browser without needing an external tool.
The library is transparent to the user, the user might not even know that the library is being used.

Some cryptographic libraries were used in order to implement the library, such as crypto (webcrypto).

Even though typescript has types, the use of raw bytes is not easily done.
A lot of fight and experimentation was needed in order to get the library to work with raw bytes.

Tests were also implemented in order to ensure that the library was working as intended.
The tests were implemented using jest.
This was a very important step, since it allowed us to find bugs and fix them.

We followed a very modular approach, in order to make the library as flexible as possible.
The multiple functions allow the user that uses it to choose for example, if either a HMAC or a DS is used, use a custom nonce or choose the nonce verifcation function.
This was done without compromising the security of the library.

In the case of the protect/unprotect functions, the library does not care about the content that is being protected.
The library receives bytes and returns bytes.

If it exists, it can be protected!

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

https://www.nist.gov/

----
END OF REPORT
