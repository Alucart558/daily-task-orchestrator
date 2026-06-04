Kontrakt definiujący, jak aplikacja powinna zlecać podsumowanie zadań (np. do sztucznej inteligencji).
```java
public interface TaskSummarizer {
    TasksSummary summarize(List<Task> tasks);
}
```
* **Wejście:** `List<Task>` – przyjmuje listę znormalizowanych zadań (czyli już po odfiltrowaniu zanieczyszczeń z surowych emaili).
* **Wyjście:** `TasksSummary` – musi zwrócić jeden zbiorczy obiekt, który zawiera posegregowane zadania, harmonogram i podsumowanie.
