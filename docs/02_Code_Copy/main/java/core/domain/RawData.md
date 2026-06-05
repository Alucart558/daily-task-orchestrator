### 1. Adres i Narzędzia (Pakiet i Importy)
```java
package com.dailytask.core.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
```
- **`package`**: To po prostu "adres" tego pliku w projekcie. Działa jak folder na Twoim komputerze, żeby utrzymać porządek.
    
- **`import`**: Zanim zaczniemy budować, musimy wziąć narzędzia z wbudowanej "skrzynki narzędziowej" Javy. Tutaj programista pożycza narzędzia do obsługi daty i czasu (`LocalDateTime`) oraz do pracy ze zbiorami danych (`Collections`, `Map`).
### 2. Definicja Pojemnika (Klasa i Pola)
```java
public class RawData {
    private final String source;
    private final String title;
    // ... i reszta pól
```
- **`public class RawData`**: Tworzymy nowy "szablon" lub "formularz", który nazywa się `RawData` (Surowe Dane). `public` oznacza, że inne części programu mogą z tego szablonu korzystać.
    
- **Pola (`private final ...`)**: To są rubryki w naszym formularzu (np. źródło, tytuł, treść, data pobrania).
    
    - **`private`** oznacza, że te dane są ukryte wewnątrz pojemnika i nikt z zewnątrz nie może w nich bezpośrednio grzebać.
        
    - **`final`** to bardzo ważne słowo. Oznacza, że **kiedy raz wypełnisz tę rubrykę, nie można jej już nigdy zmienić**. Pojemnik jest zapieczętowany.
### 3. Fabryka Pojemników (Konstruktory)
Konstruktor to specjalny mechanizm (taka mini-fabryka), który służy do tworzenia i wypełniania naszego pojemnika. W tym kodzie mamy **dwa** konstruktory.

**Główny konstruktor ("Enhanced constructor"):**
```java
public RawData(String source, String title, String rawContent, LocalDateTime fetchedAt, 
               String sender, String originalSource, String priority, Map<String, String> metadata) {
    this.source = source;
    // ... przypisywanie wartości
    this.metadata = metadata != null ? Map.copyOf(metadata) : Collections.emptyMap();
}
```
Ten konstruktor przyjmuje wszystkie 8 informacji z zewnątrz i wpisuje je do rubryk naszego formularza (np. `this.source = source` oznacza "weź wartość podaną przez użytkownika i wpisz do mojej prywatnej rubryki").

> **Ciekawostka:** Spójrz na linijkę z `metadata`. To bardzo sprytny kod. Mówi on: _"Jeśli ktoś podał jakieś metadane, zrób ich niezmienną kopię (`Map.copyOf`). Jeśli nie podał nic (`null`), stwórz pusty zbiór (`Collections.emptyMap()`)."_ Dzięki temu program nigdy nie zawiesi się z powodu braku danych!

**Stary konstruktor ("Original constructor"):**
```java
public RawData(String source, String title, String rawContent, LocalDateTime fetchedAt) {
    this(source, title, rawContent, fetchedAt, null, null, null, Collections.emptyMap());
}
```
Po co nam dwa? Komentarz mówi o "backward compatibility" (kompatybilności wstecznej). Prawdopodobnie w starszej wersji programu ten pojemnik miał tylko 4 rubryki. Żeby nie zepsuć starego kodu, zostawiono stary konstruktor. Bierze on 4 podstawowe informacje, a brakujące 4 wypełnia pustymi wartościami (`null` i pusta mapa), po czym przekazuje to wszystko do głównego konstruktora za pomocą słówka `this(...)`.
### 4. Okienka do Odczytu (Gettery)
```java
public String getSource() { return source; }
public String getTitle() { return title; }
// ...
```
Pamiętasz, jak na początku użyliśmy słowa `private`, żeby ukryć i zabezpieczyć dane? Skoro są ukryte, to jak reszta programu ma je przeczytać? Właśnie do tego służą **gettery** (od angielskiego _get_ - daj/weź). To takie bezpieczne "okienka", przez które inne części programu mogą tylko **podejrzeć** zawartość, ale nie mogą jej zmienić (bo nie ma tu tzw. _setterów_).