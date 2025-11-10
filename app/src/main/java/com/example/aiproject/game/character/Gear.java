package com.example.aiproject.game.character;

import java.util.List;

public class Gear
{
    List<Modifier> modifiers;
    Stat aptitude;

    public int useBy(CharacterBase character)
    {
        return character.roll(aptitude);
    }
}
