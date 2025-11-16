package com.example.aiproject.game.character;

import com.example.aiproject.game.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import lombok.Getter;

import static com.example.aiproject.game.character.Stat.*;

@Getter
public class Gear
{
    private static final StringBuilder text = new StringBuilder();
    private static void   clearText() {text.setLength(0);}
    private static String newText  (String text, Object... objects) {clearText(); return addText(text, objects);}
    private static String addText  (String text, Object... objects) {return Gear.text.append(String.format(text, objects)).toString();}

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

    public Turn use(Character user) {return use(user, user);}
    public Turn use(Character user, Character opponent)
    {
        if (!this.isAction()) return new Turn(user, this);
        switch (aptitude) // todo: switch function possibly better off as child classes (Gear->Actionable->Attack/Consumable)
        {
            case VIGOR:        return consume(user);
            case ATHLETICS:
            case INTELLIGENCE:
            case WILLPOWER:    return attack(user, opponent);
            default:           return new Turn(user, this); // todo: default gear use
        }
    }

    private Turn attack(Character user, Character opponent)
    {
        Turn turn   = new Turn(user, this);
        int defence = opponent.getStat(DEFENCE);
        int roll    = user.roll(aptitude);
        String dice = user.result() + " vs " + defence + DEFENCE.icon;

        newText("Using their %s, %s rolls %d against %s's %d defence, ", name, user.name, roll, opponent.name, defence);

        if (roll >= defence)
        {
            int damage = user.getStat(aptitude);
            dice+= "</p><p>"+"Damage: " + damage + aptitude.icon;

            for (Modifier mod : modifiers.stream().filter(modifier ->
                                                          modifier.getStat().equals(MELEE)||
                                                          modifier.getStat().equals(RANGED)||
                                                          modifier.getStat().equals(MAGIC)).collect(Collectors.toList()))
            {damage += mod.rollValue();dice+="+ "+mod.result(false);}

            opponent.increase(FATIGUE, damage);

            dice+=" ≻ " + damage + FATIGUE.icon;
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

        turn.setOutcome(text.toString());
        turn.setDiceThrow(dice);

        return turn;
    }

    private Turn consume(Character user) // todo: consumable gear functionality
    {
        Turn turn = new Turn(user, this);
        newText("%s consumes one %s, ", user.name, name);

        user.increase(modifiers.toArray(new Modifier[]{}));

        turn.setOutcome(text.toString());
        turn.setDiceThrow(result());

        return turn;
    }

    private String result()
    {
        String text = "";
        for (Modifier modifier : modifiers) text+=(modifier.result(false));
        return text.trim();
    }

    @NonNull
    public String toString()
    {
        newText(name);
        for (Modifier modifier : modifiers) addText(' ' + modifier.toString());
        return text.toString();
    }
}
