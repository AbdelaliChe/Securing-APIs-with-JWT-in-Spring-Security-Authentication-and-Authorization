# Securing APIs with JWT in Spring Security: Authentication and Authorization

This project demonstrates a RESTful API for user authentication using Spring Security and JSON Web Tokens (JWT). It offers endpoints for user registration, login, token refresh, and password reset, enhancing the security and usability of applications that require robust user authentication and authorization mechanisms.

## Features

- **Secure User Registration**: Store user information with encrypted password storage to ensure data security.
- **Efficient User Login**: Generate JWT tokens upon successful user authentication to manage sessions effectively.
- **Session Maintenance**: Implement a token refresh mechanism to extend user sessions securely.
- **Password Recovery**: Enable password reset via email notifications for enhanced user convenience.
- **Role-Based Access Control**: Demonstrates basic role management with "User" and "Admin" roles for testing purposes.

## Technologies Used

- **Spring Boot**
- **Spring Security**
- **JSON Web Tokens (JWT)**
- **JavaMailSender**
- **MySQL Database**
- **Lombok**

## API Endpoints

- `POST /api/auth/register`: Register a new user.
- `POST /api/auth/login`: Authenticate a user and acquire JWT tokens.
- `POST /api/auth/refreshToken`: Refresh access token using a refresh token.
- `POST /api/auth/forgotPassword`: Initiate password reset process.
- `PUT /api/auth/resetPassword`: Reset user's password using a reset token.
- `GET /api/auth/hello`: A test endpoint that is public.
- `GET /api/resources/admin`: Accessible only by users with the "ADMIN" role.
- `GET /api/resources/user`: Accessible only by users with the "USER" role.

## Authors

- **Chentoui Abdelali** - _CS Student_ - [AbdelaliChe](https://github.com/AbdelaliChe/)
