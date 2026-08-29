import java.io.IOException;
import java.nio.file.Path;

/**
 * Console chatbot for creating, updating, deleting, and persistently storing
 * todo, deadline, and event tasks.
 */
public class MEKA {
    /**
     * Prevents construction because MEKA is started through {@link #main(String[])}.
     */
    private MEKA() {
    }

    /**
     * Starts MEKA, loads saved tasks, and processes commands until input ends
     * or the user enters {@code bye}.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "meka.txt"));
        TaskList tasks;
        boolean storageAvailable = true;
        try {
            tasks = storage.load();
        } catch (DataFileException | IOException | SecurityException exception) {
            tasks = new TaskList();
            storageAvailable = false;
        }

        ui.showWelcome();
        if (!storageAvailable) {
            ui.showLoadingError();
        }
        ui.showLine();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            ui.showLine();
            try {
                String commandWord = Parser.getCommandWord(command);
                if (commandWord.equals("list")) {
                    ui.showTaskList(tasks);

                } else if (commandWord.equals("mark")) {
                    int taskNumber = Parser.parseTaskNumber(command, "mark");
                    Task task = tasks.get(taskNumber);
                    task.markAsDone();
                    saveTasks(storage, tasks, storageAvailable);
                    ui.showTaskMarked(task);

                } else if (commandWord.equals("unmark")) {
                    int taskNumber = Parser.parseTaskNumber(command, "unmark");
                    Task task = tasks.get(taskNumber);
                    task.unmark();
                    saveTasks(storage, tasks, storageAvailable);
                    ui.showTaskUnmarked(task);

                } else if (commandWord.equals("delete")) {
                    int taskNumber = Parser.parseTaskNumber(command, "delete");
                    Task task = tasks.delete(taskNumber);
                    saveTasks(storage, tasks, storageAvailable);
                    ui.showTaskDeleted(task, tasks.size());

                } else if (commandWord.equals("todo")) {
                    Task task = Parser.parseTodo(command);
                    tasks.add(task);
                    saveTasks(storage, tasks, storageAvailable);
                    ui.showTaskAdded(task, tasks.size());

                } else if (commandWord.equals("deadline")) {
                    Task task = Parser.parseDeadline(command);
                    tasks.add(task);
                    saveTasks(storage, tasks, storageAvailable);
                    ui.showTaskAdded(task, tasks.size());

                } else if (commandWord.equals("event")) {
                    Task task = Parser.parseEvent(command);
                    tasks.add(task);
                    saveTasks(storage, tasks, storageAvailable);
                    ui.showTaskAdded(task, tasks.size());

                }
            } catch (MekaException exception) {
                ui.showError(exception.getMessage());
            } catch (IOException | SecurityException exception) {
                storageAvailable = false;
                ui.showSavingError();
            }
            ui.showLine();
        }
    }

    /**
     * Saves all current tasks when the storage component is available.
     *
     * @param storage storage component that writes the data file
     * @param tasks tasks to save
     * @param storageAvailable whether loading or an earlier save succeeded
     * @throws IOException if storage is unavailable or the data file cannot be written
     */
    private static void saveTasks(Storage storage, TaskList tasks,
            boolean storageAvailable)
            throws IOException {
        if (!storageAvailable) {
            throw new IOException("Task storage is unavailable");
        }
        storage.save(tasks);
    }

}
