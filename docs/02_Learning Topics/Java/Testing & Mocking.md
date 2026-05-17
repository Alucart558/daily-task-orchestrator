## Why Testing from the Start Matters
Testing isn't an afterthought; it is an essential tool for verifying your software design.
* If a class is difficult to write a unit test for, it is almost always a sign that the class is poorly designed or too tightly coupled (spaghetti code). For example, if a class instantiates its own dependencies using `new`, you cannot isolate it to test it. 
* **Testing Abstractions:** Because we use interfaces (Ports & Adapters), we can pass "fake" mock dependencies (a fake TV) into our Core logic during testing. 
* **Primary Benefit:** This allows us to prove our core business logic works perfectly in isolation before we ever have to deal with the messy reality of configuring real API network connections.