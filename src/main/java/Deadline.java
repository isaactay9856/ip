/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {

    private final String by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task
     * @param by date or time by which the task must be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns a representation of this deadline suitable for saving to a file.
     *
     * @return the task type, completion status, description, and deadline
     */
    @Override
    public String toDataString() {
        return formatDataString("D") + " | " + by;
    }

    /**
     * Returns the deadline with its type, completion status, and due date.
     *
     * @return the task formatted as "[D][ ] description (by: date/time)"
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
