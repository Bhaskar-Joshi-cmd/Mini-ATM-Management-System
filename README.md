# Mini ATM System (AMS) 🏧

A robust, desktop-based banking simulation engineered with **Core Java** and **MySQL**. This project demonstrates a layered architecture (DAO Pattern) and secure data handling for financial operations.

---

## 🚀 Key Features

* **Secure Dual-Table Authentication:** Decouples user credentials from account profiles to enhance data security.
* **Live Transaction Impact:** Real-time preview of balances post-withdrawal/deposit using background SQL queries.
* **Automated Audit Trail:** Integrated **iText library** to generate timestamped, professional PDF receipts for every transaction.
* **Data Visualization:** Built-in trend tracking (e.g., spending/deposits) via custom Swing-based components.
* **Professional UI:** Modern, clean interface with input validation and focus-based placeholder handlers.

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 21+)
* **Database:** MySQL 9.x
* **GUI Framework:** Java Swing (GridBagLayout)
* **Persistence:** JDBC (Java Database Connectivity)
* **Libraries:** * `mysql-connector-j` (Database connectivity)
    * `iText 5.5.13` (PDF generation)

---

## 📂 Project Structure

```text
src/com/atm/
├── dao/        # Data Access Objects (SQL Logic)
├── model/      # Data entities (Account, Transaction)
├── services/   # Business logic
├── ui/         # GUI Frames & Panels (Login, Dashboard, etc.)
└── util/       # Database connection & shared utilities

⚙️ Setup & Installation
1. Database Configuration
Open MySQL Workbench.

Run the script provided in db_schema.sql to create the database, tables, and sample data.

Update src/com/atm/util/DBConnection.java with your MySQL root username and password.

2. IDE Setup
Clone the repository: git clone https://github.com/Bhaskar-Joshi-cmd/AMS.git

Open the project in NetBeans or IntelliJ IDEA.

Add the .jar files from the lib/ folder to your project's Libraries/Classpath.

3. Execution
Run LoginFrame.java to start the application.

Default Account: 1234567890

Default PIN: 1234

📜 Database Schema
The system utilizes three primary tables:

accounts: Stores user profiles and current balances.

login: Securely holds account numbers and PINs.

transactions: Logs all financial activities for the audit trail.

👨‍💻 Developer
Bhaskar Joshi

B.E. Computer Science Student | Chandigarh University

Specialization: Java Backend Development
