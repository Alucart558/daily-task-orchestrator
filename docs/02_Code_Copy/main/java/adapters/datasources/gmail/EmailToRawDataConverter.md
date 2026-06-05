Ten kod to typowy przykład **konwertera** (tłumacza). Jego zadaniem jest wzięcie informacji w formacie, w jakim dostarcza je Gmail ([[GmailMessage]]), i "przetłumaczenie" ich na ujednolicony format ([[RawData]]), którego używa reszta Twojej aplikacji.

Dzięki takiemu rozwiązaniu, reszta systemu nie musi wiedzieć, jak działa Gmail – interesuje go tylko uniwersalny format [[RawData]].
#### 1. Co to za klasa?

- **`@Component`**: Ta adnotacja mówi Springowi: „Hej, to jest ważny kawałek kodu, zarządzaj nim”. Dzięki temu w innych miejscach aplikacji możesz łatwo użyć tej klasy, nie martwiąc się o jej ręczne tworzenie.
    
- **[[EmailToRawDataConverter]]**: Klasa ma jedną, główną metodę: `convert`
#### 2. Metoda `convert` – "serce" kodu

Metoda przyjmuje obiekt typu [[GmailMessage]] i zwraca gotowy obiekt `RawData`.

- **Zabezpieczenie (`if (message == null)`):** Programista sprawdza, czy wiadomość w ogóle istnieje. Jeśli ktoś przekaże "puste nic" (null), kod wyrzuci błąd, zamiast spowodować awarię w dalszej części.
    
- **Przygotowanie metadanych (`Map<String, String> metadata`):** Tworzona jest mapa (czyli zestaw par klucz-wartość), w której zapisywane są dodatkowe informacje, takie jak etykiety (np. "praca", "ważne") oraz informacja, czy mail jest nieprzeczytany.
    
- **Bezpieczne przypisanie danych:** W liniach z `?` i `:` (tzw. operator trójargumentowy) kod sprawdza: „Jeśli dane istnieją, użyj ich. Jeśli są puste (null), wstaw wartość domyślną”.
    
    - _Przykład:_ Jeśli mail nie ma tematu, zamiast błędu otrzymasz napis "No Subject".
        
- **Tworzenie wyniku (`return new RawData(...)`):** Na końcu kod tworzy nowy obiekt `RawData`, wypełniając go wszystkimi wyciągniętymi informacjami: nadawcą, treścią, datą i przygotowaną wcześniej mapą metadanych.