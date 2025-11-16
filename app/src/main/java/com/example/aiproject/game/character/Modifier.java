package com.example.aiproject.game.character;

import com.example.aiproject.game.tool.Dice;

import lombok.Getter;

@Getter
public class Modifier
{
    private Stat stat;
    private int value;
    private Dice dice;

    public int rollValue()
    {
        if (dice == null) return value;
        return dice.roll(value);
    }

    @androidx.annotation.NonNull
    public String toString()
    {
        if (dice == null) return value + stat.icon;
        return value + dice.name() + stat.icon;
    }
}
