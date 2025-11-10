package com.example.aiproject.game.character;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.example.aiproject.game.character.Stat.*;

@Getter @Setter
public class Character extends CharacterBase
{
    public CharacterBase base;

    public int fatigue;
    public List<Gear> equippedGear;

    public Character(CharacterBase characterBase)
    {
        this.base = characterBase;
        this.aptitudes = base.aptitudes.clone();
        this.gear = List.copyOf(base.gear);

        this.equippedGear = List.copyOf(gear);
        this.fatigue = 0;
    }

    public int maxVigor()
    {
        return super.maxVigor() + resolveBonus(VIGOR, ATHLETICS, WILLPOWER);
    }

    public int vigor() // vigor calculated like this, to apply maxVigor buffs immediately
    {
        return maxVigor() - this.fatigue();
    }

    public int fatigue()
    {
        return fatigue + resolveBonus(FATIGUE);
    }

    public int defence()
    {
        return super.defence() + resolveBonus(DEFENCE, ATHLETICS, INTELLIGENCE) - this.fatigue();
    }

    public int roll(Stat aptitude)
    {
        return super.roll(aptitude) + resolveBonus(aptitude) - this.fatigue();
    }

    public int resolveBonus(Stat... stats)
    {
        int bonus = 0;
        List<Stat> statList = List.of(stats);
        for (Gear gear : equippedGear)
        {
            for (Modifier mod : gear.modifiers)
            {
                if (statList.contains(mod.stat)) bonus += mod.value;
            }
        }
        return bonus;
    }
}
