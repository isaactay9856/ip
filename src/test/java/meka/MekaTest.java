package meka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
