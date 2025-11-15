package com.example.aiproject.game.character;

import java.util.ArrayList;

import static com.example.aiproject.game.character.Stat.*;

public class Player extends Character
{
    ArrayList<Gear> inventory = new ArrayList<>();

    public Player(Character character)
    {
        super(character);
        stats.put(GOLD,0);
    }
}
