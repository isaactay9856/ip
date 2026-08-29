import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Loads tasks from and saves tasks to the application's data file.
 */
public class Storage {
    /** Location of the task data file. */
    private final Path filePath;

    /** Whether the data file remains safe and available for saving. */
    private boolean isAvailable;

    /**
     * Creates a storage component that uses the given data file.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
        this.isAvailable = true;
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return the saved tasks, or an empty list if the data file does not exist
     * @throws IOException if the data file cannot be read
     * @throws DataFileException if the data file contains an invalid record
     */
    public TaskList load() throws IOException, DataFileException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return new TaskList(tasks);
        }

        int lineNumber = 0;
        for (String originalLine : Files.readAllLines(filePath)) {
            lineNumber++;
            String line = originalLine;
            if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            if (!line.isBlank()) {
                tasks.add(parseTask(line, lineNumber));
            }
        }
        return new TaskList(tasks);
    }

    /**
     * Saves all current tasks to the data file.
     *
     * @param tasks task list to save
     * @throws IOException if the data directory or file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        if (!isAvailable) {
            throw new IOException("Task storage is unavailable");
        }
        Files.createDirectories(filePath.getParent());

        ArrayList<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toDataString());
        }
        Files.write(filePath, taskData);
    }

    /**
     * Prevents later save attempts after loading or saving has failed.
     */
    public void markUnavailable() {
        isAvailable = false;
    }

    /**
     * Converts one saved data line back into its corresponding task object.
     *
     * @param line pipe-separated task data
     * @param lineNumber line number used to identify invalid data
     * @return the reconstructed task
     * @throws DataFileException if the line contains invalid task data
     */
    private Task parseTask(String line, int lineNumber) throws DataFileException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 1) {
            throw invalidData(lineNumber, "missing task type");
        }

        int expectedFieldCount;
        switch (fields[0]) {
        case "T":
            expectedFieldCount = 3;
            break;
        case "D":
            expectedFieldCount = 4;
            break;
        case "E":
            expectedFieldCount = 5;
            break;
        default:
            throw invalidData(lineNumber, "unknown task type");
        }

        if (fields.length != expectedFieldCount) {
            throw invalidData(lineNumber, "incorrect number of fields");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw invalidData(lineNumber, "invalid completion status");
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw invalidData(lineNumber, "empty task detail");
            }
        }

        Task task;
        try {
            switch (fields[0]) {
            case "T":
                task = new Todo(fields[2]);
                break;
            case "D":
                task = new Deadline(fields[2], LocalDateTime.parse(fields[3]));
                break;
            case "E":
                task = new Event(fields[2], LocalDateTime.parse(fields[3]),
                        LocalDateTime.parse(fields[4]));
                break;
            default:
                throw invalidData(lineNumber, "unknown task type");
            }
        } catch (DateTimeParseException exception) {
            throw invalidData(lineNumber, "invalid date and time");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates a data-file exception that identifies the invalid line.
     *
     * @param lineNumber one-based line number
     * @param reason reason the record is invalid
     * @return an exception describing the invalid data
     */
    private DataFileException invalidData(int lineNumber, String reason) {
        return new DataFileException("Invalid data on line " + lineNumber + ": " + reason);
    }
}
