# Reflecta
Manages quarterly evaluation meetings, allowing users to record, track, and review meeting details over time.

## Features
- **User Registration & Authentication**
  - Secure user account creation with password hashing (BCrypt)
  - Role-based access control
  - Login and token-based authentication

- **Quarterly Meeting Management**
  - Create and store meeting records
  - Link meetings to users
  - Retrieve and list past evaluations

### Default user - Normally, I would store credentials and secrets in Spring Vault.
Username: admin
Pwd: admin123

#### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven
