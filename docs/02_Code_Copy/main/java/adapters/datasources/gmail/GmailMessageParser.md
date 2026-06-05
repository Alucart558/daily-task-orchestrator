Ten kod to klasa w języku Java, której zadaniem jest **"tłumaczenie" surowych danych otrzymanych z API Gmaila na czytelny dla Twojej aplikacji obiekt**.

Wyobraź sobie, że Google przesyła Ci wiadomość w formie bardzo skomplikowanego, technicznego dokumentu (JSON). Ta klasa działa jak tłumacz, który wyciąga z tego dokumentu to, co ważne (nadawcę, temat, treść) i wkłada to do Twojej "skrzynki" (`GmailMessage`).
### 1. Czym jest ta klasa?

- **`@Component`**: To adnotacja [[Spring]]. Mówi ona: "Hej, Springu, stwórz sobie instancję tej klasy i zarządzaj nią". Dzięki temu możesz łatwo użyć tego parsera w innych częściach aplikacji.
    
- **`GmailMessageParser`**: To główna klasa, która zawiera metodę `parse`. To ona przyjmuje obiekt `Message` (surowe dane z Google) i zwraca [[GmailMessage]] (Twój własny, czysty obiekt).
### 2. Główne kroki przetwarzania (`parse`)

1. **Walidacja**: Sprawdza, czy wiadomość w ogóle istnieje. Jeśli nie, wyrzuca błąd (`IllegalArgumentException`), żeby program nie "wywalił się" w nieoczekiwanym miejscu.
    
2. **Podstawowe dane**: Wyciąga ID wiadomości, etykiety (np. czy jest to "SPAM", "INBOX") oraz datę odebrania.
    
    - _Uwaga:_ Google podaje datę w milisekundach, a ten kod zamienia ją na bardziej ludzki format `LocalDateTime`.
        
3. **Nagłówki i Treść**: Wywołuje dwie pomocnicze metody: `extractHeaders` (dla tematu i nadawcy) oraz `extractBody` (dla właściwej wiadomości).
### 3. Jak działają "pomocnicy"?

- **`extractHeaders`**: Przegląda listę nagłówków e-maila. Jeśli znajdzie nagłówek "Subject", zapisuje go jako temat. Jeśli "From", zapisuje jako nadawcę.
    
- **`extractBody`**: To najtrudniejsza część. E-maile często składają się z wielu części (np. tekst + załącznik + wersja HTML). Kod sprawdza, czy wiadomość jest "wieloczęściowa" (multipart), i szuka części oznaczonej jako `text/plain`, czyli zwykłego tekstu, który chcemy przeczytać.
    
- **`decodeBase64`**: Google przesyła treść e-maila w formacie zakodowanym (Base64). Ta metoda bierze ten "dziwny" ciąg znaków i zamienia go z powrotem na zwykły tekst, który możemy przeczytać.
    

### 4. Ważne szczegóły techniczne

- **Logowanie (`Logger`)**: Zwróć uwagę na `log.debug` i `log.info`. To bardzo dobra praktyka. Dzięki temu, gdy coś nie zadziała w produkcji, będziesz mógł zajrzeć do logów i sprawdzić, co się stało (np. "O, ta wiadomość ma załączniki, których nie umiem przetworzyć").
    
- **Obsługa błędów**: Kod stara się być bezpieczny – sprawdza czy dane nie są `null` i próbuje dekodować tekst w bloku `try-catch`, aby uniknąć awarii aplikacji, jeśli kodowanie będzie nieprawidłowe.