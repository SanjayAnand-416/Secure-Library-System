📚 Secure Library Management System

A secure, role-based Library Management System built using Spring Boot, designed to manage books, users, transactions, and requests with strong security mechanisms such as AES encryption, password hashing, OTP-based authentication, and digital signatures to ensure data confidentiality, integrity, and access control.

🚀 Project Overview

The Secure Library Management System is a web-based application that digitizes library operations while prioritizing security and controlled access.
It supports multiple user roles, secure book transactions, request workflows, and cryptographic protection for sensitive data such as ISBNs and transaction records.

This system is ideal for academic or institutional libraries where data integrity and authentication are critical.

🧩 Key Features
🔐 Security-Centric Design

AES Encryption for sensitive fields (e.g., ISBN)

Password Hashing for secure credential storage

Digital Signatures to ensure transaction integrity

OTP-based Email Authentication

Role-Based Access Control (RBAC)

📖 Library Operations

Book addition and management

Book request and approval workflow

Restock request handling

Secure borrowing and return transactions

Notification system for users and librarians

👥 User Management

User registration and login

Role-based dashboards

Admin-controlled user management

🏗️ System Architecture

The project follows a layered Spring Boot architecture:

Controller → Service → DAO → Database
                ↓
        Security & Crypto Utilities

🧑‍💼 User Roles & Permissions
👤 Admin

Manage users and roles

View all transactions

Monitor system activity

📚 Librarian

Approve book addition requests

Handle restock requests

Manage book inventory

View and verify transactions

🎓 User / Student

Request books

View transactions

Receive notifications

Secure login with OTP

🔒 Security Implementation (Core Strength)
1️⃣ Password Security

Passwords are hashed using a secure hashing mechanism.

Plain-text passwords are never stored.

2️⃣ AES Encryption

Sensitive data like ISBN numbers are encrypted using:

AES/CBC/PKCS5Padding


Encryption and decryption are handled via:

AESUtil

AESEncryptConverter

3️⃣ Digital Signatures

Each transaction is digitally signed.

Any tampering with transaction data invalidates the signature.

Ensures data integrity and non-repudiation.

4️⃣ OTP-Based Authentication

OTP is sent to the user’s registered email during login.

Prevents unauthorized access even if credentials are compromised.

🧠 Major Modules
📦 Book Management

Add, view, and manage books

Encrypted ISBN storage

Approval-based book addition

🔄 Transaction Management

Secure borrowing and returning

Digitally signed transactions

Tamper-proof transaction history

🔔 Notification System

System-generated notifications

Request status updates

📥 Request Handling

Book addition requests

Book borrowing requests

Restock requests

🛠️ Tech Stack
Layer	Technology
Backend	Spring Boot
Frontend	Thymeleaf, HTML, Bootstrap
Database	MySQL
Security	AES Encryption, OTP, Digital Signatures
Build Tool	Maven
Language	Java
📁 Project Structure
src/main/java/com/SecureLibrarySystem/webapp
│
├── auth              → Login, Registration, OTP services
├── authorization     → Roles & permissions
├── controller        → MVC controllers
├── crypto            → AES encryption utilities
├── dao               → Database access layer
├── hashing           → Password hashing
├── model             → Entity classes
├── service           → Business logic
├── util              → Digital signature & key utilities

⚙️ Configuration
application.properties

Database configuration

Email (OTP) configuration

Encryption key settings

application-local.properties

Local development overrides

▶️ How to Run the Project

Clone the repository

Configure MySQL

Create a database

Update credentials in application.properties

Run the application

mvn spring-boot:run


Open browser:

http://localhost:8080

✅ Key Learning Outcomes

Secure software design principles

Practical implementation of cryptography

Role-based access control

Spring Boot MVC architecture

Real-world transaction integrity handling

🔮 Future Enhancements

JWT-based authentication

Audit logs for admin actions

Two-factor authentication (2FA)

Advanced analytics dashboard

Cloud deployment

📜 License

This project is developed for academic and learning purposes.



▶️ How to Run the Secure Library Management System
✅ Prerequisites

Make sure the following are installed on your system:

Java JDK 8 or above

Maven

MySQL Server

IDE (Eclipse / IntelliJ IDEA – Eclipse recommended)

Internet connection (for OTP email service)

🗄️ Step 1: Set Up the Database (MySQL)

Open MySQL Workbench or MySQL CLI.

Create a database:

CREATE DATABASE secure_library;


Make sure MySQL service is running.

⚙️ Step 2: Configure Application Properties

Open the file:

src/main/resources/application.properties


Update the following:

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/secure_library
spring.datasource.username=root
spring.datasource.password=your_mysql_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server Port
server.port=8080

📧 Step 3: Configure Email for OTP (IMPORTANT)

In the same application.properties file:

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


🔹 Use Gmail App Password, not your normal Gmail password.

🧪 Step 4: Import Project into IDE (Eclipse)

Open Eclipse

Go to
File → Import → Existing Maven Project

Select the project folder

Click Finish

Wait for Maven dependencies to download

▶️ Step 5: Run the Application
Option A: Run from Eclipse

Locate the main class:

SecureLibrarySystemApplication.java


Right-click → Run As → Spring Boot App

Option B: Run from Terminal
mvn spring-boot:run

🌐 Step 6: Access the Application

Open your browser and go to:

http://localhost:8080

🔑 Step 7: Login / Register

Register a new user

Verify login using OTP sent to email

Admin / Librarian roles can be assigned from the database or admin panel