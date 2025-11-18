package com.example.aiproject.game;

import com.example.aiproject.MainActivity;

import com.example.aiproject.ai.Response;
import com.example.aiproject.ai.AiService;

import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.character.Player;
import com.example.aiproject.game.character.Stat;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.location.Location;
import com.example.aiproject.game.tool.RollableList;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.example.aiproject.game.character.Stat.*;

public class GameEngine implements AiService
{
    private final boolean   useAi = false;
    private final MainActivity ui;

    private void  clearText(){text.setLength(0);}
    private void  newText(String text, Object... objects) {clearText(); addText(text, objects);}
    private void  addText(String text, Object... objects) {this.text.append(String.format(text,objects));}
    private final StringBuilder text = new StringBuilder();

    // following strings should *probably* be imported through resources
    //
    public  final static String paragraphAccent =
       "⊱⟢༻"
     + "                                         "
     + "༺⟣⊰"; // nothing allows me to justify these two elements to either side of the screen, not even WebView worked, I've wasted 3 hours!

    public  final static String introText =
       "<h6 style=\"text-align: center\"> <br>"+ paragraphAccent +"</h6>"
     + "<p>Hello and welcome adventurer, will you dare to delve into the dungeon?</p>"
     + "<h6 style=\"text-align: center\">"+ paragraphAccent +"</h6>"
     + "<p>"+ATHLETICS.icon   +"<b>Athletics:</b> Represents your strength, agility and overall physical health.</p>"
     + "<p>"+INTELLIGENCE.icon+"<b>Intelligence:</b> Represents your insight, alertness and how knowledgeable you are of the world.</p>"
     + "<p>"+WILLPOWER.icon   +"<b>Willpower:</b> Represents your resolve, discipline and your ability to assert yourself.</p>"
     + "<p>"+MAX_VIGOR.icon   +"<b>Vigor:</b> How far you are from dying is a combination of your <i>Athletics</i> and <i>Willpower</i>.</p>"
     + "<p>"+DEFENCE.icon     +"<b>Defence:</b> How difficult you are to hit is a combination of your <i>Athletics</i> and <i>Intelligence</i>.</p>"
     + "<p>"
     + "<b>Weapons:</b> Your ability to wield a weapon depends on the weapons type."
     + "<br>"+MELEE.icon +"<i>Melee</i> depends on your <i>Athletics</i>."
     + "<br>"+RANGED.icon+"<i>Ranged</i> depends on your <i>Intelligence</i>."
     + "<br>"+MAGIC.icon +"<i>Magic</i> depends on your <i>Willpower</i>."
     + "</p>";

    public  final static String promptInstructions =
            "Dramatically describe how the following scene plays out in 2-4 sentences using present tense, " +
            "always refer to the player as 'you', and do NOT mention specific numbers or stats.\n";

    public  final static String scenePrompt = "Feel free to involve any supposed elements from the environment.\n";
    public  final static String sceneDescription = "The scene takes place in a %s with %d exits, it involves the %s%s. %s";


    // initialized in constructor in order to set 'ui' first, otherwise would be made static
    private final Option RESTART;
    private final Option PROCEED;
    private final Option LOOTING;
    private final Location  EXIT;

    // list of completed combat-rounds, logged for... *prosperity*
    private final LinkedHashMap<Turn,Turn> rounds = new LinkedHashMap<>();
    private       Entry<Turn,Turn>         lastRound;

    // templates and base game elements, to be referenced by or copied into an active game.
    private final RollableList<Gear>      lootTable         = new RollableList<>();
    private final RollableList<Location>  locationTemplates = new RollableList<>();
    private final RollableList<Character> charTemplates     = new RollableList<>();
    private final Character               newPlayerTemplate;

    // game elements present in currently active game
    private       Location  location;
    private       Character adversary;
    private       Player    player;

