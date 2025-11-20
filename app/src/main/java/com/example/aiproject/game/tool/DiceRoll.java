package com.example.aiproject.game.tool;

import lombok.Getter;

@Getter
public class DiceRoll
{
    protected int result;
    protected int modifier;
    protected int rolls;
    protected Dice dice;

    public DiceRoll(Dice dice){this(1,dice);}
    public DiceRoll(int rolls, Dice dice){this(rolls,dice,0);}
    public DiceRoll(int rolls, Dice dice, int modifier)
    {
        this.modifier = modifier;
        this.rolls    = rolls;
        this.dice     = dice;
        this.result   = dice==null ? modifier : dice.roll(rolls)+modifier;
    }

    public int roll(){return this.result= dice==null ? modifier : dice.roll(rolls)+modifier;}

    public String formular(){return
            (dice==null  ? "" : rolls+dice.name().toLowerCase()) +
            (modifier >0 ? '+': "") +
            (modifier==0 ? "" : modifier);}

    public String toString(){return formular()+'='+result;}
}
