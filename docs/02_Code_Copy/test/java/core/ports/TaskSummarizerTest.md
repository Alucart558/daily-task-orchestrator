Sprawdzenie zachowania podsumowującego portu.
```java
@Test
void testSummarizerContract() {
    TasksSummary mockSummary = TestDataBuilder.buildSummarizedTasks();
    when(taskSummarizer.summarize(anyList())).thenReturn(mockSummary);

    // ... wywołanie metody i asercje ...
}
```
* `anyList()`: Argument Matcher z biblioteki Mockito. Bardzo przydatna rzecz! Oznacza to: "Nie obchodzi mnie, jaka konkretnie lista zostanie przekazana do metody `summarize`. Nieważne co to będzie, zmuś atrapę (mock), aby zwróciła przygotowany obiekt `mockSummary`".
