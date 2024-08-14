Spring Boot "Contact-service"  Project

## Overview
The Contact Service Application is a Spring Boot-based RESTful web service designed to process and consolidate contact information (email and phone number) across multiple purchases. This service ensures that all contact details are linked appropriately, maintaining a primary contact and linking secondary contacts as necessary.

## About the Service

The service is just a simple identity review REST service. It uses an mysql database to store the data. You can also do with a relational database like PostgreSQL. If your database connection properties work, you can call some REST endpoints defined in ```com.example.contactservice.controller;``` on **port 8080**. (see below)

You can use this sample service to understand the conventions and configurations that allow you to create a DB-backed RESTful service. Once you understand and get comfortable with the sample app you can add your own services following the same patterns as the sample service.

Here is what this little application demonstrates:

* Full integration with the latest **Spring** Framework: inversion of control, dependency injection, etc.
* Packaging as a single war with embedded container (tomcat 8): No need to install a container separately on the host just run using the ``java -jar`` command.
* Writing a RESTful service using annotation: supports both JSON request / response; simply use desired ``Accept`` header in your request
* Exception mapping from application exceptions to the right HTTP response with exception details in the body
* *Spring Data* Integration with JPA/Hibernate with just a few lines of configuration and familiar annotations.
* Automatic CRUD functionality against the data source using Spring *Repository* pattern
* Demonstrates MockMVC test framework with associated libraries

## How to Run

The **application.properties** file holds some App configurations, it's not uncommon to see sensitive credentials in it, here's its content:

```
# Server port
server.port=8090

# Datasource connection
spring.datasource.platform=mysql
spring.datasource.initialization-mode=always
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.show-sql=true

# Logging
logging.level.org.springframework=ERROR
logging.level.com.numericaideas=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

Let's build the App to make sure it compiles as expected, the following command must be run from the project root folder:

```
mvn clean install -DskipTests
```

At the end of the execution, the **Maven** build output should look like this:

![mvn_build](https://github.com/user-attachments/assets/e292b792-d1ec-4791-b698-a6d3da7ba556)

Other **Bash** scripts are available within the project for convenience:
- `build.sh`: to build the project.
- `up.sh`: to start the project using **Docker Compose**.
- `down.sh`: to shut everything down.

For our demo, the port in use is **8090** and we have implemented the CRUD operations for the **User** entity as well as a **Ping** endpoint, we won't go deeper on that since it's out of scope and you can follow the next sections of this guide with an existing Spring Boot (Maven) project too.

## Create Docker Image for Spring Boot Application
The `Dockerfile` should be provided to build an image of the Spring Boot Application, it contains the following lines:

```
# Use a base image with Java 17
FROM openjdk:17

# Copy the JAR package into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8090

# Run the App
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

It's straightforward, the `Dockerfile` uses an image based on **Java 17**, it copies into the image the executable **JAR** file that resulted from the build in the previous step, exposes the **port** the App is running on, then provides the **entry point** which is the bash command to run at the end to start the container.

By building your image with the command `docker build .`, a successful output should be similar to the next image:

![docker_build](https://github.com/user-attachments/assets/e2b1550e-4dca-4454-9ee4-f8ae0bb7b6b5)

By this stage, we have the App image and since we'll use an official MySQL Docker image, we can manually spin up both components and link them together by using the database credentials to run the App, but to make the process easier we'll link their deployments together by using **Docker Compose** in the next section.

## Docker Compose Spring Boot and MySQL
Docker Compose simplifies the orchestration of multi-container applications. Create a file named `docker-compose.yml` in your project directory and add the following configuration:

```yaml
version: '3.9'
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - 8090:8090
    depends_on:
      mysqldb:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysqldb:3306/${MYSQL_DATABASE}
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}
    networks:
      - springboot-mysql-network
  mysqldb:
    image: mysql:8.0.33  #docker pull mysql:8.0.33
    ports:
      - 3306:3306
    environment:
      - MYSQL_DATABASE=${MYSQL_DATABASE}
      - MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD}
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - springboot-mysql-network
    healthcheck:
      test: ["CMD", "mysqladmin" ,"ping", "-h", "localhost"]
      retries: 10
      interval: 3s
      timeout: 30s
volumes:
  mysql-data:
networks:
  springboot-mysql-network:
    name: springboot-mysql-network
```

This Docker Compose configuration defines two services: `app` for the **Spring Boot Application** and `mysqldb` for the **MySQL database**. The `app` service builds the image based on the [Dockerfile](./Dockerfile) in the project's root directory. The `mysqldb` service uses the official **MySQL** image and sets the environment variables for the database configuration. The `depends_on` attribute ensures that the Spring Boot application starts after the MySQL database in order to guarantee dependency ordering. Finally, the `healthcheck` makes sure the MySQL service is ready to accept connections before running the App.

For the App to connect itself to the MySQL database, as an enhanced security measure, we provided the database credentials as environment variables via the services' environment attributes so these are textually hidden from the project source code:
- **MYSQL_DATABASE**: The database name.
- **MYSQL_PASSWORD**: The database root's password, we use the root user account for simplicity only.

The environment attributes present in the **app** service are provided as environment variables to the App container at run time, we are talking about:
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD

Spring Boot automatically assigns these variables to the related **application.properties** configurations listed below:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password

Meaning we don't have to provide these manually into the **application.properties** file.

## Run the Dockerized Application
To run the App via **Docker Compose**, open a terminal, navigate to the project's root directory, and execute the following command in which we provide the **environment variables** directly:

```bash
set MYSQL_DATABASE=mydatabase
set MYSQL_PASSWORD=rootpassword
docker-compose up
```

In case you would like to provide the environment variables from a hidden `.env` file, feel free to create it from the `.env.sample` file and put it close to the `docker-compose.yml` within the project.

This being done, the command to run will change a bit to the following:

```bash
docker-compose up
```
Docker Compose will build the Spring Boot and MySQL images, create the containers, and start them. You'll see logs from both the application and the database. To stop the containers, press Ctrl+C.

Using **Docker Desktop** enables you to view a nice summary of your Docker state in which we can see our running containers as illustrated below:
![docker_build](https://github.com/user-attachments/assets/2d85aa5c-4f08-4df1-b44a-8714b7c0ef5f)

![docker_compose_up_1](https://github.com/user-attachments/assets/22b25936-803c-46af-a4c0-dfe810608f75)

![docker_compose_up_2](https://github.com/user-attachments/assets/0e4b3f93-da09-461e-9cc9-e21fe52a7e7c)

![docker_desktop_dockerimage](https://github.com/user-attachments/assets/94bb4b94-768d-4105-ae01-f82aa0477bca)

![docker_running](https://github.com/user-attachments/assets/1f7ea023-b859-4bb7-b83c-fdc3eabec3e7)

Open POSTMAN and **create** a POST request to the URL `[http://localhost:8090/v1/contact/identify]` with a random contact object to be persisted in the DB:

![create_API](https://github.com/user-attachments/assets/81f23465-0ce0-4f0c-a850-c43a1c1b24d7)

Let's **list all contacts** which includes the one we just created:

![get_API](https://github.com/user-attachments/assets/9cf8288e-50c6-4857-97f8-aa1d09fb5295)

#Bringing Down Docker Containers
 bring down Docker containers, use docker-compose down for stopping and removing containers:
 ```bash
 docker-compose down
```
![docker_down](https://github.com/user-attachments/assets/4cfcb5ab-1cff-4048-9753-1d75193c486d)




 






———————


