package meka;

import java.io.IOException;
import java.nio.file.Path;

import meka.command.Command;
import meka.exception.DataFileException;
import meka.exception.MekaException;
import meka.parser.Parser;
import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Coordinates MEKA's user interface, task list, command parser, and storage.
 */
public class Meka {
    /** Storage component used to load and save tasks. */
    private final Storage storage;

    /** Tasks available in the current MEKA session. */
    private final TaskList tasks;

    /** Console interface used to interact with the user. */
    private final Ui ui;

    /** Whether startup encountered an error while loading saved tasks. */
    private final boolean hasLoadingError;

    /**
     * Creates MEKA and loads tasks from the specified data file.
     *
     * If loading fails, MEKA starts with an empty in-memory task list and
     * avoids overwriting the unreadable data file.
     *
     * @param filePath location of the task data file
     */
    public Meka(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        boolean loadingFailed = false;
        try {
            loadedTasks = storage.load();
        } catch (DataFileException | IOException | SecurityException exception) {
            loadedTasks = new TaskList();
            loadingFailed = true;
            storage.markUnavailable();
        }
        tasks = loadedTasks;
        hasLoadingError = loadingFailed;
    }

    /**
     * Processes commands until input ends or an exit command is executed.
     */
    public void run() {
        ui.showWelcome();
        if (hasLoadingError) {
            ui.showLoadingError();
        }
        ui.showLine();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (MekaException exception) {
                ui.showError(exception.getMessage());
            } catch (IOException | SecurityException exception) {
                storage.markUnavailable();
                ui.showSavingError();
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts MEKA using its default task data file.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        new Meka("data/meka.txt").run();
    }
}
