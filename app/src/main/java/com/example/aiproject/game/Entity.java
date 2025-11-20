package com.example.aiproject.game;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Entity
{
    private static long _id;

    public final long id;
    public String name, description;

    private Entity(){id=_id++;}
}