    public GameEngine(MainActivity mainActivity, InputStream charResource, InputStream locationResource)
    {
        ui = mainActivity;

        RESTART = buildOption("Restart"); // initialized in constructor to set 'ui' first.
        PROCEED = buildOption("Proceed");
        LOOTING = buildOption("Loot");

        loadLocations(locationResource);
        loadCharacters(charResource);

        newPlayerTemplate = charTemplates.remove(0);         // separate player template from other characters.
        lootTable.addAll(charTemplates.remove(0).getGear()); // extract loot-table, and discard Loot-Bug template.
        EXIT = locationTemplates.remove(0);                  // separate special 'static' location *EXIT* to only put at the start of each newly generated dungeon
        EXIT.setAdjoined(new HashMap<>());
        Location.current = location = EXIT;

        print(introText);
    }

    public void start() {submitOption(RESTART);}

    private void loadCharacters(InputStream stream) {charTemplates.addAll(new Gson().fromJson(new InputStreamReader(stream), Character[].class));}
    private void loadLocations(InputStream stream)  {locationTemplates.addAll(new Gson().fromJson(new InputStreamReader(stream), Location[].class));}
    private void setupNewGame()
    {;
        player    = newPlayer();
        resolveTravel(newLocation(EXIT));

        describeScene();
        addText("The %s enters the dungeon and encounters %s, bent on fighting %s.",player.getName(), adversary.getName(),player.getName());
    }

    public List<Option> listOptions()
    {
        List<Option> options = new ArrayList<>();
        if       (player   .isDead()) options.add(RESTART); // main priority - the option when game is lost
        else if  (adversary.isDead())
        {
            if (!adversary.hasStat(DEFENCE))
            {
                options.add(LOOTING);
                LOOTING.setText("Loot " + adversary.getName());
            }
            for (Location location : location.getAdjoined().keySet()) options.add(buildOption(location));
        }
        else for (Gear gear : player.getActions()) options.add(buildOption(gear)); // options for being in combat
        return options;
    }

    public void submitOption(Option option) // main driving 'switch' running the game
    {                                       // not actually a switch - because old systems don't support *switch(Object)*
        try /// note: that it is possible to have non-API options by returning from function before 'prompt()' - just remember to print!
        {
            clearText();
            if (!option.equals(RESTART)) describeScene();
            lastRound = null; // todo: so far 'rounds' only refer to combat, thus toss last round - it'll be set again when applicable
            if      (option.equals(RESTART)) setupNewGame();
            else if (location.getAdjoined() .containsKey(option.gear)) resolveTravel((Location) option.gear);
            else if (option.equals(LOOTING)) resolveLoot(adversary);
            else     resolveCombat(option.gear); // default assumption for option is "combat", as the variety of combat-options is expansive
            if (useAi) prompt(); // prompt the ai service for a dramatization when actions have been resolved
            else onServiceResponse(new Response(text.replace(text.indexOf(scenePrompt),text.indexOf(scenePrompt)+scenePrompt.length(),"<br>").toString()));
        }
        catch (Throwable e) {recover(e);} // catch anything the game logic f**ks up
    }

    private Turn adversaryAction(Character adversary)
    {
        if (adversary == null || adversary.isDead()) return null;
        return adversary.act(player);
    }

    private void updateRounds(Turn playerTurn) {updateRounds(playerTurn, null);}
    private void updateRounds(Turn playerTurn, Turn adversaryTurn)
    {
        rounds.put(playerTurn, adversaryTurn);
        rounds.entrySet().iterator().forEachRemaining(round -> lastRound = round); // 'getLast()' don't exist on old systems
    }

    public void resolveTravel(Location location)
    {
        Location.current = this.location = location;
        location.generateFull(locationTemplates,charTemplates,lootTable);
        adversary = newAdversary(); // todo
        describeScene();
        addText("%s steps into the %s, encountering %s, bent on fighting %s.",
                player.getName(), location.getName(), adversary.getName(), player.getName());
    }

    public void resolveCombat(Gear gear)
    {
        updateRounds(gear.use(player, adversary), adversaryAction(adversary));

        addText(lastRound.getKey().getOutcome());
        if (adversary.isAlive()) addText("\nMeanwhile " + lastRound.getValue().getOutcome());
    }

