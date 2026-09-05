package meka.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meka.exception.DataFileException;
import meka.task.Deadline;
import meka.task.Event;
import meka.task.TaskList;
import meka.task.Todo;

public class StorageTest {
    @TempDir
    private Path directory;

    @Test
    public void load_missingFile_returnsEmptyList() throws Exception {
        assertEquals(0, new Storage(directory.resolve("missing.txt")).load().size());
    }

    @Test
    public void save_allTaskTypes_roundTripsTypesDatesOrderAndStatus() throws Exception {
        Storage storage = new Storage(directory.resolve("nested/meka.txt"));
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("meeting", LocalDateTime.of(2019, 12, 3, 9, 0),
                LocalDateTime.of(2019, 12, 4, 17, 30)));

        storage.save(tasks);
        TaskList loaded = storage.load();
        assertEquals(tasks.size(), loaded.size());
        for (int i = 1; i <= tasks.size(); i++) {
            assertEquals(tasks.get(i).getClass(), loaded.get(i).getClass());
            assertEquals(tasks.get(i).toDataString(), loaded.get(i).toDataString());
        }
    }

    @Test
    public void load_bomAndBlankLines_restoresValidTasks() throws Exception {
        Path file = directory.resolve("meka.txt");
        Files.writeString(file, "\uFEFFT | 1 | read book\n\nT | 0 | return book\n");
        TaskList tasks = new Storage(file).load();
        assertEquals(2, tasks.size());
        assertEquals("T | 1 | read book", tasks.get(1).toDataString());
    }

    @Test
    public void load_corruptRecords_reportsLineAndPreservesFile() throws Exception {
        String[] records = {
            "X | 0 | unknown", "T | 2 | bad status", "T | 0", "T | 0 | ",
            "D | 0 | task | not-a-date", "E | 0 | task | 2019-12-03T09:00 | bad"
        };
        Path file = directory.resolve("meka.txt");
        for (String record : records) {
            String content = "T | 0 | valid\n" + record + "\n";
            Files.writeString(file, content);
            DataFileException exception = assertThrows(DataFileException.class, () -> new Storage(file).load());
            assertEquals(true, exception.getMessage().startsWith("Invalid data on line 2:"));
            assertEquals(content, Files.readString(file));
        }
    }

    @Test
    public void markUnavailable_existingData_preventsOverwrite() throws Exception {
        Path file = directory.resolve("meka.txt");
        Files.writeString(file, "original contents\n");
        Storage storage = new Storage(file);
        storage.markUnavailable();
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
        assertEquals("original contents\n", Files.readString(file));
    }

    @Test
    public void load_directoryInsteadOfFile_throwsIoException() throws Exception {
        assertThrows(IOException.class, () -> new Storage(directory).load());
    }
}
