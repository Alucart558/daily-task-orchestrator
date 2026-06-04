Wszystko zaczyna się w metodzie main w pliku [[Main]] Jej zadaniem jest "uruchomienie maszyny".

```java

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Initializing Application Dependencies...");

        try {
            // 1. Pobranie konkretnych implementacji z AppConfig
            List<DataSource> sources = AppConfig.createDataSources();
            TaskSummarizer analyzer = AppConfig.createAnalyzer();
            TaskNotifier notifier = AppConfig.createNotifier();

            // 2. Stworzenie "mózgu" aplikacji z tymi implementacjami
            DailyTaskOrchestrator orchestrator = new DailyTaskOrchestrator(sources, analyzer, notifier);

            // 3. Uruchomienie głównej logiki
            orchestrator.execute();

        } catch (Exception e) {
            logger.error("Application crashed during initialization or execution", e);
            System.exit(1);
        }
    }
}
```

Co się tu dzieje?
	1. [[main]] prosi klasę [[AppConfig]] o dostarczenie jej gotowych do użycia komponentów:  źródła danych ([[DataSource]]), analizatora ([[TaskSummarizer]]) i notyfikatora ([[TaskNotifier]]). Nie wie, jakie to są konkretnie implementacje (np. Gmail, Claude), tylko że spełniają one określony kontrakt ([[Interfaces]]).
	2. Następnie tworzy główny obiekt logiki biznesowej, [[DailyTaskOrchestrator]], i "wstrzykuje" do niego te komponenty.
	3. Na koniec wywołuje metodę execute(), która rozpoczyna właściwą pracę.

**Konfiguracja zależności: [[AppConfig]]**
Ta klasa działa jak fabryka. To tutaj decydujemy, jakich konkretnych narzędzi użyje nasza aplikacja.
```java
public class AppConfig {

    public static List<DataSource> createDataSources() {
        // Zdecydowano: źródłem danych będzie Gmail
        return List.of(new GmailDataSource());
    }

    public static TaskSummarizer createAnalyzer() {
        // Zdecydowano: analizatorem będzie Claude
        return new ClaudeTaskAnalyzer();
    }

    public static TaskNotifier createNotifier() {
        // Zdecydowano: powiadomienia będą wysyłane e-mailem
        return new EmailTaskNotifier();
    }
}
```

Co się tu dzieje?
	•Gdy [[Main]] woła AppConfig.createDataSources(), [[AppConfig]] tworzy nową instancję [[GmailDataSource]] i ją zwraca.
	•Gdy [[Main]] woła AppConfig.createAnalyzer(), [[AppConfig]] tworzy ClaudeTaskAnalyzer.
	•Gdy [[Main]] woła AppConfig.createNotifier(), [[AppConfig]] tworzy EmailTaskNotifier.
Zaletą tego podejścia jest to, że jeśli jutro zechcesz dodać analizę zadań z Trello, zmienisz tylko jedną linię w createDataSources(): return List.of(new GmailDataSource(), new TrelloDataSource());. Reszta aplikacji nawet o tym nie wie.

**Mózg operacji: [[DailyTaskOrchestrator]]**
To jest serce logiki biznesowej. Ten plik nie wie, skąd pochodzą dane ani jak są analizowane, po prostu orkiestruje przepływ.

```java
public class DailyTaskOrchestrator {
    // ... (pola i konstruktor)

    public void execute() {
        logger.info("Starting Daily Task Orchestration...");

        // Krok 1: Pobierz surowe dane
        List<RawData> allRawTasks = new ArrayList<>();
        for (DataSource source : dataSources) {
            // Wywołuje np. GmailDataSource.fetch(...)
            allRawTasks.addAll(source.fetch(Instant.now().minusSeconds(24 * 3600)));
        }

        // Krok 2: Przekształć surowe dane w ustrukturyzowane zadania
        List<Task> normalizedTasks = normalizeTasks(allRawTasks);

        // Krok 3: Przekaż zadania do analizatora
        // Wywołuje np. ClaudeTaskAnalyzer.summarize(...)
        TasksSummary analyzedResult = summarizer.summarize(normalizedTasks);

        // Krok 4: Wyślij wynik za pomocą notyfikatora
        // Wywołuje np. EmailTaskNotifier.notify(...)
        notifier.notify(analyzedResult);

        logger.info("Daily Task Orchestration completed successfully.");
    }

    private List<Task> normalizeTasks(List<RawData> rawTasks) {
        // Ta metoda na razie jest uproszczona.
        // Bierze surowe dane (np. treść e-maila) i tworzy z nich obiekt Task.
        // TODO: Ta logika powinna być bardziej zaawansowana.
        // ...
    }
}
```

Co się tu dzieje?
	1.Pobieranie danych: DailyTaskOrchestrator przechodzi przez listę dataSources (w naszym przypadku zawiera tylko [[GmailDataSource]]) i na każdej z nich wywołuje metodę fetch(). [[GmailDataSource]] w tym momencie łączyłby się z Gmailem i pobierał e-maile, zwracając je jako listę obiektów [[RawData]].
	2.Normalizacja: Metoda normalizeTasks bierze surowe dane (np. obiekt [[RawData]] z tytułem i treścią e-maila) i konwertuje je na bardziej ustrukturyzowany obiekt Task. W obecnej formie jest to bardzo uproszczone, ale w docelowym rozwiązaniu mogłoby tu następować np. wyciąganie kluczowych informacji z treści e-maila.
	3.Analiza: [[DailyTaskOrchestrator]] przekazuje listę obiektów [[Task]] do summarizer.summarize(). W naszym przypadku [[ClaudeTaskAnalyzer]] wysłałby te dane do API modelu językowego Claude z prośbą o ich podsumowanie i zwróciłby wynik jako obiekt TasksSummary.
	4.Powiadomienie: Na koniec, [[DailyTaskOrchestrator]] przekazuje TasksSummary do notifier.notify(). EmailTaskNotifier sformatowałby to podsumowanie i wysłał je jako e-mail.