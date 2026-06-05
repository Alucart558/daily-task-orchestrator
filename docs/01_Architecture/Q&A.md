### 1. Czemu mamy [[RawData]] i [[GmailMessage]], czy nie robią tego samego?

**Nie robią tego samego, ponieważ należą do dwóch różnych światów w Architekturze Heksagonalnej.**

- **`RawData` to obiekt domenowy (wewnętrzny kontrakt).** Nasz `DailyTaskOrchestrator` zna tylko `RawData`. Obchodzi go tylko to, że dostał "jakieś surowe dane z jakiegoś źródła" (tytuł, treść, data). Nie wie, co to jest e-mail. Jeśli jutro dopiszesz adapter do Trello lub Slacka, one też zwrócą `RawData`.
    
- **`GmailMessage` to obiekt infrastrukturalny (specyficzny dla adaptera).** Posiada pola takie jak `isUnread`, `labels`, `messageId`. Nasz główny program nie ma pojęcia, co to jest "label", bo Trello czy Slack tego nie mają. `GmailMessage` istnieje tylko po to, aby uporządkować bałagan z API Gmaila wewnątrz samego adaptera, zanim przekujemy to na generyczne `RawData`.
    

### 2. Czemu [[EmailFilter]] woła API Gmaila? (Małe sprostowanie)

**`EmailFilter` w ogóle nie woła API.** Woła je klient (`apiClient.getEmails(...)`). Filtr działa dwuetapowo:

1. Najpierw [[GmailDataSource]] pyta filtr: _"Hej, jakiego zapytania tekstowego mam użyć dla API?"_ (`filter.getTaskQuery()`). Filtr oddaje tylko stringa (np. `after:1234567`).
    
2. `GmailDataSource` bierze tego stringa, wysyła zapytanie do API i dostaje listę maili.
    
3. Następnie `GmailDataSource` przepuszcza każdy pobrany mail przez filtr w pamięci RAM (`filter.isTaskEmail()`), sprawdzając, czy w treści są słowa kluczowe (np. "deadline", "kolokwium").
    

### 3. Czym jest `@Component` i jak działa Spring? Czemu nie po prostu `public`?

**`public` a `@Component` to dwie zupełnie różne rzeczy.**

- `public` to tylko modyfikator dostępu (jak `private`). Mówi Javie: "inne klasy mogą widzieć tę klasę".
    
- `@Component` to instrukcja dla frameworka Spring.
    
### 4. Po co nam tyle kroków: API -> Parser -> Filter -> Converter -> RawData -> Task? (Zasada SRP)

Może się to wydawać nadmiarowe, ale to klasyczne zastosowanie zasady **Single Responsibility Principle (SRP)** z zasad SOLID. Jeśli połączysz to w jedną wielką klasę "ZróbWszystkoZMailem", to przy najmniejszej zmianie (np. Google zmienia strukturę JSON, albo Ty chcesz dodać nowe słowo kluczowe) ryzykujesz popsucie całej logiki. Każdy krok ma jedno, proste zadanie:

1. **Google API zwraca `Message`:** To jest koszmarnie skomplikowany, zagnieżdżony obiekt prosto z Google. Pełen dziwnych nagłówków i kodowania Base64.
    
2. **[[GmailMessageParser]]:** Jego _jedyną_ odpowiedzialnością jest wzięcie tego śmieciowego obiektu Google, odkodowanie go i stworzenie czystego obiektu Javy (`GmailMessage`). Nic więcej. Jeśli Google jutro zmieni API, modyfikujesz tylko tę klasę.
    
3. **[[EmailFilter]]:** Jego _jedyną_ odpowiedzialnością jest odpowiedź na pytanie "Czy to jest zadanie?". Nie wie, jak parsować e-maile ani czym jest [[RawData]].
    
4. **[[EmailToRawDataConverter]]:** Zmienia `GmailMessage` w `RawData` – odcina "pępowinę" łączącą dane z Gmailem.
    
5. **[[TaskExtractor]] (w przyszłości):** Bierze `RawData` (które już nie wie, że było mailem) i przy użyciu AI lub wyrażeń regularnych wyciąga z niego czysty obiekt `Task` (z przypisaną datą wykonania, priorytetem, przedmiotem z uczelni).
    

Dzięki temu kod jest testowalny. Możesz przetestować sam parser bez wysyłania prawdziwych zapytań do Google.

### 5. Skąd wziąć `client-id` i `client-secret`?

Użytkownik (czyli Ty) musi wygenerować je w konsoli dla deweloperów Google.

1. Wchodzisz na stronę **Google Cloud Console**.
    
2. Tworzysz nowy projekt.
    
3. Włączasz bibliotekę **Gmail API** dla tego projektu.
    
4. Przechodzisz do zakładki **Ekran zgody OAuth** (OAuth consent screen) i ustawiasz go jako aplikację zewnętrzną (External).
    
5. Przechodzisz do **Dane logowania** (Credentials) i klikasz "Utwórz dane logowania" -> "Identyfikator klienta OAuth".
    
6. Wybierasz typ aplikacji: "Aplikacja na komputer" (Desktop app) lub "Aplikacja internetowa" z odpowiednim przekierowaniem (`http://localhost:8888/Callback`).
    
7. Dostajesz `client-id` oraz `client-secret`, które wklejasz do swoich zmiennych środowiskowych.