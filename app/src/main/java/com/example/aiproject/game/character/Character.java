package com.example.aiproject.game.character;

import com.example.aiproject.game.Dice;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import static com.example.aiproject.game.character.Stat.*;

public class Character
{
    @Getter protected String name, description;
    @Getter protected List<Gear> gear = new ArrayList<>();
    protected int athletics, intelligence, willpower, fatigue;

    public Character() {}

    public Character(Character base)
    {
        this.name         = base.name;
        this.description  = base.description;

        this.athletics    = base.athletics;
        this.intelligence = base.intelligence;
        this.willpower    = base.willpower;
        this.fatigue      = 0;

        this.gear         = List.copyOf(base.gear);
    }

    private int defence()
    {
        return getStat(ATHLETICS) + getStat(INTELLIGENCE) + resolveBonus(DEFENCE, ATHLETICS, INTELLIGENCE);
    }

    private int maxVigor()
    {
        return 5*(getStat(ATHLETICS) + getStat(WILLPOWER) + resolveBonus(VIGOR, ATHLETICS, WILLPOWER));
    }

    private int fatigue()
    {
        return fatigue + resolveBonus(FATIGUE);
    }

    public int roll(Stat aptitude)
    {
        return getStat(aptitude) + resolveBonus(aptitude) + Dice.d4.roll(2) - getStat(FATIGUE);
    }

    public String use(Gear gear, Character adversary)
    {
        return gear.use(this, adversary);
    }
    public String use(Gear gear)
    {
        return gear.use(this, this);
    }

    public int getStat(Stat stat)
    {
        switch (stat)
        {
            case VIGOR:        return Math.max(maxVigor() - getStat(FATIGUE),0);
            case MAX_VIGOR:    return maxVigor();
            case FATIGUE:      return fatigue();
            case DEFENCE:      return defence();
            case ATHLETICS:    return athletics;
            case INTELLIGENCE: return intelligence;
            case WILLPOWER:    return willpower;
            default:           return 0;
        }
    }

    public void increaseStat(Stat aptitude, int amount)
    {
        switch (aptitude)
        {
            case ATHLETICS:    athletics    += amount; if (athletics    < 0) athletics    = 0; return;
            case INTELLIGENCE: intelligence += amount; if (intelligence < 0) intelligence = 0; return;
            case WILLPOWER:    willpower    += amount; if (willpower    < 0) willpower    = 0; return;
            case FATIGUE:      fatigue      += amount; if (fatigue      < 0) fatigue      = 0; return;
        }
    }
    public void increaseStat(Stat aptitude)
    {
        increaseStat(aptitude, 1);
    }
    public void decreaseStat(Stat aptitude, int amount)
    {
        increaseStat(aptitude, -amount);
    }
    public void decreaseStat(Stat aptitude)
    {
        decreaseStat(aptitude, 1);
    }

    public void applyModifier(Modifier modifier)
    {
        increaseStat(modifier.getStat(), modifier.getValue());
    }

    public int resolveBonus(Stat... stats)
    {
        int bonus = 0;
        List<Stat> statList = List.of(stats);
        if(statList.isEmpty()) return bonus;
        for (Gear gear : gear)
        {
            for (Modifier mod : gear.getModifiers())
            {
                if (statList.contains(mod.getStat())) bonus += mod.getValue();
            }
        }
        return bonus;
    }
}
