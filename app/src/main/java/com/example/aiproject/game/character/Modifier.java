package com.example.aiproject.game.character;

public class Modifier
{
    Stat stat;
    int value;

    public Modifier(int value, Stat stat)
    {
        this.stat  = stat;
        this.value = value;
    }
}
