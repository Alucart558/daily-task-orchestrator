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
            List.of("[https://www.googleapis.com/auth/gmail.readonly](https://www.googleapis.com/auth/gmail.readonly)", "[https://www.googleapis.com/auth/gmail.modify](https://www.googleapis.com/auth/gmail.modify)")
        );

        // 3. Zbudowanie drzewa zależności infrastrukturalnych dla Gmaila
        NetHttpTransport httpTransport = new NetHttpTransport();
        GmailOAuth2Handler authHandler = new GmailOAuth2Handler(config, httpTransport);
        GmailApiClient apiClient = new GmailApiClient(authHandler, httpTransport);

        // 4. Zainicjalizowanie klas odpowiedzialnych za przetwarzanie surowych e-maili
        List<String> taskKeywords = List.of("assignment", "deadline", "due", "project", "submit", "quiz", "exam", "homework");
        EmailFilter emailFilter = new EmailFilter(taskKeywords);
        GmailMessageParser messageParser = new GmailMessageParser();
        EmailToRawDataConverter rawDataConverter = new EmailToRawDataConverter();
        int queryLimit = 20;

        // 5. Złożenie całego adaptera w jedną całość
        return List.of(new GmailDataSource(apiClient, emailFilter, messageParser, rawDataConverter, queryLimit));
    }

    public static TaskSummarizer createAnalyzer() {
        return new ClaudeTasksSummarizer(); // W przyszłości np. nowy adapter OpenAI
    }

    public static TaskNotifier createNotifier() {
        return new EmailTaskNotifier();
    }
}
```
**Co się tu dzieje?**

- Gdy `Main` woła `AppConfig.createDataSources()`, aplikacja najpierw sprawdza zmienne środowiskowe. Następnie przechodzi przez proces autoryzacji OAuth2 ([[GmailOAuth2Handler]]), tworzy niskopoziomowego klienta API ([[GmailApiClient]]) i dopiero na jego bazie buduje ostateczny adapter [[GmailDataSource]]. Konfigurator nie tylko buduje połączenie z Google API. Buduje również całą "linię produkcyjną" adaptera ([[GmailMessageParser]] -> [[EmailFilter]] -> [[EmailToRawDataConverter]]), ściśle stosując zasadę Single Responsibility (SRP). Cała ta złożoność jest pakowana do obiektu [[GmailDataSource]] i zwracana na zewnątrz jako prosty kontrakt [[DataSource]].
### Przepływ danych wewnątrz Adaptera: [[GmailDataSource]]

Zanim dane trafią do centralnego programu, przechodzą proces wewnątrz adaptera `GmailDataSource`:

1. **Odpytanie API:** `GmailDataSource` używa `EmailFilter.getTaskQuery()`, by stworzyć zapytanie po dacie. Odpytuje `GmailApiClient` i otrzymuje surowe, skomplikowane obiekty `Message` prosto z serwerów Google.
    
2. **Parsowanie:** Złożone i zakodowane w Base64 obiekty od Google są przekazywane do `GmailMessageParser`, który odkodowuje treść, wyciąga temat oraz nadawcę, tworząc czysty, domenowy obiekt adaptera: `GmailMessage`.
    
3. **Filtrowanie:** Gotowy `GmailMessage` trafia do `EmailFilter.isTaskEmail()`, który w pamięci sprawdza, czy e-mail faktycznie zawiera zdefiniowane słowa kluczowe (zadania, projekty).
    
4. **Konwersja:** Odfiltrowane e-maile są przekazywane do `EmailToRawDataConverter`, który zamienia powiązany z Gmailem obiekt na całkowicie niezależny ustandaryzowany obiekt `RawData`.
    

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

**Pełny cykl życia zadania w architekturze:** `Google JSON (Infrastruktura)` -> **Parser** -> `GmailMessage (Adapter)` -> **Filter** -> **Converter** -> `RawData (Domena Wejściowa)` -> **Extractor** -> `Task (Główny model domeny)` -> **Summarizer** -> `TasksSummary (Wynik)` -> **Notifier** -> `Wiadomość wysłana do użytkownika`.