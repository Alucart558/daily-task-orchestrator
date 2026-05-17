## Dependency Injection (Manual Wiring)
**Dependency Injection (DI)** means:
Objects should not create the things they need to do their job. Instead, those things should be handed to them. 
* **Analogy:** It is like giving a child their toys rather than expecting them to manufacture the toys themselves. 
 ### How the Magic Happens 
 How does the abstract "remote control" (interface) in the Core actually get paired with the real "TV" (implementation) in the Adapters? 
 **Through the constructor in an AppConfig or Main class.** 
 
 This is the only place where the Core and Adapters actually meet:
  ```java
// File: Main.java 
public class Main { public static void main(String[] args) { 
// STEP 1: We build the actual TV in memory (The Adapter).
 DataSource realGmailObject = new GmailAdapter(); 
 // STEP 2: We hand the TV to the Orchestrator via the constructor. 
 // Java pairs our remote control to that specific Gmail object! 
 Orchestrator app = new Orchestrator(realGmailObject); 
 // STEP 3: Run the app 
 app.run(); } }
  ```
  