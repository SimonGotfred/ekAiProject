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
    private static void  clearText () {text.setLength(0);}
    private static void  newText   (String text, Object... objects) {clearText(); addText(text, objects);}
    private static void  addText   (String text, Object... objects) {Gear.text.append(String.format(text, objects));}

    private String         name;
    private Stat           aptitude;
    private List<Modifier> modifiers = new ArrayList<>();
    private short          duration  = 0;
    private boolean        loot      = false;

    public Gear(String name, Modifier... modifiers) {this(name,VIGOR);aptitude=null;}
    public Gear(String name, Stat aptitude, Modifier... modifiers)
    {
        this.name      = name;
        this.aptitude  = aptitude;
        this.modifiers.addAll(List.of(modifiers));
    }

    public boolean isPassive(){return !isAction();}
    public boolean isAction() {return aptitude != null;}
    public boolean isArmor()  {return !isAction() && modifiers.stream().anyMatch(modifier -> modifier.getStat().equals(DEFENCE));}

    public Turn use(Character user) {return use(user, user);}
    public Turn use(Character user, Character subject)
    {
        if (!this.isAction()) return new Turn(user, this);
        switch (aptitude) // todo: switch function possibly better off as child classes (Gear->Actionable->Attack/Consumable)
        {
            case VIGOR:        return consume(user, user);
            case ATHLETICS:
            case INTELLIGENCE:
            case WILLPOWER:    return attack(user, subject);
            default:           return new Turn(user, this); // todo: default gear use
        }
    }

    private Turn attack(Character user, Character subject)
    {
        Turn turn   = new Turn(user, this);
        int defence = subject.getStat(DEFENCE);
        int roll    = user.roll(aptitude);
        String dice = user.result() + " ≻ " + roll + " vs " + defence + DEFENCE.icon;

        newText("Using their %s, %s rolls %d against %s's %d defence, ", name, user.name, roll, subject.name, defence);

        if (roll >= defence)
        {
            int damage = user.getStat(aptitude);
            dice+= "<br>"+"Damage: " + damage + aptitude.icon;

            for (Modifier mod : modifiers.stream().filter(modifier ->
                                                          modifier.getStat().equals(MELEE)||
                                                          modifier.getStat().equals(RANGED)||
                                                          modifier.getStat().equals(MAGIC)).collect(Collectors.toList()))
            {damage += mod.rollValue();dice+="+ "+mod.result(false);}

            subject.increase(FATIGUE, damage);

            dice+=" ≻ " + damage + FATIGUE.icon;
            addText("dealing %d damage and ",damage);

            if (subject.getStat(VIGOR) < 1) addText("finally slaying %s",subject.name);
            else addText("bringing %s to %d/%d health.",subject.name,subject.getStat(VIGOR),subject.getStat(MAX_VIGOR));
        }
        else if (roll >= defence-subject.resolveBonus(DEFENCE))
        {
            addText("dealing a glancing blow to %s, but causing no significant damage.",subject.name);
        }
        else
        {
            addText("failing to hit as %s evades the attack.",subject.name);
        }

        turn.setOutcome(text.toString());
        turn.setDiceThrow(dice);

        return turn;
    }

    private Turn consume(Character user, Character subject)
    {
        Turn turn = new Turn(subject, this);
        newText("%s consumes one %s, ", user.name, name);

        subject.increase(modifiers.toArray(new Modifier[]{}));
        user.remove(this);

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
