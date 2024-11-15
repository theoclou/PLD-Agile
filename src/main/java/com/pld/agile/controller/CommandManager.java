package com.pld.agile.controller;



import com.pld.agile.controller.Command;

import com.pld.agile.model.entity.Round;



import java.util.Stack;



public class CommandManager {

    private Stack<Command> commandStack = new Stack<>();

    private Stack<Command> undoStack = new Stack<>();



    public void executeCommand(Command command) {

        command.execute();

        commandStack.push(command);

        undoStack.clear();


    }



    public void undo() {

        if (!commandStack.isEmpty()) {

            Command command = commandStack.pop();

            command.undo();

            undoStack.push(command);


        }

    }



    public Command getLastCommand() {

        if (!commandStack.isEmpty()) {

            return commandStack.peek();

        }

        return null;

    }



    public void redo() {

        if (!undoStack.isEmpty()) {

            Command command = undoStack.pop();

            command.execute();

            commandStack.push(command);


        }

    }



    public void resetCommandStack() {

        commandStack.clear();

        undoStack.clear();

    }

}
