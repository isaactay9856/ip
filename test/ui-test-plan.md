# UI Test Plan

This file records console UI test cases for the project. Run test cases in the listed order and stop at the first failure.

## Program setup

- **Compile command:** `javac -encoding UTF-8 -d out src/main/java/*.java` using Java 25
- **Run command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp out MEKA` using Java 25
- **Comparison rules:** Exact text comparison, normalizing only CRLF/LF line endings unless stated otherwise.
- **Other setup:** Start with an empty in-memory task list. Capture standard output as UTF-8. No files are required.

## Test cases

### UI-01: Add and list all task types

- **Aim:** Verify that Todo, Deadline, and Event commands create correctly formatted tasks, update the task count, and appear in the list.
- **Inputs (in order):**

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 1. [T][ ] borrow book
 2. [D][ ] return book (by: Sunday)
 3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-02: Reject invalid commands without changing task state

- **Aim:** Verify that missing or non-numeric task numbers, empty task descriptions, and unknown commands produce the specified errors. Valid commands are interleaved with invalid commands, and the final list confirms that rejected commands did not add or modify tasks.
- **Inputs (in order):**

```text
todo first task
mark
todo
todo second task
mark one
mark 1
unmark
deadline /by Sunday
event team sync /from 2pm /to 3pm
unmark none
event /from 4pm /to 5pm
dance
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed
____________________________________________________________
____________________________________________________________
 The following command requires a task description to proceed
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] second task
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] first task
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed
____________________________________________________________
____________________________________________________________
 The following command requires a task description to proceed
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team sync (from: 2pm to: 3pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 The following command requires a number to proceed
____________________________________________________________
____________________________________________________________
 The following command requires a task description to proceed
____________________________________________________________
____________________________________________________________
 I do not understand this command. Please input a valid command.
____________________________________________________________
____________________________________________________________
 1. [T][X] first task
 2. [T][ ] second task
 3. [E][ ] team sync (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-03: Mark and unmark a valid task

- **Aim:** Verify that valid numeric task references change completion state correctly and that the final task list reflects the latest state.
- **Inputs (in order):**

```text
todo reversible task
mark 1
unmark 1
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] reversible task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] reversible task
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] reversible task
____________________________________________________________
____________________________________________________________
 1. [T][ ] reversible task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
