# AutoTrack: Car Parts Sales and Inventory System

## Project Overview

AutoTrack is a robust, modular desktop application designed to modernize the operation of local auto parts stores. It replaces error-prone manual inventory and sales tracking with a centralized, digital system built on a JavaFX interface and a MongoDB backend.

The system is engineered for **financial integrity** and **staff accountability**, ensuring precise inventory levels and traceable transactions for three distinct user roles.

## 🌟 Key Features

| Role | Core Functions | Technical Implementation Highlight |
| :--- | :--- | :--- |
| **Administrator** | Inventory Management (CRUD), Staff User Management, and Advanced Reporting. | **MongoDB Aggregation Pipeline** used for the "Most Popular Products" report. |
| **Cashier** | Dedicated Point-of-Sale (POS) terminal, quick product searching, and sale processing. | Generates a formatted **Digital Receipt** upon sale completion; uses **Client-Side Stock Validation** to prevent overselling. |
| **Customer** | Online storefront browsing, dynamic search, persistent shopping cart, and secure checkout. | **Client-Side Stock Validation** prevents adding out-of-stock items, improving UX. |
| **System Core** | Financial integrity for all currency, user authentication. | **`java.math.BigDecimal`** used throughout the entire codebase for all monetary calculations, eliminating floating-point errors. |

## 📐 Data Architecture (ERD Summary)

The system utilizes a relational data model structure built within MongoDB (non-SQL) to ensure transactional integrity:

* **Order Accountability:** The `ORDER` entity is the central transactional backbone, linking every sale via foreign keys to the **Cashier ID**, **Customer ID**, and the **Payment Method ID**.

* **Data Consistency:** The `ORDERITEMS` entity captures the `priceAtSale`, preserving historical sales data even if product prices are later updated in the `PRODUCT` collection.

## 🛠️ Technical Stack

* **Language:** Java (JDK 23+)

* **Framework:** JavaFX (Version 23)

* **Database:** MongoDB (using `mongodb-driver-sync:5.2.0`)

* **Build Tool:** Gradle (Kotlin DSL)

* **Security:** jBCrypt for password hashing (`org.mindrot:jbcrypt`)

## ⚙️ Setup and Installation

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
