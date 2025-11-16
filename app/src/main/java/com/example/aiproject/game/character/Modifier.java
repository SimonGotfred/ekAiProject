package com.example.aiproject.game.character;

import com.example.aiproject.game.tool.Dice;

import androidx.annotation.NonNull;
import lombok.Getter;

@Getter
public class Modifier
{
    private int  rolls =1, modifier, result;
    private Dice dice;
    private Stat stat;

    public Modifier(int rolls, Dice dice) // beware! only meant for characters base roll!
    {
        this.rolls = rolls;
        this.dice  = dice;
    }

    public int rollValue()
    {
        if (dice == null) return modifier + rolls; // ssh... it just works... xD
        result = dice.roll(rolls);
        return result + modifier;
    }

    public int rollValue(Stat stat, int modifier)
    {
        this.stat = stat;this.modifier = modifier;
        return rollValue();
    }

    public  String template() {return template("");}
    private String template(String result)
    {
        if (dice == null) return modifier + rolls + "";
        if (modifier > 0) return rolls + dice.name() + result + '+' + modifier;
        if (modifier < 0) return rolls + dice.name() + result + modifier;
        return rolls + dice.name() + result;
    }

    @NonNull
    public String toString() {return template()+stat.icon;}
    public String result()   {return template("=<b>" + result + "</b> ");}
    public String result(boolean iconInFront){return iconInFront ? stat.icon+result() : result()+stat.icon;}
}
