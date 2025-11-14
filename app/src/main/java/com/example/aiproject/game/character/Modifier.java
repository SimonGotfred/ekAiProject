package com.example.aiproject.game.character;

import com.example.aiproject.game.Dice;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Modifier
{
    private Stat stat;
    private int value;
    private Dice dice;

    public int value()
    {
        if (dice == null) return value;
        return dice.roll(value);
    }
}
