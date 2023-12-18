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

It was used AES(Advanced Encryption Standard) with a 256-bit key in CBC(Cipher Block Chaining) mode.
In order to use CBC mode, an IV(Initialization Vector) is needed.
AES-256 is considered to be secure by the NIST(National Institute of Standards and Technology), therefore it was chosen.
CBC mode is more secure than ECB(Electronic Code Book) mode, therefore it was chosen.

##### Integrity

The Library can accept either an HMAC(Hash-based Message Authentication Code) or a DS(Digital Signature).

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
<summary> Comparasion of unprotected and protected file </summary>

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
      "5mXdLOKu3TJvN7LB8pUqiYGG3LruRA+iH0WuOXbk7feDVNSc7aa/rHo6fy4eN7XnBKB4HKAzF/rNVE9hjmfaeqBq8LNTWliwlr97ZYxgRIeKK1U2AMm/07XpqP3KcfZlruquNR/KstDcDusKC8DJNCkjRF4VBGOAHlfO/D3OCIbGpmX5yiTiPnuCMCPHa6B2zGUQ8iz3xfIgl2aJWNIbH1565hvsFiCmsnpGArH9nBYzCiAolGbqy0ZK562xawZN"
    ],
    "balance": "IgvgQIiiAfuXecS+aHOJLtqluaQ5Z9nL7thUL4MDwlVqIUCIENNCSEEcz3MkcRbnKAiF+MXvh+/yw5upooB/I++hm9LW6i6pWC5puv5+9adTm7sv9jsi/jkZTc24l3bxoroX5FI9i9NX+KqVBV6HZP7H9Me07FXl5oNOGXzn7utpDzmDX3rX5IBdY8/hDRYzlWAFWJxI6R39bE+25U+7Z5KEf0vaVCVgWmcaHBZFVFjqLeMB5GPm750ZRslaXCkP",
    "currency": "unHvXb5MgKITnjlKKMO2jZMVLQUwivoSjwNorq9Bf+ksP+f8zDwtUpxudIsaxZcHe7mIkKwJbXDdotTPkgLVWklbV5fUkGQU6yrpD5V3y1xK4NbvncHtZT8/lRiYjaWzFGIoRZ5wPQ3dHLGlVVnUuWmR8KpLK7ns/pFTZLaLwwreHFs6iPCBxngoYXkv81ZFA77IncRHz7knI48FHnIl//LTwngaIJYeXZWKPWNGrypGTc/Cb/h/QnJtYsBR0RGe",
    "movements": [
      {
        "date": "bH5plULBkVTNl/6ZutVCQGhhVGoi2bhw0QCueaT4pXgjKmYN/jEDl9pUuxOwAZ/bDBu0TZI/m1sZX/LA/KavUdyp7+Ff5U9GTXHEDrrKQvx1UYQGOok3wuFSxsIHjrC+wU1yPsWRjBXrm9IatdABFY7gHN+pGzzrncjV9X1g77DEoSIPrVA1xId5NPiYRllmSTmEyS3qZdPnx9VJnQqGI6GvCaQwToUvDb5Zt2ANWe84yu7/gpAvSQqTBqNw60ra",
        "value": "7VwhR0gipCnbjC/2aGA8rFjOXqyK3r84eBjlsYQZeD6URrQ03VP6L5UCjm5ceS9siLGInyS5YTcl5Km5WMExQ1Ci4x8/ogE1W8BxFpC2ZLq95W3WNL1VRPV+4+GYi40Oly7wsIyUn7aXJm38JT7SbIylZnSgxxwXCbGcJ514glvnHLFt9p5nedq0MfHElLC6T3Yq65kv5U1h8XzZwDaneWJH39nebYL5iyuG+K69oK5PFU4RCcSHhNpg/9LOywc5",
        "description": "fKN+Thxu9GyWvnq/GM/DoQW0LMgYYenT9LN9TvFehA2d6D13fDm7akVgWZWmQbdms+KpWWXT19YJysjIifE10C3aMJrX15UVEaxQA0i3BrNrPVxXsYHDlTNSeDmSaAYNcvXhNEkGKm//AIQuvbvnihFuToha6qEswsecWra5GtkBczyr6Vd6fxBVQW2jEVdWVbZx21MW+MWKySg2Y+AfnnEjem4hokJrmpw3Jij8gA90OCif67jaQoR/jSZgG3KR"
      },
      {
        "date": "kbokuHgWTi6lDeB2oNtyRlEEePx6Sl2gknkWRzoZXSXjFUdb1f/rRt4xCMnu1cmn1xfWqKYrPiocTvX08z0WoSHyw9Sz5p82F25sUq2W6ucN4oix3CQk7SkjIS7N9NqME1yo94WCFOP3uIpcI5u3zCSfA9G+tlTjdgO/8SFvDIVj+d4KF2GANVhPQqvTiYINSvPzMx3oUolND6/ytT2k6waZWFxnkRSV5TtQ1n8y0pBbicRYI0ysw97fz+KcFr+K",
        "value": "5bMHX6qteu5k702oJE0IhChkGyn3VSIA82v3iptfQirybJfYZcLadG6RrPDABwxRIFEinGuwO4hrXXHjMUSlDM0OAmvG7WJuhtVPW1grVsTdBapyE0rTh7W0O7ay6RGp3yS1o14xGlTJ7pTYAa80OI4B/IzRbkq1aD3AV9ZgoISdkWAmug+GL+pgE5odiSTn9jqEn4Wv77HMwi0XMo+Ozc02XGOw5YJdDa83XWtaCKiGEHshDhHUpDdWACRwqSMW",
        "description": "U2yI272LM5/9hQ7OrYrpjEHQIm8NZQ+WzghsvtQnNtMG81i2XSNvxqhXaPe52VN9x/pdB+q+BQGfDnba4y2u9FBy953IUYwJu6jIdniPg+kTsnNLsRN1j0W4Q/kYTfRKXwJYmsLkCxRzWrC2ieRHcIQbEPo6/NmoVHs78vzahibn9Ie4fogE33YsA+AGFfSO/gi4G8ic+dnNIZM1j/EN/osQHyD8AerLkrDHhmlTDcQCPkD7pndjyldYE48B6m9D"
      },
      {
        "date": "GupZ9imRkY3PxMYNk3EBDeTBJd6/khACbMtp1n+QbfNHxp+RQTJLwfr/E8oGB5L4VlKz5xaooDOZkgIfZWU76u1WzmQp9BbmGPJawj4xVYHWF8eLau0DjzrAl+8XfWw3SPmbC1wrAh0UlOqc8HNAQZ6tz2rPr4mgmyx9JJDbUqY0VTE15Hgaz2pAfwEY+X3hZkOEp+OHW5r48U8/JeLKu/H1OAO0S40eCfd7CRQ4WaFWtZoDV4yg5nlLs6tTPjqd",
        "value": "JqcBNLwBFv4Bq0KYcug2uwkLEuRjD2dFbE5mr9TFf7wpgeYYsZrRLrkd3j3WbBPV8jo08v9fuBHle7GJfpbnJepoIoR8NiP4AqtO6dINBKbqBoMASIydGR5JwUF83HXPN+YGvsEob09L8XCwDcafH7TLy/PVfxMShF14AuwuFKsG4fgpi34+0b3RbsdZPApzwaWnkte6b+K3M0fVosWGtO1eLRQxno6fVHVXFpEvCdy0G2TS4P4QozcsSa5TE3CV",
        "description": "moKGvpUQPdzbEXTy5Dn05J13G1AHNnhr+oxeL5oBKrUIgQIvSVk84rrOe7fXzaucgesW4sxXTM8aA+D74SBMISD1+u8Fqg8CgCBGpzQ7iMVf0eLcsbT34uOCLzvSXNBn0r9EzSy6JdgRhO9u++cfLqcT6UjjZZQQF9jEsmwVHuQRpFmRaP5/nHUPWLXgQ3kZKa6Dya276Ub1cAuCPu/SbquhNs1i9YK+kjM7zI9S5FcgtSPNu1e7bBnBxAGERVRp"
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
