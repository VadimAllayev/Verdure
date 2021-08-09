import java.io.Serializable;

public class Artifact extends Item implements Serializable
{
    //ints for numerical buff, chars to specify which stat
    private int damage,mobility,life,mana;
    
    //if these artifacts buff or attack
    private int magnitude; //how much artifact does (ex. damage, healing, etc.)
    private long performTime; //keep track of when action was performed by artifact
    private int cooldown; //how long to wait before doing something again
    private int cost; //cost of projectile that spawns
    private String proj; //name of projectile (if one spawns from this artifact)
    
    public Artifact()
    {
        super();
        
        damage = 0;
        mobility = 0;
        life = 0;
        mana = 0;
        magnitude = 0;
        performTime = 0;
        cooldown = 0;
        cost = 0;
        proj = "";
    }
    
    public Artifact(String name)
    {
        super(name,true);
        
        damage = 0;
        mobility = 0;
        life = 0;
        mana = 0;
        magnitude = 0;
        performTime = 0;
        cooldown = 0;
        cost = 0;
        proj = "";
        
        createArtifact();
    }
    
    private void createArtifact()
    {
        String name = getName();
        if(name.equals("Doran's Blade"))
        {
            damage = +1;
            setDescription("Increases damage by 1.");
        }
        else if(name.equals("Doran's Shield"))
        {
            mana = +1;
            setDescription("Increases mana by 1.");
        }
        else if(name.equals("Dagger"))
        {
            damage = +1;
            setDescription("Increases damage by 1.");
        }
        else if(name.equals("Long Sword"))
        {
            damage = +1;
            setDescription("Increases damage by 1.");
        }
        else if(name.equals("Obsidian Cleaver"))
        {
            damage = +2;
            mobility = -1;
            setDescription("Increases power by 2. Reduces mobility by 1.");
        }
        else if(name.equals("Michelle's Cutlass"))
        {
            damage = +2;
            mana = -1;
            setDescription("Increases power by 2. Reduces mana by 1.");
        }
        else if(name.equals("Allen's Gladius"))
        {
            damage = +2;
            life = -1;
            setDescription("Increases power by 2. Reduces life by 1.");
        }
        else if(name.equals("Anahi's Ghostblade"))
        {
            damage = +3;
            life = -1;
            mana = -1;
            setDescription("Increases power by 3. Reduces life and mana by 1.");
        }
        else if(name.equals("Justin's Zeal"))
        {
            damage = +2;
            mobility = +2;
            life = -1;
            mana = -1;
            setDescription("Increases power and mobility by 2. Reduces life and mana by 1.");
        }
        else if(name.equals("Trinity Force"))
        {
            damage = +1;
            life = +1;
            mana = +1;
            setDescription("Increases power, life, and mana by 1.");
        }
        else if(name.equals("Blade of Jacob"))
        {
            damage = +2;
            setDescription("Increases power by 2.");
        }
        else if(name.equals("Boots of Speed"))
        {
            mobility = +1;
            setDescription("Increases mobility by 1.");
        }
        else if(name.equals("Boots of Mobility"))
        {
            mobility = +2;
            setDescription("Increases mobility by 2.");
        }
        else if(name.equals("Berserker's Greaves"))
        {
            mobility = +2;
            damage = +1;
            setDescription("Increases mobility by 2. Increases power by 1.");
        }
        else if(name.equals("Sorcerer's Shoes"))
        {
            mobility = +2;
            mana = +1;
            setDescription("Increases mobility by 2. Increases mana by 1.");
        }
        else if(name.equals("Boots of Swiftness"))
        {
            mobility = +3;
            setDescription("Increases mobility by 3.");
        }
        else if(name.equals("Ruby Crystal"))
        {
            life = +1;
            setDescription("Increases life by 1.");
        }
        else if(name.equals("Sapphire Crystal"))
        {
            mana = +1;
            setDescription("Increases mana by 1.");
        }
        else if(name.equals("Rachel's Charm"))
        {
            life = +1;
            mana = +1;
            setDescription("Increases life and mana by 1.");
        }
        else if(name.equals("Cloth Armor"))
        {
            life = +2;
            setDescription("Increases life by 2.");
        }
        else if(name.equals("Cloak of Agility"))
        {
            life = +1;
            mobility = +1;
            setDescription("Increases life and mobility by 1.");
        }
        else if(name.equals("Chain Vest"))
        {
            life = +3;
            setDescription("Increases life by 3.");
        }
        else if(name.equals("Melanie's Spectre Cowl"))
        {
            life = +1;
            mana = +2;
            setDescription("Increases mana by 2 and life by 1.");
        }
        else if(name.equals("Sigalita's Sunfire Cape"))
        {
            mana = +2;
            mobility = +2;
            setDescription("Increases mana and mobility by 2.");
        }
        else if(name.equals("Righteous Glory"))
        {
            life = +2;
            mobility = +1;
            setDescription("Increases life by 2 and mobility by 1.");
        }
        else if(name.equals("Joshua's Riotmail"))
        {
            life = +3;
            damage = +1;
            mobility = -1;
            setDescription("Increases life by 3, increases power by 1, reduces mobility by 1.");
        }
        else if(name.equals("Irwin's Deathcap"))
        {
            mana = +3;
            damage = +1;
            mobility = -1;
            setDescription("Increases mana by 3, increases power by 1, reduces mobility by 1.");
        }
        else if(name.equals("Arnav's Spirit"))
        {
            life = +4;
            setDescription("Increases life by 4.");
        }
        else if(name.equals("Alex's Guardian Orb"))
        {
            mana = +4;
            setDescription("Increases mana by 4.");
        }
        else if(name.equals("Catalyst of Aeons")) {
            mana = +3;
            life = +1;
            setDescription("Increases mana by 3 and life by 1.");
        }
        else if(name.equals("Aegis of the Legion")) {
            life = +3;
            mana = +1;
            setDescription("Increases life by 3 and mana by 1.");
        }
        else if(name.equals("Locket of a Fallen King")) {
            life = +4;
            mana = +2;
            setDescription("A locket worn by the former King. Increases life by 4 and mana by 2.");
        }
        else if(name.equals("Aether Wisp"))
        {
            setDescription("Grants Soul Seeker ability. Deals 1 damage. Expends 1 mana.");
            cost = 1;
            proj = "wisp";
        }
        else if(name.equals("Essence of Fire"))
        {
            setDescription("Grants Engulfing Flame ability. Deals 3 damage. Expends 1 mana.");
            cost = 1;
            proj = "flare";
        }
        else if(name.equals("Essence of Water"))
        {
            setDescription("Grants Persistent Geyser ability. Deals 2 damage. Expends 1 mana.");
            cost = 1;
            proj = "geyser";
        }
        else if(name.equals("Essence of Nature"))
        {
            setDescription("Grants Tricky Vine ability. Deals 2 damage. Expends 1 mana.");
            cost = 1;
            proj = "vine";
        }
        else if(name.equals("Ben's Clay Launcher"))
        {
            setDescription("Grants Clay Shuriken ability. Deals 1 damage. Expends 1 mana.");
            cost = 1;
            proj = "clayWheel";
        }
        else if(name.equals("Abe's Shield of Slime"))
        {
            setDescription("Grants Slime Ring ability. Deals 2 damage. Expends 1 mana.");
            cost = 1;
            proj = "acidSplat";
        }
        else if(name.equals("Leon's Wand of Blasting"))
        {
            setDescription("Grants Launch Fireball ability. Deals 3 damage. Expends 1 mana.");
            cost = 1;
            proj = "fireball";
        }
        else if(name.equals("Flamberge of Destruction"))
        {
            setDescription("Grants Invoke Explosion ability. Deals 5 damage. Expends 2 mana.");
            cost = 2;
            proj = "explosion";
        }
        else if(name.equals("Anisara's Glacial Shroud"))
        {
            setDescription("Grants Ice Breaker ability. Deals 4 damage. Expends 2 mana.");
            cost = 2;
            proj = "ice";
        }
        else if(name.equals("Celestial Staff"))
        {
            setDescription("Grants Shooting Star ability. Deals 1 damage. Expends no mana.");
            cost = 0;
            proj = "star";
        }
    }

    public int getDamage() {
        return damage;
    }

    public int getMobility() {
        return mobility;
    }

    public int getLife() {
        return life;
    }

    public int getMana() {
        return mana;
    }
    
    public int getCost() {
        return cost;
    }
    
    public String getProj() {
        return proj;
    }
}