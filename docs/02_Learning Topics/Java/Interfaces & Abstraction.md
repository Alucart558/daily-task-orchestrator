# Java Interfaces & Abstraction

## What Is an Interface?

A contract that says:
"If you implement me, you MUST have these methods with these signatures"

## Example

```java
public interface DataSource {
    List<RawTask> fetch();
    String getName();
}
```

Any class that implements DataSource MUST have:
- `fetch()` method returning `List<RawTask>`
- `getName()` method returning `String`

## Interfaces as Contracts (Remote Controls)

An interface is a **contractual agreement**. It states: *"If you want to be a data source, you MUST have a `fetch()` method."* It is just an empty set of rules.

Think of an interface variable as a **remote control**. If you type `private DataSource source;`, you haven't built a TV; you've just built a remote control with a `fetch()` button. Until you pair it with a real TV, pressing the button does nothing.

### Why this prevents Spaghetti Code

**The Bad Way (Tightly Coupled)**
If you hardcode your tools, changing a tool means ripping open your core files and rewriting them. This causes bugs.
```java
// BAD: If Gmail changes to Outlook, we have to rewrite our Core logic!
class Orchestrator {
    // 1. Delete Gmail, add Outlook
    OutlookService outlook = new OutlookService(); 
    
    void run() {
        // 2. Change the method name because Outlook uses a different one
        String data = outlook.downloadMessages(); 
    }
}

// GOOD: We never touch this file again. Ever.
class Orchestrator {
    DataSource source; 
    
    Orchestrator(DataSource source) {
        this.source = source;
    }
    
    void run() {
        // This line stays exactly the same, whether it's Gmail or Outlook!
        String data = source.fetch(); 
    }
}
```


## Why Use Interfaces?

1. **Polymorphism** - treat different implementations the same way
2. **Testing** - easily swap real with mock
3. **Contracts** - explicit about what's needed
4. **Flexibility** - change implementation without changing code that uses it

Resources:
- Oracle Java Docs: https://docs.oracle.com/javase/tutorial/java/concepts/interface.html