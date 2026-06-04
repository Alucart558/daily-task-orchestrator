Tutaj łączymy klocki ze sobą. Jest całkowicie niezależny od tego, czy używamy Gmaila, czy Outlooka.
```java
public class DailyTaskOrchestrator {
    private final List<DataSource> dataSources;
    private final TaskSummarizer summarizer;
    private final TaskNotifier notifier;

    public DailyTaskOrchestrator(List<DataSource> dataSources, TaskSummarizer summarizer, TaskNotifier notifier) {
        this.dataSources = dataSources;
        this.summarizer = summarizer;
        this.notifier = notifier;
    }
    // ... metoda execute() ...
}
```
* **Dependency Injection (Wstrzykiwanie Zależności):** Zauważ, że konstruktor przyjmuje interfejsy jako parametry. Orchestrator sam z siebie _nie tworzy_ obiektów (nie ma tu słówka `new GmailDataSource()`). Obiekty są mu podawane (wstrzykiwane) z zewnątrz. Dzięki temu kod jest modularny i bardzo łatwy do testowania (możemy mu wstrzyknąć "fałszywe" adaptery do testów).
* `execute()`: Główny przepływ (Workflow).
* 1. Pętla `for (DataSource source : dataSources)` iteruje przez wszystkie podpięte źródła i pobiera surowe dane (`RawData`).
* 2. `normalizeTasks()`: Zmienia dane surowe (`RawData`) w ustandaryzowane (`Task`).
* 3. `summarizer.summarize(...)`: Zleca podsumowanie wszystkich znormalizowanych zadań.
* 4. Wysyła wynikowy raport powiadomieniem.
* `try-catch`: Blok łapiący wyjątki (błędy). Jeśli padnie serwer Gmaila, program nie "wybuchnie", tylko elegancko zapisze błąd przez `logger.error(...)`.