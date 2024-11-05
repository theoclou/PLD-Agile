package com.pld.agile.controller;

import com.pld.agile.model.entity.Round;

public interface Command {
    void execute();
    void undo();
    Round getRound(); // Nouvelle méthode pour récupérer le round
}
