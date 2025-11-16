package com.example.aiproject.game.character;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import static com.example.aiproject.game.character.Stat.*;

@Getter
public class Gear
{
    private static final StringBuilder strBuilder = new StringBuilder();
    private static void   clearText() {strBuilder.setLength(0);}
    private static String getText  () {return strBuilder.toString();}
    private static String startText(String text, Object... objects) {clearText(); return addText(text,objects);}
    private static String addText  (String text, Object... objects) {return strBuilder.append(String.format(text,objects)).toString();}

    private String         name;
    private Stat           aptitude;
    private List<Modifier> modifiers = new ArrayList<>();
    private short          duration  = 0;
    private boolean        loot      = false;

    public Gear(String name, Modifier... modifiers)
    {
        this.name = name;
        this.modifiers = List.of(modifiers);
        duration       = -1;
    }

    public boolean isAction() {return aptitude != null;}
    public boolean isArmor()  {return !isAction() && modifiers.stream().anyMatch(modifier -> modifier.getStat().equals(DEFENCE));}

    public String use(Character user) {return use(user, user);}
    public String use(Character user, Character opponent)
    {
        if (!this.isAction()) return startText("%s checks their %s.", user.name, name);
        switch (aptitude) // todo: switch function possibly better off as child classes (Gear->Actionable->Attack/Consumable)
        {
            case VIGOR:        return consume(user);
            case ATHLETICS:
            case INTELLIGENCE:
            case WILLPOWER:    return attack(user, opponent);
            default:           return ""; // todo: default gear use
        }
    }

    private String attack(Character user, Character opponent)
    {
        int roll = user.roll(aptitude);
        int defence = opponent.getStat(DEFENCE);

        startText("Using their %s, %s rolls %d against %s's %d defence, ", name, user.name, roll, opponent.name, defence);

        if (roll >= defence)
        {
            int damage = user.getStat(aptitude);

            for (Modifier mod : modifiers.stream().filter(modifier ->
                                                          modifier.getStat().equals(MELEE)||
                                                          modifier.getStat().equals(RANGED)||
                                                          modifier.getStat().equals(MAGIC)).collect(Collectors.toList()))
            {damage += mod.rollValue();}

            opponent.increase(FATIGUE, damage);

            addText("dealing %d damage and ",damage);

            if (opponent.getStat(VIGOR) < 1) addText("finally slaying %s",opponent.name);
            else addText("bringing %s to %d/%d health.",opponent.name,opponent.getStat(VIGOR),opponent.getStat(MAX_VIGOR));
        }
        else if (roll >= defence-opponent.resolveBonus(DEFENCE))
        {
            addText("dealing a glancing blow to %s, but causing no significant damage.",opponent.name);
        }
        else
        {
            addText("failing to hit as %s evades the attack.",opponent.name);
        }

        return getText();
    }

    private String consume(Character user)
    {
        startText("%s consumes one %s, ",user.name,name);

        user.increase(modifiers.toArray(new Modifier[]{}));

        return getText();
    }
}
