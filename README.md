# Spring Boot REST API Forecast

A simple CRUD-based RESTful API built with **Spring Boot** that gather forecast from https://api.met.no/ and save it into a Postgres DB
This project serve as skill a test for Spond

---

## Technologies Used

- **Java 1721** (or compatible version)
- **Spring Boot 3.3.x** (Web, Data JPA)
- **Docker** (for services)
- **Postgres** (via Docker)
- **Maven** (build tool)
- **Postman** (for testing; optional)


## Prerequisites

- **Java** (version 21+ recommended)
- **Maven** (to build and run the project)
- **Docker** (if you want to run MySQL in a container)
- **Git** (for cloning this repository)

---

## Getting Started

1 - Clone the repo to local machin
2 - Inside the project folder run  
    ```bash
    mvn clean install -DskipTests
    docker-compose up --build  
   ```
3 - The project should start in a few seconds and you are now able to run the following postman get   
    ```bash
    http://localhost:8080/api/v1/forecast/?eventId=1 
   ```
Note - data.sql file seems to not be creating new records on start. 
In that case you should exec into postgres service and run  
 ```bash
    psql -h localhost -p 5432 -U admin -d forecast_db
   ```
after this you should copy the content on data.sql and commit it. 