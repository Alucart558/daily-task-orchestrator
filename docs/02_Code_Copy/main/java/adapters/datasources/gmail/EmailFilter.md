### 1. Czym jest ta klasa?

- **`@Component`**: To adnotacja [[Spring]], która mówi: „Springu, stwórz obiekt tej klasy automatycznie i zarządzaj nim”. Dzięki temu klasa ta może być używana w innych częściach aplikacji.
    
- **Cel**: Klasa `EmailFilter` ma dwie główne funkcje:
    
    1. Sprawdza, czy e-mail to „zadanie” (na podstawie słów kluczowych).
        
    2. Tworzy zapytanie (np. do API Gmaila), aby pobrać e-maile od określonego czasu.
        

### 2. Inicjalizacja (Konstruktor)
```java
public EmailFilter(@Value("${gmail.task-keywords}") List<String> taskKeywords) {
    this.taskKeywords = taskKeywords.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
}
```
- **`@Value("${...}")`**: Pobiera listę słów kluczowych (np. "zadanie", "do zrobienia", "task") z zewnętrznego pliku konfiguracyjnego aplikacji.
    
- **`stream().map(String::toLowerCase)...`**: To zamiana wszystkich pobranych słów na małe litery. Dzięki temu filtr będzie działał niezależnie od tego, czy w e-mailu napisano "Zadanie", "ZADANIE" czy "zadanie".
### 3. Logika filtrowania (`isTaskEmail`)

To najważniejsza metoda, która decyduje: „czy to e-mail z zadaniem?”.

- Najpierw sprawdza, czy wiadomość istnieje (`null` check).
    
- Zamienia temat i treść e-maila na małe litery (znowu, żeby zachować spójność z naszymi słowami kluczowymi).
    
- **`anyMatch`**: To bardzo elegancki sposób w Javie, który mówi: „sprawdź, czy **chociaż jedno** słowo kluczowe z naszej listy znajduje się w temacie LUB w treści e-maila”. Jeśli tak – metoda zwraca `true`.
    

### 4. Generowanie zapytania (`getTaskQuery`)

Ta metoda przygotowuje polecenie dla Gmaila:

- Jeśli nie podamy daty (`since` jest puste), szuka po prostu w skrzynce odbiorczej: `in:inbox`.
    
- Jeśli podamy datę, tworzy zapytanie typu `after:123456789`, co pozwala pobrać tylko e-maile, które przyszły po określonym momencie w czasie.

| Element      | Co robi?                                                             |
| ------------ | -------------------------------------------------------------------- |
| @Component   | Rejestruje klasę w systemie [[Spring]]                               |
| Konstruktor  | Wczytuje słowa kluczowe i przygotowuje je do porównań (małe litery). |
| isTaskEmail  | Przeszukuje e-mail w poszukiwaniu dopasowań słów kluczowych.         |
| getTaskQuery | Tworzy tekstowe zapytanie dla API Gmaila.                            |
