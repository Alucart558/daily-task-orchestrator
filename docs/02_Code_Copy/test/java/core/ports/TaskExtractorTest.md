```markdown
Sprawdzenie, czy ekstraktor potrafi zamienić surowe dane na listę `Task`.
```java
@ExtendWith(MockitoExtension.class)
class TaskExtractorTest {
    @Test
    void testExtractorContract() {
        TaskExtractor taskExtractor = new ClaudeRawDataAnalyzer();

        List<RawData> rawData = List.of(TestDataBuilder.buildRawData());
        List<Task> result = taskExtractor.extract(rawData);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Email", result.get(0).getTitle());
    }
}
```
* `TaskExtractor`: Port odpowiedzialny za zamianę danych surowych na znormalizowane zadania.
* `ClaudeRawDataAnalyzer`: Dummy implementacja, która na razie tylko mapuje dane bez prawdziwego wywołania Claude API.
* `assertEquals(1, result.size())`: Sprawdza, czy jeden surowy rekord został zamieniony na jedno zadanie.
```
