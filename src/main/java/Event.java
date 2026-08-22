/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {

    private final String from;
    private final String to;

    /**
     * Creates an event task.
     *
     * @param description description of the event
     * @param from start date or time of the event
     * @param to end date or time of the event
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event with its type, completion status, and time range.
     *
     * @return the task formatted as
     *         "[E][ ] description (from: start to: end)"
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
