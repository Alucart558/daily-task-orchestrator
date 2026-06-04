Udaje, że komunikuje się z API Claude AI w celu podsumowania zadań.
```java
public class ClaudeTasksSummarizer implements TaskSummarizer {
    private static final Logger logger = LoggerFactory.getLogger(ClaudeTasksSummarizer.class);

    @Override
    public TasksSummary summarize(List<Task> tasks) {
        logger.info("Analyzing {} tasks using Claude AI...", tasks.size());
        
        return new TasksSummary(
                tasks,
                "Dummy Summary: You have " + tasks.size() + " tasks pending.",
                "Dummy Schedule: Do everything ASAP.",
                new ArrayList<>()
        );
    }
}
```
* `implements TaskSummarizer`: Gwarantuje, że klasa posiada metodę `summarize`.
* `tasks.size()`: Zlicza ile elementów znajduje się na liście. Przekazujemy tę wartość do loggera za pomocą znaczników `{}` (np. "Analyzing 5 tasks...").
* `new TasksSummary(...)`: Ponieważ nie podpięliśmy jeszcze prawdziwego API Claude'a, tworzymy "zaślepkę" (ang. _dummy/stub_). Zwracamy obiekt ze sztucznym tekstem.
