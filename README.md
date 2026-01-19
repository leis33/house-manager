# House Manager
# Electronic Home Manager

A comprehensive Java application for managing home maintenance in multi-family buildings, built with Hibernate, MySQL, Gradle Kotlin, Apache Log4j and GlassFish Expressly for validation.

## Setup Instructions

### Step 1: Install MySQL

1. Install MySQL on your system
2. Create a database user with appropriate privileges
3. The database will be created automatically by Hibernate

### Step 2: Configure Database Connection

Edit `src/main/resources/hibernate.properties` and update the database connection settings:

```
hibernate.connection.username=root
hibernate.connection.password=pass1234
```

### Step 3: Build the Project
If you use IntelliJ, you can just run Main.java. If not, you can run the following commands.

Using Gradle wrapper:
```bash
./gradlew build
```

Or using installed Gradle:
```bash
gradle build
```

### Step 4: Run the Application

Using Gradle:
```bash
./gradlew run
```

## Architecture Overview

### 1. Model Layer (Entity Classes)

The application uses JPA/Hibernate annotations for object-relational mapping as well as Lombok annotations:

- Company: Represents property management companies
- Employee: Company employees who service buildings (1 employee per building)
- Building: Multi-family buildings with apartments
- Apartment: Individual apartments in buildings
- Owner: Apartment owners (extends Person)
- Resident: People living in apartments (extends Person)
- Pet: Pets registered to apartments
- Fee: Monthly maintenance fees for apartments
- Payment: Payment records for fees

**Key Relationships:**
- Company → Employees (One-to-Many)
- Company → Buildings (One-to-Many)
- Employee → Buildings (One-to-Many)
- Building → Apartments (One-to-Many)
- Apartment → Residents (One-to-Many)
- Apartment → Pets (One-to-Many)
- Apartment → Fees (One-to-Many)
- Fee → Payments (One-to-Many)

### 2. Repository Layer (DAO)

Handles all database operations using Hibernate Sessions. Each repository provides:
- CRUD operations (Create, Read, Update, Delete)
- Custom query methods
- Transaction management

### 3. Service Layer

Contains business logic and implements functional requirements:

- CompanyService: Company management operations
- EmployeeService: Employee management with automatic building redistribution
- BuildingService: Building assignment to employees
- ApartmentService: Apartment management
- FeeService: Fee calculation based on area, residents, and pets
- PaymentService: Payment processing and fee tracking
- ReportService: Generates various reports

### 4. Utility Classes

- SessionFactoryUtil: SessionFactory management
- ValidationUtil: Tables validation using GlassFish Expressly
- PaymentFileWriter: Writes payment records to file

## Fee Calculation Logic

Monthly fees are calculated as follows:

1. Base Amount: `apartment_area × 2.0 BGN/sq.m`
2. Resident Charge: `residents_over_7 × 10.0 BGN` (only if building has elevator)
3. Pet Charge: `pets_using_common_areas × 15.0 BGN`

Total Fee = Base Amount + Resident Charge + Pet Charge

## Key Features Implemented

### 1. CRUD Operations
-  Create, edit, delete companies
-  Create, edit, delete buildings
-  Create, edit, delete owners and residents
-  Create, edit, delete employees

### 2. Automatic Assignment
-  New buildings assigned to employee with fewest buildings
-  When employee is deleted, buildings redistributed automatically

### 3. Fee Management
-  Generate monthly fees for apartments
-  Automatic calculation based on area, residents, and pets
-  Track paid/unpaid status

### 4. Payment Processing
-  Record payments
-  Write payment data to file
-  Automatic fee status updates

### 5. Filtering and Sorting
-  Companies by revenue
-  Employees by name and building count
-  Residents by name and age

### 6. Reports
-  Buildings served by each employee
-  Apartments in a building
-  Residents in a building
-  Payment amounts (by company, building, employee)
-  Paid amounts (by company, building, employee)

### 7. Data Validation

All entities use Jakarta Validation annotations:
- @NotBlank: Required string fields
- @NotNull: Required object references
- @Min: Minimum numeric values
- @Pattern: Regex validation for phone numbers, registration numbers
- @Email: Email format validation

### Payment File Format

Payments are automatically recorded in `payments/payments.txt`:

```
Company: Elite Property Management | Employee: Ivan Petrov | Building: 123 Main Street | Apartment: 101 | Amount: 185.00 | Date: 2025-01-18 14:30:00
```