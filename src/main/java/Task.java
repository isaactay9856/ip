/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private String description;
    private boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        this.isDone = true;
    } // mark task as done

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with x
    }

    public void unmark() { this.isDone = false; } // mark task as not done

    /**
     * Returns the task with its completion status for display.
     *
     * @return a task formatted as "[x] description" or "[ ] description"
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}


