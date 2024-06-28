# HashEncrypt

## Overview

HashEncrypt is a password management tool designed to help users generate unique and strong passwords for different websites while maintaining a simple and memorable password scheme. By utilizing a combination of user-provided main phrases and specific symbols related to each website, HashPass generates unique hashes for login credentials, ensuring security without sacrificing ease of use.


### Usage

1. **Set static Salt (Optional)**: Users can choose to set an static salt for hashing, either manually or automatically within the app settings.
2. **Define Main Phrase**: Enter a memorable main phrase that will serve as the basis for generating passwords.
3. **Add Specific Symbols**: For each website, append specific symbols to the main phrase to create a unique combination.
4. **Generate Password**: HashPass will generate a unique password based on the main phrase and symbols provided.
5. **Copy and Use**: Copy the generated password and use it for logging in to the corresponding website.

### Screenshots

<img src="https://github.com/tadassolys/HashEncrypt/assets/103380760/4d6868a7-df52-4545-ab72-f0fc1cf46f91" width="300" alt="Screenshot 1">
<img src="https://github.com/tadassolys/HashEncrypt/assets/103380760/6bad92e7-a6bd-48b5-9b7d-3625f26e3208" width="300" alt="Screenshot 2">
<img src="https://github.com/tadassolys/HashEncrypt/assets/103380760/104a62ce-1b29-4aee-812e-a585a016d2c0" width="300" alt="Screenshot 3">


## Security Considerations

- **Unique Passwords**: Each generated password is unique to the combination of main phrase and specific symbols, enhancing security.
- **Secure Hashing**: HashPass uses a secure hashing algorithm to encrypt passwords, ensuring that they cannot be easily decrypted.
- **Local Storage**: All hashing methods are stored locally on the user's device, providing maximum privacy and security.
- **Biometric/Fingerprint Authentication**: HashPass requires fingerprint or PIN authentication to access the app, ensuring that only authorized user can use it.
