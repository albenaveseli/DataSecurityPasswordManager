# Password Manager Application

## Overview

This desktop Password Manager is a secure and user-friendly application developed with JavaFX. It enables users to safely store, organize, and manage their login credentials. The application emphasizes strong security by encrypting all sensitive data using AES encryption with keys derived from a user-defined master password. It also supports encrypted data synchronization across multiple devices via cloud storage solutions like Google Drive.

## Key Features

- **Strong AES Encryption:**  
  All credentials—website, username, password, and category—are encrypted using AES in CBC mode with PKCS5 padding. The encryption key is securely derived from the master password using PBKDF2 with HMAC-SHA256.

- **Master Password Authentication:**  
  Users must enter a master password on startup. The password is never stored; instead, it derives the encryption key. Access is verified by decrypting a securely stored token.

- **Secure Password Generation:**  
  Built-in password generator creates cryptographically strong random passwords to encourage best security practices.

- **Credential Organization by Category:**  
  Users can categorize credentials, simplifying management and searching.

- **Encrypted Data Display:**  
  Credentials are shown in encrypted form by default and only decrypted on explicit user request, reducing accidental exposure.

- **Search Functionality:**  
  Search credentials by website or category with decrypted results displayed securely.

- **CRUD Operations:**  
  Full support for adding, loading, decrypting, searching, and deleting credentials.

- **Cross-Device Synchronization:**  
  The SQLite database file is stored in a user-configured cloud-synced folder (e.g., Google Drive), enabling seamless encrypted data synchronization across devices.

## Technical Details

- **Encryption Algorithm:** AES/CBC/PKCS5Padding with 128-bit keys derived via PBKDF2WithHmacSHA256, using a fixed salt and 65,536 iterations for key stretching.

- **Data Persistence:** SQLite database accessed through JDBC, stored locally but designed for cloud folder synchronization.

- **User Interface:** JavaFX-based GUI providing intuitive and responsive user interactions.

- **Error Handling:** Comprehensive and user-friendly error messages guiding through encryption, decryption, input validation, and database operations.

## Usage Instructions

1. **Launch the application.**  
2. **Enter your master password** (create it on first launch).  
3. **Add new credentials** by filling in website, username, password (or generate a strong one), and category.  
4. **Save credentials** securely with AES encryption.  
5. **Load credentials** to view encrypted entries.  
6. **Select an entry and decrypt** it to reveal the stored details safely.  
7. **Search credentials** by website or category.  
8. **Delete credentials** as needed.  
9. **Ensure your database folder is synced** via cloud service for multi-device access.

## Security Considerations

- The master password is never stored; only a verification token encrypted by the key derived from it is saved.  
- Each credential is encrypted with a unique, randomly generated initialization vector (IV) to enhance cryptographic security.  
- All cryptographic operations follow standard and well-established practices.  

---

This Password Manager provides a robust foundation for secure, multi-device credential management with a clean, easy-to-use interface, and can be extended with additional features as needed.