    public void resolveLoot(Character corpse)
    {
        List<Gear> loot = corpse.getGear().stream().filter(Gear::isLoot).collect(Collectors.toList());
        corpse.getGear().removeAll(loot);
        player.getGear().addAll(loot); // todo: selectable loot

        // add *vigor* stat to corpse to flag as completely looted,
        // it is a stat that is unimaginable to have inherently on a character otherwise,
        // since such desired effect should be done using *MAX_VIGOR*
        corpse.decrease(DEFENCE);

        addText("%s searches the slain body of %s, finding",player.getName(),adversary.getName());

        if (loot.isEmpty()) {addText(" nothing of value.");return;}
        for (Gear gear:loot) addText(" a %s,",gear.getName());
        if (loot.size() > 1) text.insert(text.lastIndexOf(", a ")+1," and")
                                 .deleteCharAt(text.lastIndexOf(","));
        text.deleteCharAt(text.length()-1).append('.');
    }

    public String describeScene()
    {
        newText(sceneDescription, location.getDescription(),
                location.getAdjoined().size(), player.toString(),
                " and a "+adversary.toString(),scenePrompt);
        return text.toString();
    }

    private Player    newPlayer() {player = new Player(newPlayerTemplate); return player;}
    private Location  newLocation(Location path)
    {
        Location location = new Location(new RollableList<>(locationTemplates.stream().filter(template -> template.getMinPaths() > 1).collect(Collectors.toList())).getRandom());
        location.addPath(path);
        return location;
    }

    private Character newAdversary() // todo: deprecate
    {
        Character adversary = location.getCharacters().getRandom();
        adversary.add(lootTable.getRandom());
        return adversary;
    }

    public String result(){return resultOf(lastRound);}
    public String resultOf(Turn turn){return turn.result();}
    public String resultOf(Entry<Turn,Turn> round)
    {
        if (round == null) return "";
        String text = round.getKey().result();
        if (round.getValue()!=null) text += "</p><p>" + round.getValue().result();
        return text;
    }

    public String statBar(){return writeStats(GOLD, VIGOR, DEFENCE, ATHLETICS, INTELLIGENCE, WILLPOWER);}
    public String writeStats(Stat... stats)
    {
        text.setLength(0);
        for (Stat stat : stats) {text.append(player.writeStat(stat)).append("\t\t");}
        return text.toString().trim();
    }

        // AI Hooks \\
    private void prompt() {prompt(promptInstructions + text);}
    public  void onServiceResponse(Response response)
    {
        try
        {
            String s = useAi ? response.getText() : response.getResponseId();
            newText("<h6 style=\"text-align: center\">%s<br>%s</h6>"
                       + "<p>%s</p>"
                       + "<p>%s</p>", statBar(), paragraphAccent, s, result());
            print(text.toString());
            refreshOptions();
        }
        catch (Throwable e) {recover(e);} // catch anything the a̶i̶ my service f**ks up
    }

    public  void onServiceException(Throwable e)
    {
        try
        {
            String s = text.toString();
            newText("<h6 style=\"text-align: center\">%s<br>%s</h6>"
                    + "<p>%s</p>"
                    + "<p>%s</p>", statBar(), paragraphAccent, s, result());
            print(text.toString());
            refreshOptions();
        }
        catch (Throwable e2) {recover(e2);} // catch anything the a̶i̶ my service f**ks up
    }

    private void recover(Throwable e)
    {
        print(e.getLocalizedMessage());
        refreshOptions(RESTART);
    }

        //  UI Hooks  \\
    public void   print(String text)                {ui.newText (text);} // todo: figure out breaklines
    public void   clearOptions()                    {ui.clearButtons();}
    public void   refreshOptions()                  {ui.clearButtons(); ui.addMultipleBtn(listOptions());}
    public void   refreshOptions(Option... options) {ui.clearButtons(); ui.addMultipleBtn(List.of(options));}
    public Option buildOption(String option)        {return buildOption(new Gear(option));}
    public Option buildOption(Gear gear)
    {
        Option option = new Option(ui,gear);
        option.setOnClickListener(ui -> submitOption(option));
        return option;
    }
}
