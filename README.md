# Bicycle Delivery Tour Optimization Application

This project is a Java application designed to efficiently manage delivery requests.
It focuses on distributing delivery requests, where couriers start from a warehouse and complete multiple deliveries. The system uses the **Traveling Salesman Problem (TSP)** algorithms to compute the best possible routes for couriers, ensuring that the total time of the delivery tour is minimized.

## Table of Contents

- [Introduction](#introduction)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Running the Application](#running-the-application)
- [Running the Tests](#running-the-tests)
- [Customizing Input](#customizing-input)
- [Key Classes (How it is implemented ?)](#key-classes(how-it-is-implemented-?))
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)

## Introduction

The application aims to optimize delivery routes for bicycle couriers in urban environments. It is designed to help couriers minimize delivery times by calculating optimal routes based on city maps and delivery requests in a short time. Each courier starts their tour from the same warehouse and must complete deliveries and return to the warehouse.

The system assumes:
- **Constant speed of 15 km/h** for all couriers.
- **5 minutes for each pickup and delivery** operation.
- **8 hours shift** per courier.

In this app, the user can:
- Load city maps in XML format, which provide intersections, roads, and warehouse locations.
- Load a list of delivery requests (XML format) which will then be assigned to courriers (number of delivery people chosen by the user knowing that there are at least 2 and we limit ourselves to 10 on the maximum terminal side for reasons of realism).
- Calculate the TSP (Traveling Salesman Problem) of a list of delivery requests and generate a delivery tour
- View optimized delivery tours for each courier.
- Modify delivery tours / delivry requests.
- Validate delivery tours
- View optimized delivery tours for each courier on a map (visual view) and with text (textual view).

Note : - If a courier cannot fulfill all delivery requests, the system will assign deliveries to another courier. If no more couriers are available, the delivery request is rejected.
- An XML must be well-formed to be loaded (2 distinct formats : 1 for loadMap and 1 for loadDelivery -> see in the main code, the repository named Data)

## Technologies

General:
- **nodeJS then npm**: Allows you to install the npm package manager which will be able to install elements like react.

BackEnd:
- **Java + its features (jdk)**: Core programming language for implementation.
- **Spring Boot**: Used for application management and potential web service integration.
- **JUnit**: For unit testing.
- **Maven**: For dependency management and building the project.
- **UML Tools**: StarUML or ObjectAid for reverse engineering diagrams from code.
- **JavaDoc**: For generating online code documentation.

FrontEnd:
- **React**: For application design (html, css, js, ...).

## Project Structure

Note : The project follows a Model-View-Controller (MVC) structure to separate concerns effectively.

```
bash
new-front
├── node_modules
├── public
├── src
│   ├── Components
src
├── main
│   ├── java/com/pld/agile
│   │   ├── Application.java             # Main entry point
│   │   ├── model
│   │   │   ├── algorithm                # TSP Solvers
│   │   │   ├── entity                   # Couriers, Delivery Requests, etc.
│   │   │   ├── graph                    # Graph, Intersection, Section
│   │   │   └── strategy                 # TSP Solving Strategies
│   │   └── controller                   # Spring Controllers (if applicable)
├── resources
│   └── data                             # XML files for city plans and delivery requests
└── test
    └── java/com/pld/agile                # Unit tests
```

## Running the Application

Note : you need a recent version of java like 21 for example.

1. **Clone the repository**:
    ```bash
    git clone https://github.com/theoclou/PLD-Agile.git
    cd PLD-Agile
    ```

2. **Launch the web server**:
    Launch the application in the file with the same name (SpringApplication.run(Application.class, args)) or use the command mvn spring-boot:run in the terminal.
    Important Note : To avoid compilation and installation problems (like maven or things in the pom.xml), work on the IDE Intellij and just launch the project.

3. **Launch the front project**:
    Install nodeJS (20.18.0)
    Use npm to install all useful packages with the command "install npm"
    In a new terminal, go to the repo new-front and use the command "npm start" : it will start the front project
    See the Readme in the repo new-front for more informations..

## Running the Tests

Follow the same instructions than above but just launch this time ApplicationTests.java (for the moment the tests are not done here but that will change when the project is more advanced).
  
## Customizing Input

You can customize the input data by editing the XML files in the `src/resources/data/` folder:
- **City Map (XML)**: Contains intersections, roads, and the warehouse location.
- **Delivery Requests (XML)**: Contains pickup and delivery locations for each request.

Make sure the format adheres to the expected XML structure, and modify `filePath` and the file is under 10MB.

## Key Classes (How it is implemented ?)

- **Application.java**: The main entry point for the application.
- **Solver.java**: Manages the TSP solving process using different strategies.
- **Plan.java**: Represents the city map, including intersections (verticies) and sections (edges) which will allow us to build a graph later.
- **DeliveryRequest.java**: Represents a delivery task, including pickup and drop-off locations.
- **BranchAndBound.java**: Implements the Branch and Bound algorithm for solving the TSP.
- **CompleteGraph.java**: Represents a complete graph used for solving the TSP.

Note : Go to the code to see all the classes and and all their features to understand how it works precisely.

## Future Improvements

- **Support for alternative algorithms**: Implement more optimized version the current TSP solver to get a solution for around 30 points in few seconds.
- **Real-time updates**: Support real-time updates for couriers, enabling users to track the delivery progress live.
- **User Interface Enhancements**: Improve the graphical interface for better user experience.

## Contributing

Project made at INSA Lyon as part of the course on the aglie method.
Contributors: SOULET Audrey, BOUZIANE Abderrahmane, CLOUSCARD Théo, MARIAT Quentin, CATHERINE Noam
    
