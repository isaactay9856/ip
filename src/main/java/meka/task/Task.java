package meka.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Description entered by the user for this task. */
    private String description;

    /** Whether this task has been marked as completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Returns the symbol used to show whether this task is completed.
     *
     * @return {@code "X"} if the task is completed, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns a representation of this task suitable for saving to a file.
     *
     * @return the task type, completion status, and description
     */
    public String toDataString() {
        return formatDataString("T");
    }

    /**
     * Formats the common fields shared by all saved task types.
     *
     * @param taskType letter identifying the task type
     * @return the task type, completion status, and description
     */
    protected String formatDataString(String taskType) {
        return taskType + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the task with its completion status for display.
     *
     * @return a task formatted as "[X] description" or "[ ] description"
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
