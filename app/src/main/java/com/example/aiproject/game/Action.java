package com.example.aiproject.game;

import com.example.aiproject.game.character.Character;

public interface Action
{
    default String use()              {return use(null);}
    default String use(Character user){return use(user,user);}
            String use(Character user, Character subject);
}
