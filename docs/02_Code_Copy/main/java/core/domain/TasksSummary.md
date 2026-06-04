```markdown
Reprezentuje finalny wynik podsumowania zadań. Zawiera listę zadań, streszczenie i rekomendacje.
```java
public class TasksSummary {
    private final List<Task> allTasks;
    private final String summary;
    private final List<String> recommendations;

    public TasksSummary(List<Task> allTasks, String summary, String schedule, List<String> recommendations) {
        this.allTasks = allTasks;
        this.summary = summary;
        this.recommendations = recommendations;
    }
}
```
* `final`: Dane raportu są traktowane jako wynik końcowy, nie chcemy ich zmieniać w trakcie działania programu.
* `allTasks`: Lista wszystkich znormalizowanych zadań przekazanych do podsumowania.
* `recommendations`: Lista krótkich rekomendacji wygenerowanych przez AI.
```
