## Maven Project Structure & Conventions 
Setting up a project correctly from day one is like pouring a solid concrete foundation for a house. Our Maven project is organized with a strict architectural purpose:
* **`src/main/java` vs `src/test/java`**: This separates production code from verification code. `main` is what actually runs; `test` exists purely to prove `main` works. Production code should *never* import anything from the test folder. * 
* **`ports` folder**: Contains the **interfaces** (the rules/contracts). * 
* **`adapters` folder**: Contains the **concrete implementations** (the engines that do the heavy lifting). * 
* **`core` (Domain Models & Logic)**: Represents the raw data and business rules.