Ten kod to **"strażnik kluczy"** dla Twojej aplikacji. Jego zadaniem jest uzyskanie pozwolenia od użytkownika na dostęp do jego konta Gmail, a następnie bezpieczne przechowywanie tego "pozwolenia", aby Twoja aplikacja mogła czytać lub wysyłać e-maile w imieniu użytkownika bez konieczności ciągłego podawania przez niego hasła.

### Jak działa ten proces krok po kroku?

Wyobraź sobie, że chcesz wejść do strzeżonego budynku (Gmail API). Zamiast dawać Ci klucze główne (hasło użytkownika), system wydaje Ci specjalną kartę dostępu (token).

#### 1. Przygotowanie (Konfiguracja)

- **`GoogleClientSecrets`**: Aplikacja mówi: "Jestem zarejestrowana u Google jako aplikacja o nazwie X". Używa do tego `ClientId` i `ClientSecret` pobranych z konfiguracji.
    
- **`FileDataStoreFactory`**: To miejsce, gdzie aplikacja zapisuje otrzymany "token" (kartę dostępu) na dysku, aby nie trzeba było pytać o zgodę przy każdym uruchomieniu programu.
#### 2. Rozpoczęcie przepływu (Flow)

- **`GoogleAuthorizationCodeFlow`**: To silnik całego procesu. Definiuje on, jakie uprawnienia chcemy uzyskać (np. tylko odczyt poczty, wysyłanie e-maili – to jest w `config.getScopes()`).
    
- **`LocalServerReceiver`**: To sprytny trik. Aby otrzymać token, Google musi odesłać kod autoryzacyjny do Twojej aplikacji. Klasa ta uruchamia na chwilę mały serwer na Twoim komputerze (na porcie 8888), który "łapie" ten kod po tym, jak użytkownik zaloguje się w przeglądarce.
#### 3. Autoryzacja

- **`AuthorizationCodeInstalledApp`**: To "[[Interfaces]]" dla użytkownika. Otwiera przeglądarkę, w której użytkownik loguje się do swojego Gmaila i klika przycisk "Zezwól".
#### 4. Zarządzanie tokenami

To najważniejsza część dla stabilności programu:

- **`Access Type: offline`**: Dzięki temu Google wysyła nie tylko krótki dostęp (`AccessToken`), ale także `RefreshToken`.
    
- **Co to daje?** `AccessToken` wygasa bardzo szybko (np. po godzinie). `RefreshToken` pozwala aplikacji automatycznie pobrać nowy dostęp bez angażowania użytkownika. Kod sprawdza, czy token jest ważny, a jeśli nie – automatycznie go odświeża (`credential.refreshToken()`).


| Komponent            | Rola                                                                                  |
| -------------------- | ------------------------------------------------------------------------------------- |
| GmailConfiguration   | Zewnętrzne źródło Twoich haseł i ID aplikacji.                                        |
| Credential           | To Twój "cyfrowy paszport" – zawiera tokeny potrzebne do każdego zapytania do Gmaila. |
| LocalServerReceiver  | Pozwala aplikacji "porozmawiać" z przeglądarką i odebrać zgodę użytkownika.           |
| FileDataStoreFactory | "Sejf" na dysku, w którym trzymasz klucze, żeby nie prosić o nie za każdym razem.     |
###  `AuthorizationCodeInstalledApp` – "Kierowca" (Logika procesu)

To jest klasa nadrzędna, która zarządza **całym procesem autoryzacji**. Możesz o niej myśleć jak o orkiestrze.

- **Co robi:** Wie, kiedy zacząć proces, wie jak poprosić `flow` o wygenerowanie URL-a do logowania i – co najważniejsze – **wie, jak otworzyć przeglądarkę** na komputerze użytkownika.
    
- **Jej rola:** To ona mówi: "Hej, użytkowniku, otwórz przeglądarkę pod tym adresem, żeby się zalogować". Nie przejmuje się tym, co stanie się potem – ona po prostu inicjuje kontakt z człowiekiem.
    

###  `LocalServerReceiver` – "Słuchawka" (Techniczny odbiór)

To jest specjalistyczne narzędzie, które `AuthorizationCodeInstalledApp` wykorzystuje do "złapania" odpowiedzi od Google.

- **Co robi:** To **aktywny serwer**. W momencie, gdy użytkownik klika "Zezwól" w przeglądarce, Google wysyła odpowiedź (kod autoryzacyjny) nie bezpośrednio do Twojego głównego programu, ale pod adres `localhost:8888`.
    
- **Jej rola:** Czeka w "ukryciu", aż przeglądarka odeśle informację zwrotną z Google. Gdy tylko otrzyma ten kod, przekazuje go z powrotem do `AuthorizationCodeInstalledApp`, aby ten mógł wymienić go na ostateczny `Credential` (token).

| Cecha              | AuthorizationCodeInstalledApp               | LocalServerReceiver                                             |
| ------------------ | ------------------------------------------- | --------------------------------------------------------------- |
| Główna rola        | Zarządzanie przepływem (flow).              | Odbieranie odpowiedzi od serwera Google.                        |
| Interakcja         | Otwiera przeglądarkę (widzi ją użytkownik). | Uruchamia nasłuchiwanie na porcie (ukryte przed użytkownikiem). |
| Kluczowe działanie | Wywołuje przeglądarkę.                      | "Łapie" kod z adresu URL po zalogowaniu.                        |
