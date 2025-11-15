package com.example.aiproject.game;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class RandomSuite
{
    private static final Random rand = new Random();

    public static void setSeed(Object seed) {rand.setSeed(seed.hashCode());}
    public static void resetSeed()
    {
        long o = ((long)new Object().hashCode()) << 32 | new Object().hashCode();
        long seed = System.nanoTime() ^ o;
        for (int i = 0; i < 3; i++)
        {
            seed ^= seed >>> getInt(4,32);
            seed ^= seed <<  getInt(4,32);
        }
        rand.setSeed(seed);
    }

    public static void ascii() // print ASCII table to console, to sort out localization
    {
        for (int i = 26; i < 256; i++)
        {
            System.out.print(i + "\t" + (char)i + " | ");
            if ((i-25) % 10 == 0) System.out.println();
        }
    }

    public static int     getInt   ()                       {return rand.nextInt();}
    public static int     getInt   (int bound)              {return rand.nextInt(bound);}
    public static int     getInt   (int min, int max)       {return min+rand.nextInt(max-min);}
    public static double  getDouble()                       {return rand.nextDouble();}
    public static double  getDouble(double bound)           {return rand.nextDouble()*bound;}
    public static double  getDouble(double min, double max) {return min+getDouble(max-min);}
    public static boolean chance   (double percent)         {return percent > rand.nextDouble()*100;}

    public static <T> T oneOf( T...   objects) {return oneOf(List.of(objects));}
    public static <T> T oneOf(List<T> objects)
    {
        if (objects.isEmpty()) return null;
        return objects.get(rand.nextInt(objects.size()));
    }

    // returns and removes a random name from stored 'list'
    public static String getName()
    {
        if (nameList.isEmpty()) {resetNames();}
        return nameList.remove(rand.nextInt(nameList.size()));
    }

    // loads original list of names into 'list'.
    private static void resetNames()
    {
        nameList.clear();
        nameList.addAll(Arrays.asList(nameSrc));
        addLastNames();
    }

    // adds a random last name initial to each name stored in 'list'
    // also duplicates random first names, while ensuring different last names.
    private static void addLastNames()
    {
        ArrayList<String> newNames = new ArrayList<>();
        StringBuilder last = new StringBuilder();
        String a;

        for (String name : nameList)
        {
            for (int i = 0; i < 5; i++)
            {
                a = "" + (char)('A' + rand.nextInt(12) + rand.nextInt(12));
                if (!last.toString().contains(a))
                {
                    last.append(a);
                }
            }

            for (char b : last.toString().toCharArray())
            {
                newNames.add(name + " " + b);
            }
        }

        nameList.clear();
        nameList.addAll(newNames);
    }

    private static final ArrayList<String> nameList = new ArrayList<>();

    private static final String[] nameSrc = new String[]{
            "Aaron",
            "Abbey",
            "Abbie",
            "Abby",
            "Abdul",
            "Abe",
            "Abel",
            "Abigail",
            "Abraham",
            "Abram",
            "Ada",
            "Adah",
            "Adalberto",
            "Adaline",
            "Adam",
            "Adan",
            "Addie",
            "Adela",
            "Adelaida",
            "Adelaide",
            "Adele",
            "Adelia",
            "Adelina",
            "Adeline",
            "Adell",
            "Adella",
            "Adelle",
            "Adena",
            "Adina",
            "Adolfo",
            "Adolph",
            "Adria",
            "Adrian",
            "Adriana",
            "Adriane",
            "Adrianna",
            "Adrianne",
            "Adrien",
            "Adriene",
            "Adrienne",
            "Afton",
            "Agatha",
            "Agnes",
            "Agnus",
            "Agripina",
            "Agueda",
            "Agustin",
            "Agustina",
            "Ahmad",
            "Ahmed",
            "Ai",
            "Aida",
            "Aide",
            "Aiko",
            "Aileen",
            "Ailene",
            "Aimee",
            "Aisha",
            "Aja",
            "Akiko",
            "Akilah",
            "Al",
            "Alaina",
            "Alaine",
            "Alan",
            "Alana",
            "Alane",
            "Alanna",
            "Alayna",
            "Alba",
            "Albert",
            "Alberta",
            "Albertha",
            "Albertina",
            "Albertine",
            "Alberto",
            "Albina",
            "Alda",
            "Alden",
            "Aldo",
            "Alease",
            "Alec",
            "Alecia",
            "Aleen",
            "Aleida",
            "Aleisha",
            "Alejandra",
            "Alejandrina",
            "Alejandro",
            "Aleka",
            "Alena",
            "Alene",
            "Alesha",
            "Aleshia",
            "Alesia",
            "Alessandra",
            "Aleta",
            "Aletha",
            "Alethea",
            "Alethia",
            "Alex",
            "Alexa",
            "Alexander",
            "Alexandra",
            "Alexandria",
            "Alexia",
            "Alexis",
            "Alfonso",
            "Alfonzo",
            "Alfred",
            "Alfreda",
            "Alfredia",
            "Alfredo",
            "Ali",
            "Alia",
            "Alica",
            "Alice",
            "Alicia",
            "Alida",
            "Alina",
            "Aline",
            "Alisa",
            "Alise",
            "Alisha",
            "Alishia",
            "Alisia",
            "Alison",
            "Alissa",
            "Alita",
            "Alix",
            "Aliza",
            "Alla",
            "Allan",
            "Alleen",
            "Allegra",
            "Allen",
            "Allena",
            "Allene",
            "Allie",
            "Alline",
            "Allison",
            "Allyn",
            "Allyson",
            "Alma",
            "Almeda",
            "Almeta",
            "Alona",
            "Alonso",
            "Alonzo",
            "Alpha",
            "Alphonse",
            "Alphonso",
            "Alta",
            "Altagracia",
            "Altha",
            "Althea",
            "Alton",
            "Alva",
            "Alvaro",
            "Alvera",
            "Alverta",
            "Alvin",
            "Alvina",
            "Alyce",
            "Alycia",
            "Alysa",
            "Alyse",
            "Alysha",
            "Alysia",
            "Alyson",
            "Alyssa",
            "Amada",
            "Amado",
            "Amal",
            "Amalia",
            "Amanda",
            "Amber",
            "Amberly",
            "Ambrose",
            "Amee",
            "Amelia",
            "America",
            "Ami",
            "Amie",
            "Amiee",
            "Amina",
            "Amira",
            "Ammie",
            "Amos",
            "Amparo",
            "Amy",
            "An",
            "Ana",
            "Anabel",
            "Analisa",
            "Anamaria",
            "Anastacia",
            "Anastasia",
            "Andera",
            "Anderson",
            "Andra",
            "Andre",
            "Andrea",
            "Andreas",
            "Andree",
            "Andres",
            "Andrew",
            "Andria",
            "Andy",
            "Anette",
            "Angel",
            "Angela",
            "Angele",
            "Angelena",
            "Angeles",
            "Angelia",
            "Angelic",
            "Angelica",
            "Angelika",
            "Angelina",
            "Angeline",
            "Angelique",
            "Angelita",
            "Angella",
            "Angelo",
            "Angelyn",
            "Angie",
            "Angila",
            "Angla",
            "Angle",
            "Anglea",
            "Anh",
            "Anibal",
            "Anika",
            "Anisa",
            "Anisha",
            "Anissa",
            "Anita",
            "Anitra",
            "Anja",
            "Anjanette",
            "Anjelica",
            "Ann",
            "Anna",
            "Annabel",
            "Annabell",
            "Annabelle",
            "Annalee",
            "Annalisa",
            "Annamae",
            "Annamaria",
            "Annamarie",
            "Anne",
            "Anneliese",
            "Annelle",
            "Annemarie",
            "Annett",
            "Annetta",
            "Annette",
            "Annice",
            "Annie",
            "Annika",
            "Annis",
            "Annita",
            "Annmarie",
            "Anthony",
            "Antione",
            "Antionette",
            "Antoine",
            "Antoinette",
            "Anton",
            "Antone",
            "Antonetta",
            "Antonette",
            "Antonia",
            "Antonietta",
            "Antonina",
            "Antonio",
            "Antony",
            "Antwan",
            "Anya",
            "Apolonia",
            "April",
            "Apryl",
            "Ara",
            "Araceli",
            "Aracelis",
            "Aracely",
            "Arcelia",
            "Archie",
            "Ardath",
            "Ardelia",
            "Ardell",
            "Ardella",
            "Ardelle",
            "Arden",
            "Ardis",
            "Ardith",
            "Aretha",
            "Argelia",
            "Argentina",
            "Ariana",
            "Ariane",
            "Arianna",
            "Arianne",
            "Arica",
            "Arie",
            "Ariel",
            "Arielle",
            "Arla",
            "Arlean",
            "Arleen",
            "Arlen",
            "Arlena",
            "Arlene",
            "Arletha",
            "Arletta",
            "Arlette",
            "Arlie",
            "Arlinda",
            "Arline",
            "Arlyne",
            "Armand",
            "Armanda",
            "Armandina",
            "Armando",
            "Armida",
            "Arminda",
            "Arnetta",
            "Arnette",
            "Arnita",
            "Arnold",
            "Arnoldo",
            "Arnulfo",
            "Aron",
            "Arron",
            "Art",
            "Arthur",
            "Artie",
            "Arturo",
            "Arvilla",
            "Asa",
            "Asha",
            "Ashanti",
            "Ashely",
            "Ashlea",
            "Ashlee",
            "Ashleigh",
            "Ashley",
            "Ashli",
            "Ashlie",
            "Ashly",
            "Ashlyn",
            "Ashton",
            "Asia",
            "Asley",
            "Assunta",
            "Astrid",
            "Asuncion",
            "Athena",
            "Aubrey",
            "Audie",
            "Audra",
            "Audrea",
            "Audrey",
            "Audria",
            "Audrie",
            "Audry",
            "August",
            "Augusta",
            "Augustina",
            "Augustine",
            "Augustus",
            "Aundrea",
            "Aura",
            "Aurea",
            "Aurelia",
            "Aurelio",
            "Aurora",
            "Aurore",
            "Austin",
            "Autumn",
            "Ava",
            "Avelina",
            "Avery",
            "Avis",
            "Avril",
            "Awilda",
            "Ayako",
            "Ayana",
            "Ayanna",
            "Ayesha",
            "Azalee",
            "Azucena",
            "Azzie"};
}
