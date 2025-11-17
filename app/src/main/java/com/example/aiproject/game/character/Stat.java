package com.example.aiproject.game.character;

public enum Stat
{
        // base aptitudes:
    ATHLETICS   ("💪"),
    INTELLIGENCE("👁️"),
    WILLPOWER   ("🧠"),

        // derived stats:
    VIGOR    ("💖"),
    MAX_VIGOR("❤️"),
    FATIGUE  ("💔"),
    DEFENCE  ("🛡️"),

        // damage types:
    MELEE    ("🗡️"),
    RANGED   ("🏹"),
    MAGIC    ("✨"),

    GOLD     ("💰");

    public final String icon;

    Stat(String icon){this.icon=icon;}
}
