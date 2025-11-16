package com.example.aiproject.game.tool;

import java.util.Random;

public enum Dice
{
    d4(4),
    d6(6),
    d8(8),
    d10(10),
    d12(12),
    d20(20),
    d100(100);

    private final static Random roller = new Random();

    public final int value;

    Dice(int value) {this.value = value;}

    public int roll() {return roller.nextInt(value)+1;}

    public int roll(int times)
    {
        if (times < 1) return 0;
        return this.roll() + this.roll(times - 1);
    }

    public boolean against(int challenge) {return this.roll() > challenge;}
    public boolean against(Dice die) {return this.against(die.roll());}
}