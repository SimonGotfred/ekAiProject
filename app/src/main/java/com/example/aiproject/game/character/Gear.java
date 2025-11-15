package com.example.aiproject.game.character;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import static com.example.aiproject.game.character.Stat.*;

@Getter
public class Gear
{
    private String         name;
    private Stat           aptitude;
    private List<Modifier> modifiers = new ArrayList<>();
    private short          duration  = 0;
    private boolean        loot      = false;

    public Gear(String name, Modifier... modifier)
    {
        this.name = name;
        this.modifiers = List.of(modifier);
        duration       = -1;
    }

    public boolean isAction() {return aptitude != null;}

    public String use(Character user, Character opponent)
    {
        switch (aptitude)
        {
            case VIGOR:        user.increase(modifiers.toArray(new Modifier[]{})); return "";
            case ATHLETICS:    return attack(user, opponent, MELEE);
            case INTELLIGENCE: return attack(user, opponent, RANGED);
            case WILLPOWER:    return attack(user, opponent, MAGIC);
            default:           return "";
        }
    }

    private String attack(Character user, Character opponent, Stat attackType)
    {
        StringBuilder str = new StringBuilder();
        int roll = user.roll(aptitude);

        str.append("Using their " + name + ", " + user.name + " rolls " + roll + " against "
                   + opponent.name + "'s " + opponent.getStat(DEFENCE) + " defence, ");

        if (roll >= opponent.getStat(DEFENCE))
        {
            int damage = modifiers.get(0).getValue() + user.getStat(aptitude);
            opponent.increase(FATIGUE, damage);

            str.append("dealing " + damage + " damage. ");

            if (opponent.getStat(VIGOR) < 1) str.append(" Finally slaying " + opponent.name);
            else str.append("Bringing "  + opponent.name + " to " + opponent.getStat(VIGOR) + '/' + opponent.getStat(MAX_VIGOR) + " health.");
        }
        else
        {
            str.append("failing to deal noticeable damage.");
        }

        return str.toString();
    }
}
