package com.example.aiproject.game.character;

import com.example.aiproject.game.Turn;
import com.example.aiproject.game.tool.Dice;
import com.example.aiproject.game.RandomSuite;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;

import static com.example.aiproject.game.character.Stat.*;

public class Character
{
    private static final Modifier baseRoll = new Modifier(2, Dice.d10);

    @Getter protected String name, description;
    @Getter protected List<Gear>     gear  = new ArrayList<>();
    protected EnumMap<Stat, Integer> stats = new EnumMap<>(Stat.class);

    public Character(Character base)
    {
        this.name         = base.name;
        this.description  = base.description;
        this.stats        = base.stats.clone();
        this.gear         . addAll(base.gear);
        this.stats        . put(FATIGUE,0);
    }

    public int    roll(Stat aptitude)               {return baseRoll.rollValue(aptitude, getStat(aptitude));}
    public String result()                          {return baseRoll.result(false);}

    public Gear act()                               {return RandomSuite.oneOf(this.getActions());}
    public Turn act(Character adversary)            {return act().use(this, adversary);} // todo: catch if no gear

    public Turn use(Gear gear, Character adversary) {return gear.use(this, adversary);}
    public Turn use(Gear gear)                      {return gear.use(this, this);}

    public List<Gear> getActions(){return gear.stream().filter(Gear::isAction).collect(Collectors.toList());}

    public boolean isAlive()         {return getStat(VIGOR) > 0;}
    public boolean isDead()          {return !isAlive();}
    public boolean isArmored()       {return resolveBonus(DEFENCE) > 0;}

    protected int rawStat(Stat stat) {return Optional.ofNullable(stats.getOrDefault(stat, 0)).orElse(0);}
    protected int rawDefence()       {return rawStat(ATHLETICS) + rawStat(INTELLIGENCE);}
    protected int rawVigor()         {return rawStat(ATHLETICS) + rawStat(WILLPOWER);}

    public int getStat(Stat stat)
    {
        switch (stat)
        {
            case VIGOR:     return Math.max(getStat(MAX_VIGOR) - getStat(FATIGUE), 0);
            case MAX_VIGOR: return Math.max(5*(rawVigor()   + resolveBonus(ATHLETICS, WILLPOWER, MAX_VIGOR)), 5);
            case DEFENCE:   return Math.max(5+(rawDefence() + resolveBonus(ATHLETICS, INTELLIGENCE, DEFENCE)),5);
            default:        return             rawStat(stat)+ resolveBonus(stat);
        }
    }

    public int resolveBonus(Stat... stats) // todo: beautify
    {
        int bonus = 0;
        List<Stat> statList = List.of(stats);
        if(statList.isEmpty()) return bonus;
        for (Gear gear : gear)
        {
            for (Modifier mod : gear.getModifiers())
            {
                if (statList.contains(mod.getStat())) bonus += mod.getModifier();
            }
        }
        return bonus;
    }

    public void decrease(Stat aptitude, int amount) {increase(aptitude, -amount);}
    public void decrease(Stat aptitude)             {decrease(aptitude,1);}
    public void increase(Stat aptitude)             {increase(aptitude,1);}
    public void increase(Stat aptitude, int amount)
    {
        if (aptitude.equals(VIGOR)) decrease(FATIGUE,amount);
        stats.replace(aptitude, Math.max(rawStat(aptitude)+amount,0));
    }

    public void increase(Modifier... modifier) {for (Modifier m : modifier) increase(m.getStat(), m.rollValue());}
    public void apply(Modifier... modifier)    {this.add(new Gear("Condition", modifier));}
    public void add(Gear... gear)              {this.gear.addAll(List.of(gear));}
    public void remove(Gear... gear)           {this.gear.removeAll(List.of(gear));}

    public String writeStat(Stat stat)
    {
        switch (stat)
        {
            case VIGOR: return MAX_VIGOR.icon + getStat(VIGOR) + '/' + getStat(MAX_VIGOR);
            case GOLD:  return stat.icon + getStat(stat);
            default:    return getStat(stat) + stat.icon;
        }
    }
}
