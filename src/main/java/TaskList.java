import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages the collection of tasks used by MEKA.
 */
public class TaskList implements Iterable<Task> {
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "The task number does not exist in the list.";

    /** Tasks in the order in which they appear to the user. */
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their current order.
     *
     * @param tasks tasks with which to initialise the list
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task identified by a one-based task number.
     *
     * @param taskNumber one-based task number supplied by the user
     * @return the selected task
     * @throws MekaException if the number is outside the task list
     */
    public Task get(int taskNumber) throws MekaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MekaException(INVALID_TASK_NUMBER_MESSAGE);
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns the task identified by a one-based task number.
     *
     * @param taskNumber one-based task number supplied by the user
     * @return the removed task
     * @throws MekaException if the number is outside the task list
     */
    public Task delete(int taskNumber) throws MekaException {
        Task task = get(taskNumber);
        tasks.remove(taskNumber - 1);
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an iterator over the tasks in display order.
     *
     * @return task iterator
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
