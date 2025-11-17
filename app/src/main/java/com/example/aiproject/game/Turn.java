package com.example.aiproject.game;

import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.character.Character;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Turn
{
    private String actor;
    private String action;
    private String outcome;
    private String diceThrow;

    public Turn(Character actor, Gear action)        {this(actor.getName(), action.toString());}
    public Turn(String actorName, String actionName) {this.actor = actorName; this.action = actionName;}

    public String result(){return actor +": "+ diceThrow;}
}
