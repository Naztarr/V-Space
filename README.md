# V-Space Digital Property Rental Application
V-Space is a comprehensive digital property rental application designed for renting various types of properties including houses, land, and warehouses. The application integrates several modern technologies to provide a robust and efficient platform for property management.
## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Usage](#usage)
- [Contributing](#contributing)

## Features

- **Property Listings:** Browse and search for houses, land, and warehouses.
- **User Management:** Register, login, and manage user accounts.
- **Role-Based Access Control:** Secure access based on user roles.
- **Caching:** Improved performance with Redis caching.
- **Messaging:** Asynchronous communication using RabbitMQ.
- **API Documentation:** Interactive API documentation via Swagger.

## Technologies Used

- **Spring Boot:** Framework for building the application.
- **Spring Security:** Authentication and authorization.
- **Redis:** Caching for improved performance.
- **RabbitMQ:** Messaging for handling asynchronous tasks.
- **Swagger:** API documentation and testing.
- **PostgreSQL:** Database for storing application data.

## Installation

1. **Clone the Repository**

   ```
   git clone https://github.com/Naztarr/V-Space.git
   cd v-space
   ```
   
2. **Set Up Environment Variables**
   
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/V-Space
   spring.datasource.username=postgres
   spring.datasource.password=${DB_PASSWORD}
   
   server.port=8025
   
   spring.security.user.name=VSpace
   spring.security.user.password=${SECURITY_PW}
   
   spring.cache.type=redis
   spring.redis.host=localhost
   spring.redis.port=6379
   
   spring.rabbitmq.host=localhost
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest
   spring.rabbitmq.virtual-host=/
   spring.rabbitmq.publisher-confirm-type=correlated
   spring.rabbitmq.template.mandatory=true
   rabbitmq.queue.email=emailQueue
   
   vSpace.security.jwt-secret-key=${JWT_SECRET_KEY}
   spring.mail.username=${EMAIL_USERNAME}
   spring.mail.password=${EMAIL_PASSWORD}
   
   cloudinary.cloud_name=${CLOUDINARY_NAME}
   cloudinary.api_key=${CLOUDINARY_API_KEY}
   cloudinary.api_secret=${CLOUDINARY_API_SECRET}
   ````
   
3. **Build the Application**

   ```
   mvn clean install
   ```
   
4. **Run the Application**

   ``` 
   mvn spring-boot:run
   ```

## Configuration

- **Database Configuration:** Ensure PostgreSQL is running and configured as specified in your config file.
- **Redis Configuration:** Make sure Redis is running on the specified host and port.
- **Redis:** Caching for improved performance.
- **RabbitMQ Configuration:** Ensure RabbitMQ is running and accessible as configured.

## Running the Application
After starting the application, you can access it via:

- **Web Application:** http://localhost:8025
- **Swagger API Documentation:** http://localhost:8025/swagger-ui/index.html#

## API Documentation
The API documentation is available via Swagger at:

http://localhost:8025/swagger-ui/index.html#

This provides interactive documentation for all the endpoints, allowing you to test API requests directly from your browser.

## Usage
1. **User Registration and Login:**

- Register new users and log in to access the application.
2. **Property Management:**

- List, search, and manage properties (houses, land, warehouses).
3. **Role-Based Access:**

- Access different features based on user roles (USER, ADMIN, SUPERADMIN).


## Contributing
Contributions are welcome! Please follow these steps to contribute:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Create a new Pull Request.

