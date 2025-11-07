# AutoTrack: Car Parts Sales and Inventory System

## Project Overview

AutoTrack is a robust, modular desktop application designed to modernize the operation of local auto parts stores. It replaces error-prone manual inventory and sales tracking with a centralized, digital system built on a JavaFX interface and a MongoDB backend.

The system is engineered for **financial integrity** and **staff accountability**, ensuring precise inventory levels and traceable transactions for three distinct user roles.

##  Technical Stack

* **Language:** Java (JDK 23+)

* **Framework:** JavaFX (Version 23)

* **Database:** MongoDB (using `mongodb-driver-sync:5.2.0`)

* **Build Tool:** Gradle (Kotlin DSL)

* **Security:** jBCrypt for password hashing (`org.mindrot:jbcrypt`)

##  Setup and Installation

### 1. Requirements

* Java Development Kit (JDK 23 or later)

* Gradle build tool

* Access to a running **MongoDB** instance (Local or Atlas Cloud).

### 2. Database Connection

The MongoDB URI and database name are configured in the `DatabaseHelper.java` file.

### 3. Initial Users (For Testing)

For demonstration, use the following pre-hashed accounts. The system will automatically direct users to the correct dashboard based on their role.

| Role | Username | Password (Plaintext) | Access |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin` | Inventory Management, Reports, Staff User Management |
| **Cashier** | `cashier` | `cashier` | Point-of-Sale (POS) Transactions, Receipt Generation |
| **Customer** | `customer` | `customer` | Online Storefront, Cart, Checkout |

### 4. Running the Application

1.  Clone the repository: `git clone [repository URL]`

2.  Open the project in IntelliJ IDEA or your preferred IDE that supports Gradle/JavaFX.

3.  Ensure your MongoDB instance is running and accessible via the URI configured in `DatabaseHelper.java`.

4.  Run the main class: `oop.avengers.avengersgroup.AutoTrackApplication`

5. Note: Make sure to delete the module-info.java before running it in an IDE.  
