### Wszystko zaczyna się w metodzie `main` w pliku [[Main]] Jej zadaniem jest "uruchomienie maszyny".
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
**Co się tu dzieje?**

1. [[main]] prosi klasę [[AppConfig]] o dostarczenie jej gotowych do użycia komponentów: źródła danych ([[DataSource]]), analizatora ([[TaskSummarizer]]) i notyfikatora ([[TaskNotifier]]). Nie wie, jakie to są konkretnie implementacje (np. Gmail, [[Claude]]), tylko że spełniają one określony kontrakt (interfejsy).
    
2. Następnie tworzy główny obiekt logiki biznesowej, [[DailyTaskOrchestrator]], i "wstrzykuje" do niego te komponenty.
    
3. Na koniec wywołuje metodę `execute()`, która rozpoczyna właściwą pracę.
    

### Konfiguracja zależności: [[AppConfig]]

Ta klasa działa jak centrum dowodzenia dla zależności ([[Dependency Injection]]). To tutaj pobieramy konfigurację ze środowiska, logujemy się do zewnętrznych API i decydujemy, jakich konkretnych narzędzi użyje nasza aplikacja.
```java
public class AppConfig {

    public static List<DataSource> createDataSources() {
        // 1. Pobranie kluczy API ze zmiennych środowiskowych (bezpieczeństwo!)
        String clientId = System.getenv("GMAIL_CLIENT_ID");
        String clientSecret = System.getenv("GMAIL_CLIENT_SECRET");
        
        if (clientId == null || clientSecret == null) {
            throw new IllegalStateException("Brak zmiennych środowiskowych dla Gmail API!");
        }

        // 2. Konfiguracja uprawnień i ścieżek
        GmailConfiguration config = new GmailConfiguration(
            clientId, clientSecret, "http://localhost:8888/Callback",
            "~/.dailytask/gmail_tokens",
            List.of("https://www.googleapis.com/auth/gmail.readonly", "https://www.googleapis.com/auth/gmail.modify")
        );

        // 3. Zbudowanie drzewa zależności dla Gmaila
        NetHttpTransport httpTransport = new NetHttpTransport();
        GmailOAuth2Handler authHandler = new GmailOAuth2Handler(config, httpTransport);
        GmailApiClient apiClient = new GmailApiClient(authHandler, httpTransport);

        // Zdecydowano: źródłem danych będzie Gmail (wstrzykujemy gotowego klienta API)
        return List.of(new GmailDataSource(apiClient));
    }

    public static TaskSummarizer createAnalyzer() {
        // Zdecydowano: analizatorem będzie Claude
        return new ClaudeTasksSummarizer();
    }

    public static TaskNotifier createNotifier() {
        // Zdecydowano: powiadomienia będą wysyłane e-mailem
        return new EmailTaskNotifier();
    }
}
```
**Co się tu dzieje?**

- Gdy `Main` woła `AppConfig.createDataSources()`, aplikacja najpierw sprawdza zmienne środowiskowe. Następnie przechodzi przez proces autoryzacji OAuth2 ([[GmailOAuth2Handler]]), tworzy niskopoziomowego klienta API ([[GmailApiClient]]) i dopiero na jego bazie buduje ostateczny adapter [[GmailDataSource]].
    
- Dzięki temu klasa [[Main]] nie musi nic wiedzieć o protokołach HTTP, tokenach OAuth2 czy ukrytych kluczach – dostaje gotowe do pracy źródło danych.
    
- Gdy [[Main]] woła `AppConfig.createAnalyzer()` i `createNotifier()`, otrzymuje odpowiednie implementacje do analizy i powiadomień.
    
- Zaletą tego podejścia jest to, że jeśli jutro zechcesz dodać nowe źródło (np. Trello), stworzysz konfigurację Trello w tej klasie i po prostu dodasz ją do zwracanej listy, bez zmieniania reszty aplikacji.
    

### Mózg operacji: [[DailyTaskOrchestrator]]

To jest serce logiki biznesowej. Ten plik nie wie, skąd pochodzą dane (czy to Gmail, czy Trello) ani jak są analizowane, po prostu orkiestruje przepływ danych pomiędzy komponentami.
```java
public class DailyTaskOrchestrator {
    // ... (pola i konstruktor)

    public void execute() {
        logger.info("Starting Daily Task Orchestration...");

        // Krok 1: Pobierz surowe dane
        List<RawData> allRawTasks = new ArrayList<>();
        for (DataSource source : dataSources) {
            // Wywołuje np. GmailDataSource.fetch(...) z określoną datą początkową
            allRawTasks.addAll(source.fetch(Instant.now().minus(24, ChronoUnit.HOURS)));
        }

        // Krok 2: Przekształć surowe dane w ustrukturyzowane zadania
        List<Task> normalizedTasks = normalizeTasks(allRawTasks);

        // Krok 3: Przekaż zadania do analizatora
        // Wywołuje np. ClaudeTasksSummarizer.summarize(...)
        TasksSummary analyzedResult = summarizer.summarize(normalizedTasks);

        // Krok 4: Wyślij wynik za pomocą notyfikatora
        // Wywołuje np. EmailTaskNotifier.notify(...)
        notifier.notify(analyzedResult);

        logger.info("Daily Task Orchestration completed successfully.");
    }

    private List<Task> normalizeTasks(List<RawData> rawTasks) {
        // Ta metoda na razie jest uproszczona.
        // Bierze surowe dane (np. treść e-maila) i tworzy z nich obiekt Task.
        // W docelowej architekturze zostanie zastąpiona dedykowanym portem TaskExtractor.
        // ...
    }
}
```
**Co się tu dzieje?**

1. **Pobieranie danych:** [[DailyTaskOrchestrator]] przechodzi przez listę dataSources i na każdej z nich wywołuje metodę `fetch(Instant from)`. [[GmailDataSource]] w tym momencie uderza do API Google, autoryzuje się odświeżonym tokenem i pobiera e-maile z ostatnich 24 godzin, zwracając je jako listę obiektów [[RawData]].
    
2. **Normalizacja:** Metoda `normalizeTasks` bierze surowe, nieustrukturyzowane dane (np. obiekt [[RawData]] z zanieczyszczoną treścią e-maila) i konwertuje je na obiekt [[Task]].
    
3. **Analiza:** Orchestrator przekazuje listę obiektów [[Task]] do `summarizer.summarize()`. W naszym przypadku [[ClaudeTasksSummarizer]] wysłałby te dane do API modelu językowego Claude z prośbą o inteligentne grupowanie i zwróciłby wynik jako obiekt [[TasksSummary]].
    
4. **Powiadomienie:** Na koniec, wynik trafia do `notifier.notify()`. `EmailTaskNotifier` formatuje to podsumowanie i wysyła użytkownikowi gotowy raport.