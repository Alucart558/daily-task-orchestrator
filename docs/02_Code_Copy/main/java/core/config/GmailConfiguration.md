zadaniem jest **przechowywanie i zabezpieczanie informacji konfiguracyjnych**, które są niezbędne, aby Twoja aplikacja mogła bezpiecznie połączyć się z usługą Gmail (np. poprzez API Google).
### 1. Pola klasy (Dane)

Klasa posiada pięć pól, które przechowują kluczowe dane:

- `clientId`, `clientSecret`: Unikalne identyfikatory aplikacji w Google (odpowiednik loginu i hasła dla Twojej aplikacji).
    
- `redirectUri`: Adres, na który Google przekieruje użytkownika po zalogowaniu.
    
- `tokenDirectory`: Ścieżka do folderu na komputerze, gdzie aplikacja zapisze pliki sesji (tokeny dostępu).
    
- `scopes`: Lista uprawnień, o które aplikacja prosi użytkownika (np. "tylko czytanie maili" lub "wysyłanie maili").
    

Wszystkie te pola są `private` (ukryte przed dostępem z zewnątrz) oraz `final` (nie można ich zmienić po utworzeniu obiektu – co czyni klasę bezpieczną i niezmienną).

### 2. Konstruktor (Tworzenie obiektu)

Konstruktor służy do ustalenia wartości pól podczas tworzenia nowej konfiguracji:

- **Walidacja (`Objects.requireNonNull`):** To bardzo ważna część. Jeśli spróbujesz przekazać `null` (brak wartości) dla któregokolwiek z tych pól, program od razu przerwie działanie, wyrzucając jasny błąd. Dzięki temu nie ryzykujesz błędów aplikacji w późniejszym czasie.
    
- **Sprawdzanie poprawności (`isBlank`):** Dodatkowo kod sprawdza, czy `clientId` lub `clientSecret` nie są tylko pustymi tekstami (np. samymi spacjami). Jeśli są puste, rzucany jest zrozumiały komunikat o błędzie z instrukcją, gdzie szukać problemu.

### 3. Metody dostępowe (Gettery)

Ponieważ pola są prywatne, stworzono metody typu `get...`, aby inne części programu mogły odczytać te wartości.

Warto zwrócić uwagę na ciekawą funkcjonalność w metodzie `getTokenDirectory()`:

```java
public String getTokenDirectory() { 
    return tokenDirectory.replaceFirst("^~", System.getProperty("user.home")); 
}
```

- Dzięki temu możesz w konfiguracji użyć znaku `~` (tylda), który w systemach typu Linux/macOS oznacza "katalog domowy użytkownika".
    
- Kod automatycznie podmienia ten znak na prawdziwą ścieżkę w systemie operacyjnym (np. `C:\Users\TwojeImie` w Windows lub `/home/TwojeImie` w Linux), dzięki czemu konfiguracja jest przenośna między komputerami.