## Hexagonal Architecture (Ports & Adapters)
Hexagonal Architecture splits an application into distinct regions: **Inside** (Core Business Logic) and **Outside** (Infrastructure, APIs). This is the ultimate implementation of Separation of Concerns.
### 1. The Port (The Contract) 
Located in the `ports` folder. It has no logic. 
```java
// File: ports/DataSource.java
 public interface DataSource { String fetch(); // No body, just a rule! }
```
 
 
 ### 2.The Adapters (The Real TVs)
```java
// File: adapters/GmailAdapter.java
public class GmailAdapter implements DataSource { 
@Override 
public String fetch() {
 // Dirty code to connect to Google's servers, handle passwords, etc.
  return "Emails from Gmail"; } }
```
### 3. The Core (The Boss)
```java
// File: core/Orchestrator.java
public class Orchestrator {
    // This is just our remote control.
    private DataSource source; 
    
    public Orchestrator(DataSource source) {
        this.source = source; 
    }
    
    public void run() {
        // The core doesn't know if this is Gmail, Outlook, or a fake test!
        String data = source.fetch(); 
    }
}
```
