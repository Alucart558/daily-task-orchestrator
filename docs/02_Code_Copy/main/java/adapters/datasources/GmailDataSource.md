### 1. Przygotowanie (Konstruktor)
```java
public GmailDataSource(GmailApiClient apiClient) {
    this.apiClient = apiClient;
}
```
Klasa ta potrzebuje pomocy zewnętrznego narzędzia ([[GmailApiClient]]), aby faktycznie połączyć się z serwerami Google. Przekazujemy to narzędzie w momencie tworzenia obiektu – dzięki temu nasza klasa wie, kogo prosić o pobranie maili.

### 2. Pobieranie danych (`fetch`)

To najważniejsza metoda. Gdy Twój program woła: _"Daj mi maile od tej daty"_, dzieje się następujący proces:

- **Zamiana czasu:** Metoda przyjmuje `Instant` (punkt w czasie). Gmail potrzebuje jednak czasu w formacie liczbowym (sekundy od początku epoki unixowej). Kod wykonuje konwersję: `from.getEpochSecond()`.
    
- **Budowanie zapytania:** Tworzy ciąg znaków, np. `"after:1717516512"`. To standardowa składnia wyszukiwarki Gmaila.
    
- **Komunikacja:** Używa `apiClient`, aby pobrać listę wiadomości.
    
- **Przetwarzanie (Streamy):** To bardzo elegancki fragment Javy. Zamiast pisać długą pętlę `for`, kod bierze listę surowych wiadomości z Google i za pomocą `.map(this::mapToRawData)` "przepuszcza" każdą z nich przez funkcję zmieniającą ją na format, który rozumie Twój program ([[RawData]]).

### 3. Konwersja (`mapToRawData`)

Twój program nie może pracować na skomplikowanych obiektach z biblioteki Google, bo stałby się od nich "uzależniony". Dlatego ta metoda wyciąga z maila tylko to, co istotne:

- `message.getSnippet()` – krótki podgląd treści maila.
    
- `message.getId()` – unikalny numer identyfikacyjny maila.
    
- `message.getInternalDate()` – czas otrzymania wiadomości.
    

Z tych danych tworzy czysty obiekt [[RawData]], który jest "językiem wspólnym" Twojej aplikacji.
### 4. Obsługa błędów (`try-catch`)

Programowanie to nie tylko "szczęśliwa ścieżka", ale też sytuacje, gdy coś pójdzie nie tak (np. brak internetu). Blok `try-catch` dba o to, żeby w razie błędu:

1. **Zalogować błąd** (żebyś wiedział, co się stało).
    
2. **Przerzucić wyjątek dalej** w czytelnej formie, aby aplikacja nie "zabiła się" bez słowa wyjaśnienia.
### W skrócie: Jak to działa?

- **Aplikacja** prosi o dane.
    
- [[GmailDataSource]] tłumaczy tę prośbę na język Gmaila.
    
- **Gmail** odpowiada surowymi danymi.
    
- [[GmailDataSource]] "obiera" te dane z niepotrzebnych informacji i zwraca je do Twojej aplikacji w formacie, który ona lubi ([[RawData]]).