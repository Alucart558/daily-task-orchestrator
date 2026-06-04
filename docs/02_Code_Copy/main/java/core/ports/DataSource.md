Definiuje, jak aplikacja komunikuje się z każdym zewnętrznym źródłem danych.
```java
public interface DataSource {
    List<RawData> fetch(Instant from);
    String getName();
}
```
* `interface`: Nie ma tu ciała metod (brak klamerek `{}`). Wymuszamy tylko, aby każda klasa udająca źródło danych miała metodę `fetch(Instant from)`, która zwraca listę obiektów `RawData`.
* `Instant from`: Punkt startowy pobierania danych. Dzięki temu źródło wie, od kiedy ma szukać maili albo ogłoszeń.
