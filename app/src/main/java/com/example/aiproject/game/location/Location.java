package com.example.aiproject.game.location;

import static com.example.aiproject.game.location.Direction.*;
import static com.example.aiproject.game.tool.Dice.*;

import com.example.aiproject.game.RandomSuite;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.tool.RollableList;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Optional;

@Getter @Setter
public class Location extends Gear
{
    public static Location current; // todo: better solution

    Location template;
    int minPaths, maxPaths;
    HashMap<Location,Direction> adjoined = new HashMap<>();
    RollableList<Character>     characters;
    RollableList<Gear>          loot;


    public Location(Location template)
    {
        super(template.name);
        this.template    = template;
        this.description = template.description;
        this.minPaths    = template.minPaths;
        this.maxPaths    = template.maxPaths;
    }

    public boolean hasGenerated(){return template==null;}
    public boolean needsPaths()  {return adjoined.size()< minPaths;}
    public boolean fullPaths ()  {return adjoined.size()>=maxPaths;}
    public int     hasPaths  ()  {return needsPaths() ? -1 : fullPaths() ? 1 : 0;}

    private void unpackTemplate()
    {
        if (template==null) return;
        if (template.loot      !=null) generateLoot      (template.loot);
        if (template.characters!=null) generateCharacters(template.characters);
        if (template.adjoined  !=null) generatePaths(new RollableList<>(template.adjoined.keySet()));
        template=null;
    }

    public void generateFull(RollableList<Location> locationTemplates, RollableList<Character> characterTemplates, RollableList<Gear> lootTable)
    {
        unpackTemplate();
        if (characters==null) {characters = new RollableList<>(); generateCharacters(characterTemplates);}
        if (loot      ==null) {loot       = new RollableList<>(); generateLoot(lootTable);}
        if (!fullPaths())     generatePaths(locationTemplates);
    }

    public RollableList<Location> generatePaths(RollableList<Location> possiblePaths)
    {
        RollableList<Location> list = new RollableList<>();
        RollableList<Direction> availableDirections = new RollableList<>(Direction.values());
        availableDirections.removeAll(adjoined.values());
        for (int i = RandomSuite.getInt(minPaths, maxPaths) - adjoined.size(); i > 0; i--)
        {
            if (availableDirections.isEmpty()) return list;
            list.add(this.addPath(new Location(possiblePaths.getRandom()),availableDirections.removeRandom()));
        }
        return list;
    }

    public Location addPath(Location location) {return addPath(location,new RollableList<>(Direction.values()).getRandom());}
    public Location addPath(Location location, Direction direction)
    {
        adjoined.put(location,direction);
        location.adjoined.put(this,direction.opposite());
        return location;
    }

    public RollableList<Character> generateCharacters(RollableList<Character> possibleChars) // todo: generate characters in location
    {
        characters.add(new Character(possibleChars.getRandom()));
        return characters;
    }

    public RollableList<Gear> generateLoot(RollableList<Gear> possibleGear) {return generateLoot(possibleGear,Math.abs(d4.roll(2)-4));}
    public RollableList<Gear> generateLoot(RollableList<Gear> possibleGear, int amount) // todo: generate loot in location
    {
        loot.addAll(possibleGear.getRandom(amount, true));
        return new RollableList<>();
    }

    public String toString()
    {
        return Optional.ofNullable(adjoined.getOrDefault(current, DOWNSTAIRS)).orElse(DOWNSTAIRS).opposite().name().toLowerCase() + " towards a " + name;
    }
}
