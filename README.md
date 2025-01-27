# contact-service
Spring Boot "Contact-service"  Project

## Overview
The Contact Service Application is a Spring Boot-based RESTful web service designed to process and consolidate contact information (email and phone number) across multiple purchases. This service ensures that all contact details are linked appropriately, maintaining a primary contact and linking secondary contacts as necessary.

## How to Run

This application is packaged as a jar which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the ```java -jar``` command.

* Clone this repository
* Make sure you are using latest JDK and Maven 3.x
* You can build the project and run the tests by running ```mvn clean install```
*Once the application runs you should see something like this

```
1:48.161+05:30  WARN 18800 ---                [contact-service] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2024-08-07T14:11:49.078+05:30  INFO 18800 --- [contact-service] [           main] c.e.contactservice.ContactServiceTests   : Started ContactServiceTests in 5.111 seconds (process running for 21.737)
```
* Also You can run the main class \contactservice\ContactServiceApplication.java
```
  2024-08-07T14:13:37.128+05:30  INFO 15440 --- [contact-service] [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
  2024-08-07T14:13:37.141+05:30  INFO 15440 --- [contact-service] [  restartedMain] c.e.c.ContactServiceApplication          : Started ContactServiceApplication in 8.785 seconds (process running for 10.994)
```

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

