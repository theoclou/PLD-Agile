# Bicycle Delivery Tour Optimization Application

This project is a Java application designed to efficiently manage delivery requests.
It focuses on distributing delivery requests, where couriers start from a warehouse and complete multiple deliveries. The system uses the **Traveling Salesman Problem (TSP)** algorithms to compute the best possible routes for couriers, ensuring that the total time of the delivery tour is minimized.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
  - [Running the Application](#running-the-application)
  - [Customizing Input](#customizing-input)
- [How it Works](#how-it-works)
- [Development Process](#development-process)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)

## Introduction

The application aims to optimize delivery routes for bicycle couriers in urban environments. It is designed to help couriers minimize delivery times by calculating optimal routes based on city maps and delivery requests. Each courier starts their tour from the same warehouse and must complete deliveries and return to the warehouse.

The system assumes:
- **Constant speed of 15 km/h** for all couriers.
- **5 minutes for each pickup and delivery** operation.
- **8 hours shift** per courier.

The user can:
- Load city maps in XML format, which provide intersections, roads, and warehouse locations.
- Enter delivery requests with pickup and drop-off locations.
- Adjust the number of couriers.
- View optimized delivery tours for each courier.
- Save and restore tours from a file.

If a courier cannot fulfill all delivery requests, the system will ask the user to assign deliveries to another courier. If no more couriers are available, the delivery request is rejected.

## Features

- **City map management**: Load city maps from XML files.
- **Dynamic delivery requests**: Add delivery requests and re-optimize tours in real-time.
- **Optimized delivery tours**: Solve the **Traveling Salesman Problem (TSP)** using various algorithms Branch and Bound approach.
- **Courier management**: Adjust the number of couriers dynamically.
- **Graphical interface**: Display the city map and delivery routes.
- **Time optimization**: Minimizes total delivery time while ensuring all time-window constraints are met.
- **Tour persistence**: Save and restore courier tours to/from files.

## Technologies

- **Java**: Core programming language for implementation.
- **Spring Boot**: Used for application management and potential web service integration.
- **JUnit**: For unit testing.
- **Maven**: For dependency management and building the project.
- **UML Tools**: StarUML or ObjectAid for reverse engineering diagrams from code.
- **JavaDoc**: For generating online code documentation.

## Project Structure

```bash
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
## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/theoclou/PLD-Agile.git
    cd PLD-Agile
    ```


## Usage

### Running the Application

- After building and running the project, the application will load a city map from `src/resources/data/petitPlan.xml` and handle delivery requests in real-time. You can modify the XML files to change the map and test different scenarios.
  
### Customizing Input

You can customize the input data by editing the XML files in the `src/resources/data/` folder:
- **City Map (XML)**: Contains intersections, roads, and the warehouse location.
- **Delivery Requests (XML)**: Contains pickup and delivery locations for each request.

Make sure the format adheres to the expected XML structure, and modify `filePath` .

## How it Works

1. **City Map Loading**: The XML file is loaded to create a graph of the city, where intersections are nodes and road segments are edges.
2. **Delivery Request Management**: The user can add delivery requests, specifying both pickup and delivery locations. Each request is assigned to a courier.
3. **TSP Solver**: The application computes the optimal tour for each courier using a variant of the TSP algorithm (Branch and Bound).
4. **Tour Display**: The optimized delivery tours are displayed on the map, showing arrival and departure times for each location.
5. **Courier Reassignment**: If a courier cannot complete all deliveries within the time constraints, the system prompts the user to assign the remaining deliveries to another courier.

### Key Classes

- **Application.java**: The main entry point for the application.
- **Solver.java**: Manages the TSP solving process using different strategies.
- **Plan.java**: Represents the city map, including intersections and road segments.
- **DeliveryRequest.java**: Represents a delivery task, including pickup and drop-off locations.
- **BranchAndBound.java**: Implements the Branch and Bound algorithm for solving the TSP.
- **CompleteGraph.java**: Represents a complete graph used for solving the TSP.

## Future Improvements

- **Support for alternative algorithms**: Implement more optimized version the current TSP solver to get a solution for around 30 points in few seconds.
- **Real-time updates**: Support real-time updates for couriers, enabling users to track the delivery progress live.
- **User Interface Enhancements**: Improve the graphical interface for better user experience.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
    