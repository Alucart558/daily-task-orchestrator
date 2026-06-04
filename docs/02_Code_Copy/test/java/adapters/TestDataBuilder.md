"Fabryka" testowych danych. Zamiast w każdym teście pisać od nowa obiektów z wymyślonymi nazwami, robimy to raz tutaj.
```java
public class TestDataBuilder {
    public static RawData buildRawData() {
        return new RawData("Test Source", "Test Email", "Hello...", LocalDateTime.now());
    }

    public static Task buildTask() {
        return new Task("1", "Process Email", "Hello...", LocalDateTime.now().plusDays(1), "HIGH", "Test Source", "TODO");
    }

    public static TasksSummary buildSummarizedTasks() {
        return new TasksSummary(List.of(buildTask()), "Test Summary", "Test Schedule", List.of("Recommendation 1"));
    }
}
```
* **Wzorzec Projektowy (Test Data Builder / Object Mother):** Dzięki temu plikowi nasze testy stają się o wiele czytelniejsze. Jeśli kiedykolwiek dodamy nowe pole do klasy `Task` (np. przypisana kategoria), będziemy musieli zaktualizować konstruktor tylko w tym jednym miejscu, a nie w 50 plikach testowych.
* Wszystkie metody są `static`, więc wywołujemy je pisząc po prostu `TestDataBuilder.buildTask()`.
