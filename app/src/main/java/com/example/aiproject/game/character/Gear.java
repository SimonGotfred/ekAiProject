package com.example.aiproject.game.character;

import com.example.aiproject.game.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import lombok.Getter;

import static com.example.aiproject.game.character.Stat.*;

@Getter
public class Gear // todo: separate "Action" from "Gear"
{
    protected static final StringBuilder text = new StringBuilder();
    protected static void  clearText () {text.setLength(0);}
    protected static void  newText   (String text, Object... objects) {clearText(); addText(text, objects);}
    protected static void  addText   (String text, Object... objects) {Gear.text.append(String.format(text, objects));}

    protected String         name;
    protected String         description;
    protected Stat           aptitude;
    protected List<Modifier> modifiers = new ArrayList<>();
    protected short          duration = 0;
    protected boolean        isLoot     = false; // todo: should maybe depend on whether having GOLD modifiers?

    public Gear(String name, Modifier... modifiers) {this(name,VIGOR);aptitude=null;}
    public Gear(String name, Stat aptitude, Modifier... modifiers)
    {
        this.name      = name;
        this.aptitude  = aptitude;
        this.modifiers.addAll(List.of(modifiers));
    }

    public String getDescription(){return description.isEmpty() ? name : description;}

    public boolean isPassive(){return !isAction();}
    public boolean isAction() {return aptitude != null;}
    public boolean isArmor()  {return !isAction() && modifiers.stream().anyMatch(modifier -> modifier.getStat().equals(DEFENCE));}
    public boolean isItem()   {return modifiers.stream().anyMatch(modifier -> modifier.getStat().equals(GOLD));}

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

        newText("using their %s, %s rolls %d against %s's %d defence, ", name, user.getName(), roll, subject.getName(), defence);

        if (roll >= defence)
        {
            int damage = user.getStat(aptitude);
            dice+= "<br>"+"Damage: " + damage + aptitude.icon;

            for (Modifier mod : modifiers.stream().filter(modifier ->
                                                          modifier.getStat().equals(MELEE)||
                                                          modifier.getStat().equals(RANGED)||
                                                          modifier.getStat().equals(MAGIC)).collect(Collectors.toList()))
            {damage += mod.roll();dice+= "+ " + mod.result(false);}

            subject.increase(FATIGUE, damage);

            dice+=" ≻ " + damage + FATIGUE.icon;
            addText("dealing %d damage and ",damage);

            if (subject.getStat(VIGOR) < 1) addText("finally slaying %s",subject.getName());
            else addText("bringing %s to %d/%d health.",subject.getName(),subject.getStat(VIGOR),subject.getStat(MAX_VIGOR));
        }
        else if (roll >= defence-subject.resolveBonus(DEFENCE))
        {
            addText("dealing a glancing blow to %s, but causing no significant damage.",subject.getName());
        }
        else
        {
            addText("failing to hit as %s evades the attack.",subject.getName());
        }

        turn.setOutcome(text.toString());
        turn.setDiceThrow(dice);

        return turn;
    }

    private Turn consume(Character user, Character subject)
    {
        Turn turn = new Turn(subject, this);
        newText("%s consumes a %s, ", user.getName(), name);

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
