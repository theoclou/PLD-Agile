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



        // Update tourAttribution list

        Round round = command.getRound();

//        if (command instanceof AddDeliveryPointCommand) {
//
//            round.setTourAttribution(((AddDeliveryPointCommand) command).getUpdatedTours());
//
//        } else if (command instanceof DeleteDeliveryCommand) {
//
//            round.setTourAttribution(((DeleteDeliveryCommand) command).getUpdatedTours());
//
//        }

    }



    public void undo() {

        if (!commandStack.isEmpty()) {

            Command command = commandStack.pop();

            command.undo();

            undoStack.push(command);



            // Update tourAttribution list

//            Round round = command.getRound();
//
//            if (command instanceof AddDeliveryPointCommand) {
//
//                round.setTourAttribution(((AddDeliveryPointCommand) command).getUpdatedTours());
//
//            } else if (command instanceof DeleteDeliveryCommand) {
//
//                round.setTourAttribution(((DeleteDeliveryCommand) command).getUpdatedTours());
//
//            }

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



            // Update tourAttribution list

//            Round round = command.getRound();
//
//            if (command instanceof AddDeliveryPointCommand) {
//
//                round.setTourAttribution(((AddDeliveryPointCommand) command).getUpdatedTours());
//
//            } else if (command instanceof DeleteDeliveryCommand) {
//
//                round.setTourAttribution(((DeleteDeliveryCommand) command).getUpdatedTours());
//
//            }

        }

    }



    public void resetCommandStack() {

        commandStack.clear();

        undoStack.clear();

    }

}
