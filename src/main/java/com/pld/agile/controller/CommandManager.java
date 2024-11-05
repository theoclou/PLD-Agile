package com.pld.agile.controller;

import com.pld.agile.controller.Command;

import java.util.Stack;

public class CommandManager {
    private Stack<Command> commandStack = new Stack<>(); // Stack to hold executed commands
    private Stack<Command> undoStack = new Stack<>();    // Stack to hold undone commands

    // Execute a command
    public void executeCommand(Command command) {
        command.execute(); // Call the execute method
        commandStack.push(command);   // Push command onto executed stack
        undoStack.clear();            // Clear redo stack when a new command is executed
    }

    // Undo the last command
    public void undo() {
        if (!commandStack.isEmpty()) {
            Command command = commandStack.pop(); // Get the last executed command
            command.undo();                       // Call undo on the command
            undoStack.push(command);              // Push the command onto the undo stack
        }
    }

    public Command getLastCommand() {
        if (!commandStack.isEmpty()) {
            return commandStack.peek();
        }
        return null;
    }


    // Redo the last undone command
    public void redo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop(); // Get the last undone command
            command.execute();                  // Re-execute the command
            commandStack.push(command);        // Push it back onto the executed stack
        }
    }
}
