# Bicycle Delivery Tour Optimization Application

This project is a Java application designed to efficiently manage delivery requests.
It focuses distributing delivery requests, where couriers start from a warehouse and complete multiple deliveries. The system uses the **Traveling Salesman Problem (TSP)** algorithms to compute the best possible routes for couriers, ensuring that the total time of the delivery tour is minimized.

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

The application aims to optimize delivery routes for bicycle couriers in urban environments. It is designed to help couriers minimize delivery times by calculating optimal routes based on city maps and delivery requests. Each courier starts their tour from a central warehouse and must complete deliveries and return to the warehouse.

The system assumes:
- **Constant speed of 15 km/h** for all couriers.
- **5 minutes for each pickup and delivery** operation.

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
- **Optimized delivery tours**: Solve the **Traveling Salesman Problem (TSP)** using various algorithms (e.g., Branch and Bound, Dynamic Programming).
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
