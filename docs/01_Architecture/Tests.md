W świecie Javy do testowania używamy zestawu specjalnych adnotacji (takich z małpą @, które dodają zachowanie do kodu) oraz metod z dwóch różnych bibliotek, które ze sobą współpracują: JUnit 5 oraz Mockito.
Oto zestawienie najważniejszych "komend" i wyjaśnienie, jak one działają pod spodem, w kontekście Twojej aplikacji.

### 🟢 JUnit 5 (Szkielet testu i sprawdzanie wyników)
JUnit to silnik. Odpowiada za to, żeby test się w ogóle uruchomił i żeby sprawdzić, czy wynik działania Twojego kodu jest zgodny z oczekiwaniami.
	•**@Test**
◦Jak działa: Kładziesz to nad metodą. Mówi kompilatorowi i środowisku IntelliJ: "Ta metoda to jest test jednostkowy, pojaw się obok niej zielony przycisk 'Play'".
	•**@BeforeEach / @AfterEach**
◦Jak działa: Metody z tymi adnotacjami uruchamiają się odpowiednio przed każdym lub po każdym teście w danej klasie. Służą np. do zresetowania stanu bazy danych przed każdym testem, aby testy na siebie nie wpływały.
	•**assertEquals(oczekiwane, rzeczywiste)**
◦Jak działa: Najważniejsza asercja (z pakietu org.junit.jupiter.api.Assertions). Bierze to, czego się spodziewasz, oraz to, co zwróciła Twoja metoda, i sprawdza, czy są równe. Jeśli nie są – test "oblewa" (świeci się na czerwono) i wywala błąd.
	•**assertThrows(KlasaWyjatku.class, () -> metoda())**
◦Jak działa: Sprawdza tzw. "nieszczęśliwe ścieżki" (sad paths). Mówisz: "Spodziewam się, że ta metoda w tej sytuacji wyrzuci wyjątek". Jeśli wyrzuci – test przechodzi na zielono! W Twoim kodzie fetch() może wyrzucić RuntimeException, więc to świetne miejsce na taki test.
### 🎭 Mockito (Tworzenie atrap / "wydmuszek")
Mockito rozwiązuje problem zewnętrznych zależności (jak sieć, baza danych, API Google). Pozwala stworzyć obiekty, które udają prawdziwe obiekty.
	•**@ExtendWith(MockitoExtension.class)**
◦Jak działa: Kładziesz to na samej górze klasy testowej. "Włącza" integrację Mockito z JUnit5.
	•**@Mock**
◦Jak działa: Tworzy obiekt-wydmuszkę (np. @Mock [[GmailApiClient]] apiClient). Ten fałszywy klient ma wszystkie metody prawdziwego, ale domyślnie one nic nie robią i zwracają null lub puste listy. Nie wysyłają żadnych żądań HTTP do Google!
	•**@InjectMocks**
◦Jak działa: Tworzy Twój testowany obiekt (np. @InjectMocks [[GmailDataSource]] dataSource) i magicznie wstrzykuje do niego wszystkie stworzone wcześniej @Mocki.
	•**when(wywolanieMetody).thenReturn(wynik)**
◦Jak to działa (Najważniejsza komenda!): Służy do "tresowania" Twoich atrap. Mówisz mockowi: "Słuchaj, w tym teście, gdy testowany kod wywoła na tobie metodę getEmails("after:123", 100L), to nie uderzaj do serwerów Google. Po prostu od razu zwróć tę zmyśloną, sztuczną listę maili, którą ci zaraz podam".
◦Uwaga odnośnie kaskadowych wywołań: Gdybyś mockował czyste API Google'a, wyglądałoby to wręcz absurdalnie: when(gmail.users().messages().list("me").execute()).thenReturn(...). Dlatego to świetnie, że opakowałeś API w [[GmailApiClient]]– testuje się to o niebo prościej!
	•**verify(mock).metoda(...)**
◦Jak to działa: Sprawdza zachowanie. Na koniec testu pytasz Mockito: "Czy mój kod faktycznie wywołał tę metodę z takimi parametrami na tej atrapie?".

Zobacz : [[TestDataBuilder]],[[DataSourceTest]],[[TaskAnalyzerTest]],[[TaskNotifierTest]],[[application-test.yaml]],[[TaskExtractorTest]],[[TaskSummarizerTest]],[[DailyTaskOrchestratorTest]],