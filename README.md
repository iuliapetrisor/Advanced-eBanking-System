# PETRISOR IULIA-ALEXIA - 322CD
# Project Assignment POO  - J. POO Morgan - Phase Two
#### Assignment Link: [https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2024/proiect-etapa2](https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2024/proiect-etapa2)

# ADVANCED e-BANKING SYSTEM
This project extends the simplified banking system created in Phase One, adding new features and functionalities to
enhance the user experience. The goal is to create a revolutionary application similar to Revolut, but with additional
features to improve users' financial lives. The project uses object-oriented programming concepts and design patterns to
ensure a modular and scalable architecture. 

## Objectives
- Implement at least 4 design patterns.
- Refactor the code to allow the addition of new functionalities.
- Enhance the application with new features such as business accounts, cashback strategies, and different account plans.

## New Features in Stage II
**User Data Enhancements**: Additional data for users such as birth date, occupation, and account management plan type.
**Commerciants**: Commerciants will have information such as name, ID, account IBAN, type, and cashback strategy.
**Cashback Strategies**: Two types of cashback strategies - nrOfTransactions and spendingThreshold.
**Service Plans**: Customizable plans with different transaction fees and benefits.
**Business Accounts**: Shared accounts for entrepreneurs with different user roles (owner, manager, employee) and
transaction limits.

## Design Patterns Used
1. **Command Pattern**
   Description: Encapsulates a request as an object, thereby allowing for parameterization of clients with queues,
   requests, and operations.
   Usage: Used to handle various operations such as printing users, adding accounts, creating cards, and adding funds.
   Each operation is encapsulated in a command object that implements the Command interface.
   Classes: Command, AddAccount, AddFunds, CreateCard, DeleteAccount, DeleteCard, SetMinBalance, CheckCardStatus,
   PayOnline, SendMoney, SetAlias, SplitPayment, ChangeInterestRate, AddNewBusinessAssociate, BusinessReport etc.
2. **Factory Pattern**
   Description: Defines an interface for creating an object but lets subclasses alter the type of objects that will be created.
   Usage: Used to create instances of different account plans based on the provided plan type string.
   Classes: AccountPlanFactory.
3. **Strategy Pattern**
   Description: Defines a family of algorithms, encapsulates each one, and makes them interchangeable. Strategy lets 
   the algorithm vary independently from clients that use it.
   Usage: Used for implementing different cashback strategies for commerciants.
   Classes: CashbackStrategy, NrOfTransactionsStrategy, SpendingThresholdStrategy.
4. **Builder Pattern**
   Description: Separates the construction of a complex object from its representation so that the same construction 
   process can create different representations.
   Usage: Used to construct instances of Transaction with various optional parameters.
   Classes: Transaction.Builder.

## Execution of the Program
### Program Entry Point:
The program starts execution from the `main` method in the `Main` class. This method initializes the necessary
components and triggers the execution of commands.

### Initialization:
1. **Data Loading**: The program reads user and commerciant data from JSON files using the `ObjectMapper` class
   from the Jackson library.
2. **Bank Initialization**: The `Bank` class is instantiated with the loaded data. This class is responsible for managing
   users, commerciants, and executing commands.

### Command Execution:
1. **Command Parsing**: Commands are parsed from the input data and stored in an array.
2. **Command Execution**: The `executeCommands` method in the `Bank` class iterates over the parsed commands and
   executes them using the appropriate command classes. Each command class implements the `Command` interface and defines
   the `execute` method to perform specific operations.

### Service Plans:
1. **Account Plan Creation**: The `AccountPlanFactory` class creates instances of different account plans
   (e.g., `StudentAccount`, `SilverAccount`, `GoldAccount`, `StandardAccount`) based on the plan type.
2. **Transaction Fees**: Each account plan implements the `AccountPlan` interface and defines the `getTransactionFee` method
   to calculate transaction fees based on the plan type.

### Cashback Strategies:
1. **Strategy Implementation**: The `CashbackStrategy` interface is implemented by `NrOfTransactionsStrategy` and
   `SpendingThresholdStrategy`.
2. **Cashback Calculation**: These strategies calculate cashback based on the number of transactions or spending thresholds.

### Transactions:
1. **Transaction Creation**: The `Transaction` class uses the Builder pattern to create transaction instances with
   various optional parameters.
2. **Transaction Management**: The `TransactionManager` class manages transactions for users and accounts, adding transactions
   to the appropriate user and account.

## Project Structure
The project is organized into several packages, each containing related classes and interfaces:

- `org.poo.banksystem`: Contains core classes such as `Bank`, `User`, `Account`, `Commerciant`, and `ExchangeRateManager`.
- `org.poo.commands`: Contains command classes that implement the `Command` interface and handle specific operations.
- `org.poo.fileio`: Contains classes for reading input data from JSON files.
- `org.poo.transactions`: Contains classes related to transactions, including `Transaction` and `TransactionManager`.
- `org.poo.banksystem.serviceplans`: Contains classes related to different account plans and the `AccountPlanFactory`.
- `org.poo.banksystem.strategies`: Contains classes related to cashback strategies.

This structure ensures a modular and scalable architecture, making it easy to add new features and functionalities.