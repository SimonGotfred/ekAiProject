package com.example.aiproject.game.tool;

public class DiceRoll
{
    final int result;
    final int modifier;
    final int rolls;
    final Dice dice;

    public DiceRoll(int rolls, Dice dice, int modifier)
    {
        this.modifier = modifier;
        this.rolls    = rolls;
        this.dice     = dice;
        this.result   = dice==null ? modifier : dice.roll(rolls)+modifier;
    }

    public DiceRoll reRoll(){return new DiceRoll(rolls,dice,modifier);}

    public String formular(){return
            (dice==null  ? "" : rolls+dice.name().toLowerCase()) +
            (modifier >0 ? '+': "") +
            (modifier==0 ? "" : modifier);}

    public String toString(){return formular()+'='+result;}
}
