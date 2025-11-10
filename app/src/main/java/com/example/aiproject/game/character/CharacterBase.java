package com.example.aiproject.game.character;

import com.example.aiproject.game.Dice;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.example.aiproject.game.character.Stat.*;

@Getter @Setter
public class CharacterBase
{
    String description = "";
    List<Gear>   gear = new ArrayList<>();
    List<String> limbs = new ArrayList<>();
    EnumMap<Stat, Integer> aptitudes = new EnumMap<>(Stat.class);

    public int defence()
    {
        return aptitudes.get(ATHLETICS) + aptitudes.get(INTELLIGENCE);
    }

    public int maxVigor()
    {
        return aptitudes.get(ATHLETICS) + aptitudes.get(WILLPOWER);
    }

    public int roll(Stat aptitude)
    {
        return aptitudes.get(aptitude) + Dice.d4.roll(2);
    }

    public int use(Gear gear)
    {
        return gear.useBy(this);
    }
}
