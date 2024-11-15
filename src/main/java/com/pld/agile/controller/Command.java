package com.pld.agile.controller;

import com.pld.agile.model.entity.Round;

/**
 * The {@code Command} interface defines the contract for implementing commands
 * in the Command design pattern. Each command encapsulates an operation that
 * can be executed, undone, and potentially associated with a {@link Round}.
 */
public interface Command {

    /**
     * Executes the command, performing the encapsulated operation.
     */
    void execute();

    /**
     * Undoes the command, reverting the changes made during execution.
     */
    void undo();

    /**
     * Retrieves the {@link Round} associated with this command.
     *
     * @return The {@link Round} object related to this command.
     */
    Round getRound();
}
