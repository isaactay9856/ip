package meka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MekaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_validTodo_updatesSharedTaskList() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = meka.getResponse("todo read book");
        String listResponse = meka.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("1. [T][ ] read book", listResponse);
    }

    @Test
    void getResponse_invalidCommand_returnsFriendlyError() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String response = meka.getResponse("dance");

        assertEquals("I do not understand this command. Please input a valid command.", response);
        assertFalse(meka.isExitRequested());
    }

    @Test
    void getResponse_bye_requestsExit() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String response = meka.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(meka.isExitRequested());
    }

    @Test
    void getResponse_archiveAll_movesTasksAndReportsEmptyList() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Meka meka = new Meka(activeFile.toString());
        meka.getResponse("todo first task");
        meka.getResponse("todo second task");

        String archiveResponse = meka.getResponse("archive all");

        assertEquals("Noted. I've archived all 2 tasks." + System.lineSeparator()
                + " Now you have 0 tasks in the list.", archiveResponse);
        assertEquals("", meka.getResponse("list"));
        assertEquals("", Files.readString(activeFile));
        assertEquals("T | 0 | first task" + System.lineSeparator()
                        + "T | 0 | second task" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("tasks-archive.txt")));
    }

    @Test
    void getResponse_archiveAllWithEmptyList_reportsNoTasks() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String response = meka.getResponse("archive all");

        assertEquals("There are no tasks to archive.", response);
    }
}
