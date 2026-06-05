Ten kod to klasa `GmailDataSource`, która pełni rolę „pośrednika” (tzw. adaptera) pomiędzy Twoją aplikacją a pocztą Gmail. Jej zadaniem jest pobranie e-maili z Gmaila i zamiana ich na ustandaryzowaną formę ([[RawData]]), którą reszta Twojego systemu potrafi zrozumieć.
### Jak to działa krok po kroku?

1. **Przygotowanie (Konstruktor):** Klasa potrzebuje kilku „pomocników”, aby wykonać swoją pracę (klient API do łączenia się z Google - [[GmailApiClient]], filtr do wyszukiwania - [[EmailFilter]], [[GmailMessageParser]] do czytania treści e-maila i konwerter). Są oni wstrzykiwani automatycznie przez framework [[Spring]].
    
2. **Główna misja (`fetch`):** To najważniejsza metoda. Przyjmuje parametr `from` (od kiedy chcemy pobrać wiadomości) i zwraca listę danych.
    
    - **Zapytanie:** Używa [[emailFilter]], aby przygotować odpowiednią frazę wyszukiwania (np. "znajdź maile od daty X").
        
    - **Pobieranie:** Używa [[GmailApiClient]], aby połączyć się z serwerami Google i ściągnąć listę wiadomości.
        
    - **Przetwarzanie (pętla `for`):** Kod przechodzi przez każdą pobraną wiadomość:
        
        - `messageParser` wyciąga z technicznego obiektu Gmaila konkretne informacje (np. treść, temat, nadawcę).
            
        - `emailFilter` sprawdza, czy e-mail faktycznie kwalifikuje się jako "zadanie" (task).
            
        - [[EmailToRawDataConverter]] zamienia tego e-maila na format [[RawData]], który jest „wspólnym językiem” w Twojej aplikacji.
            
    - **Obsługa błędów:** Jeśli jeden e-mail jest uszkodzony, kod go pomija (`log.error`), zamiast przerywać działanie całego programu.
        

### Kluczowe pojęcia dla nowicjusza

- **@Service:** To adnotacja [[Spring]], która mówi: „Hej, ta klasa jest ważnym komponentem, zajmij się jej tworzeniem i zarządzaniem”.
    
- **Wstrzykiwanie zależności (Dependency Injection):** Zauważ, że `GmailDataSource` nie tworzy swoich pomocników (np. `apiClient`) słowem `new`. Dostaje je „w prezencie” w konstruktorze. To dobra praktyka, bo ułatwia testowanie kodu.
    
- **Interfejs [[DataSource]]:** Klasa implementuje ten interfejs, co oznacza, że obiecuje systemowi: „Jeśli zapytasz mnie o `fetch()` lub `getName()`, zawsze dostarczysz mi to, czego potrzebuję”. Dzięki temu w innych miejscach aplikacji można używać różnych źródeł danych (np. Outlook, Slack) tak samo jak Gmaila.