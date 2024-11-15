package com.pld.agile.controller;

import java.util.Stack;

/**
 * The {@code CommandManager} class is responsible for managing the execution,
 * undoing, and redoing of commands in the Command design pattern.
 * It maintains two stacks to track the executed commands and undone commands,
 * enabling seamless undo and redo functionality.
 */
public class CommandManager {

    /**
     * Stack to store executed commands for potential undo operations.
     */
    private Stack<Command> commandStack = new Stack<>();

    /**
     * Stack to store undone commands for potential redo operations.
     */
    private Stack<Command> undoStack = new Stack<>();

    /**
     * Executes a given command, adds it to the command stack, and clears the undo stack.
     *
     * @param command the {@link Command} to be executed
     */
    public void executeCommand(Command command) {
        command.execute();
        commandStack.push(command);
        undoStack.clear();
    }

    /**
     * Undoes the last executed command, moving it to the undo stack.
     * If there are no commands to undo, this method does nothing.
     */
    public void undo() {
        if (!commandStack.isEmpty()) {
            Command command = commandStack.pop();
            command.undo();
            undoStack.push(command);
        }
    }

    /**
     * Retrieves the last executed command without removing it from the command stack.
     *
     * @return the last executed {@link Command}, or {@code null} if the stack is empty
     */
    public Command getLastCommand() {
        if (!commandStack.isEmpty()) {
            return commandStack.peek();
        }
        return null;
    }

    /**
     * Redoes the last undone command, moving it back to the command stack.
     * If there are no commands to redo, this method does nothing.
     */
    public void redo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.execute();
            commandStack.push(command);
        }
    }

    /**
     * Clears both the command stack and the undo stack, resetting the command history.
     */
    public void resetCommandStack() {
        commandStack.clear();
        undoStack.clear();
    }
}
