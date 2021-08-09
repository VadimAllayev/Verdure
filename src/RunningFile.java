import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Font;
//adding images
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
//adding music
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RunningFile extends JComponent implements KeyListener, MouseListener
{
    private int WIDTH,HEIGHT;
    private int backgroundX,backgroundY;
    private long currentTime,startTime,skipTime,campfireTime;
    private int skips;
    private final int WRITEX, WRITEY;
    private int step, story; //plot
    private String progress; //plot
    private boolean cutscene, writeMapName, firstSpell, twoPlayers;
    private Hero hero;
    private Enemy friend;
    private ArrayList<Item> drops;
    private ArrayList<Enemy> enemies;
    private GameAudio audio;
    private Inventory inventory;
    private Map map;
    private String heroFace, rivalFace;
    
    private Text text;
    
    private boolean friendAttacking; //new - for convenience
    private boolean bossFight;
    private boolean start, end;
    private boolean[] aliveBosses, discovered;
    private boolean defaultControls; //true--> OP, false--> JK
    private boolean foundEasterEgg,beatTheGame,muted,displayP2Level;
    private Storage storage;
    private int campX;
    
    //Default Constructor
    public RunningFile()
    {
        //initializing instance variables
        map = new Map("Menu"); //"Menu" initially
        WIDTH = 1200;
        HEIGHT = 675;
        backgroundX = 0;
        backgroundY = 0;
        currentTime = 0;
        skipTime = 0; //to skip cutscenes
        campfireTime = 0;
        skips = 0; //to skip cutscenes
        startTime = System.currentTimeMillis();
        step = 0; //0 initially
        story = 0; //0 initially
        progress = "Tutorial"; //"Tutorial" initially
        cutscene = false; //inital value irrelavent 
        bossFight = true; //intiially true to not let people go too far
        writeMapName = false;
        firstSpell = true;
        foundEasterEgg = false;
        beatTheGame = false;
        twoPlayers = false;
        displayP2Level = false; //start on indicator
        friendAttacking = false;
        
        //audio
        start = true;
        end = false;
        audio = new GameAudio(map.getSong());
        muted = false;
        
        hero = new Knight();
        enemies = new ArrayList<Enemy>();
        spawnRival(2250,false); //initial addition
        friend = new Enemy(); //player 2
        
        aliveBosses = new boolean[3];
        aliveBosses[0] = true; //moss dragon in deep forest
        aliveBosses[1] = true; //king in the castle
        aliveBosses[2] = true; //sandworm in deep cave (postgame)
        
        discovered = new boolean[10]; //cave is at index 8, deep cave at index 9
        discovered[1] = true; //forest
        
        //spawnEnemies(true);
        
        inventory = new Inventory(WIDTH, HEIGHT);
        
        //text
        WRITEX = 180; //when someone speaks, location x
        WRITEY = 20; //when someone speaks, location y
        text = new Text(WRITEX, WRITEY);
        
        drops = new ArrayList<Item>();
        defaultControls = true;
        
        try {
            //The destination to save the saved object file to
            FileInputStream file = new FileInputStream("Storage.obj");
            //ObjectInputStream is responisible for writing the byte file
            ObjectInputStream inputStream = new ObjectInputStream(file);
            //Trying to read the object
            storage = (Storage) inputStream.readObject();
        } 
        catch (Exception e) {
            storage = new Storage();
        }
        
        //Setting up the GUI
        JFrame gui = new JFrame(); //This makes the gui box
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Makes sure program can close
        gui.setTitle("Verdure - by: Vadim Allayev"); //This is the title of the game, you can change it
        gui.setPreferredSize(new Dimension(WIDTH + 5,HEIGHT + 30)); //Setting the size for gui
        gui.setResizable(false); //Makes it so the gui can't be resized
        gui.getContentPane().add(this); //Adding this class to the gui

        /*If after you finish everything, you can declare your buttons or other things
         *at this spot. AFTER gui.getContentPane().add(this) and BEFORE gui.pack();
         */

        gui.pack(); //Packs everything together
        gui.setLocationRelativeTo(null); //Makes so the gui opens in the center of screen
        gui.setVisible(true); //Makes the gui visible
        gui.addKeyListener(this);
        gui.addMouseListener(this);
    }
    
    
    //This method will acknowledge user input
    public void keyPressed(KeyEvent e) 
    {
        int key = e.getKeyCode();
        //System.out.println(key);
        
        if(!map.getName().equals("Menu") && !beatTheGame) {
           if(!cutscene && !hero.isDead())
            {
                //INVENTORY STUFFS
                if(key==81) //q - open/close inventory (maybe make it F)
                {
                    inventory.toggleVisibility();
                    if(!inventory.isVisible()) //so it was turned off
                    {
                        inventory.reset();
                    }
                }
                if(inventory.isVisible())
                {
                    //turning pages
                    if(key==37) //left
                        inventory.turnPage(false);
                    else if(key==39) //right
                        inventory.turnPage(true);
                    
                    if(inventory.getPage()==2)
                    {
                        //if you're currently selecting something
                        if(inventory.getSelection()!=-1)
                        {
                            if(key==38) //up
                            {
                                inventory.selectionUp();
                            }
                            else if(key==37) //left
                            {
                                inventory.selectionLeft();
                            }
                            else if(key==40) //down
                            {
                                inventory.selectionDown();
                            }
                            else if(key==39) //right
                            {
                                inventory.selectionRight();
                            }
                            else if(key==27) //esc - stop selection
                            {
                                inventory.toggleSelection(false);
                            }
                            else if(key==69) //e - equip
                            {
                                inventory.useSelected(hero,'e');
                            }
                            else if(key==67) //c - consume
                            {
                                inventory.useSelected(hero,'c');
                            }
                            else if(key==84) //t - trash
                            {
                                inventory.trashSelected(hero);
                            }
                            else if(key==85) //u - unequip
                            {
                                inventory.useSelected(hero,'u');
                            }
                        }
                        else if(key==10) //enter
                        {
                            inventory.toggleSelection(true);
                        }
                    }
                    else if(inventory.getPage()==3)
                    {
                        if(key==49 || key==97) //1
                        {
                            inventory.spendPoint(hero,1);
                        }
                        else if(key==50 || key==98) //2
                        {
                            inventory.spendPoint(hero,2);
                        }
                        else if(key==51 || key==99) //3
                        {
                            inventory.spendPoint(hero,3);
                        }
                        else if(key==52 || key==100) //4
                        {
                            inventory.spendPoint(hero,4);
                        }
                    }
                    else if(inventory.getPage()==4)
                    {
                        if(key==16) //SHIFT
                        {
                            defaultControls = !defaultControls;
                            inventory.toggleControls();
                        }
                    }
                }

                //MAJORITY OF COMMANDS
                else {
                    if(key==87) //w
                    {
                        hero.setUp(true);
                    }
                    if(key==83) //s
                    {
                        hero.setDown(true);
                        //if shielding, press down to roll
                        if(hero.isReady() || (hero.getAction()=='s' && hero.isKnight()) || (hero.getAction()=='t' && !hero.isKnight())) {
                            hero.setAction('r');
                            hero.setAnimationTime(currentTime);
                        }
                    }
                    if(key==65) //a
                    {
                        if(hero.isReady() || hero.isAirborne()) {
                            hero.setLeft(true);
                            if(hero.isFacingRight()) //fix image
                            {
                                if(hero.isKnight())
                                    hero.move(-40,0);
                                else
                                    hero.move(-65,0);
                            }
                            hero.setFacingRight(false);
                        }
                        //if shielding, you can change direction
                        else if((hero.getAction()=='s' && hero.isKnight()) || (hero.getAction()=='t' && !hero.isKnight()))
                        {
                            if(hero.isFacingRight()) //fix image
                            {
                                if(hero.isKnight())
                                    hero.move(-40,0);
                                else
                                    hero.move(-65,0);
                            }
                            hero.setFacingRight(false);
                        }
                    }
                    if(key==68) //d
                    {
                        if(hero.isReady() || hero.isAirborne()) {
                            hero.setRight(true);
                            if(!hero.isFacingRight()) //fix image
                            {
                                if(hero.isKnight())
                                    hero.move(40,0);
                                else
                                    hero.move(65,0);
                            }
                            hero.setFacingRight(true);
                        }
                        //if shielding, you can change direction
                        else if((hero.getAction()=='s' && hero.isKnight()) || (hero.getAction()=='t' && !hero.isKnight()))
                        {
                            if(!hero.isFacingRight()) //fix image
                            {
                                if(hero.isKnight())
                                    hero.move(40,0);
                                else
                                    hero.move(65,0);
                            }
                            hero.setFacingRight(true);
                        }
                    }
                    if(key==79) //o
                    {
                        if(defaultControls) {
                            //if not attacking or shielding
                            if(hero.isReady())
                            {
                                //if attacking for first time or time between previous attack
                                //and current attack is too much
                                if(hero.getAttackStage()==1 || hero.getAttackTime()+400<currentTime)
                                {
                                    hero.setAction('a');
                                    hero.setAnimationTime(currentTime);
                                    hero.setAttacking(true); //for smoothness
                                }
                                else if(hero.getAttackStage()==2)
                                {
                                    hero.setAction('b');
                                    hero.setAnimationTime(currentTime);
                                    hero.setAttacking(true);
                                }
                                else //hero.getAttackStage()==3
                                {
                                    hero.setAction('c');
                                    hero.setAnimationTime(currentTime);
                                    hero.setAttacking(true);
                                }
                            }
                        }
                    }
                    else if(key==74) { //j
                        if(!defaultControls) {
                            //if not attacking or shielding
                            if(hero.isReady())
                            {
                                //if attacking for first time or time between previous attack
                                //and current attack is too much
                                if(hero.getAttackStage()==1 || hero.getAttackTime()+400<currentTime)
                                {
                                    hero.setAction('a');
                                    hero.setAnimationTime(currentTime);
                                    hero.setAttacking(true); //for smoothness
                                }
                                else if(hero.getAttackStage()==2)
                                {
                                    hero.setAction('b');
                                    hero.setAnimationTime(currentTime);
                                    hero.setAttacking(true);
                                }
                                else //hero.getAttackStage()==3
                                {
                                    hero.setAction('c');
                                    hero.setAnimationTime(currentTime);
                                    hero.setAttacking(true);
                                }
                            }
                        }
                    }
                    if(key==80) //p
                    {
                        if(defaultControls) {
                            //if not attacking or shielding, put up shield
                            if(hero.isReady() && hero.getMana()>0) {
                                hero.setAction('t');
                                hero.setAnimationTime(currentTime);
                            }

                            //holding shield
                            hero.setUsingAbility(true);
                        }
                    }
                    else if(key==75) //k
                    {
                        if(!defaultControls) {
                            //if not attacking or shielding, put up shield
                            if(hero.isReady() && hero.getMana()>0) {
                                hero.setAction('t');
                                hero.setAnimationTime(currentTime);
                            }

                            //holding shield
                            hero.setUsingAbility(true);
                        }
                    }
                    if(key==32) //space - jump
                    {
                        if(hero.isReady()) {
                            hero.setAction('j');
                            hero.setAnimationTime(currentTime);
                        }
                    }

                    if(key==49 || key==97) //1
                    {
                        String proj = inventory.getEquipment()[0].getProj();
                        int cost = inventory.getEquipment()[0].getCost();
                        if(!proj.equals("") && hero.getMana()>=cost) {
                            hero.addProjectile(proj,enemies,currentTime,backgroundX,backgroundY);
                            hero.expendMana(cost);
                        }
                    }
                    else if(key==50 ||key==98) //2
                    {
                        String proj = inventory.getEquipment()[1].getProj();
                        int cost = inventory.getEquipment()[1].getCost();
                        if(!proj.equals("") && hero.getMana()>=cost) {
                            hero.addProjectile(proj,enemies,currentTime,backgroundX,backgroundY);
                            hero.expendMana(cost);
                        }
                    }
                    else if(key==51 || key==99) //3
                    {
                        String proj = inventory.getEquipment()[2].getProj();
                        int cost = inventory.getEquipment()[2].getCost();
                        if(!proj.equals("") && hero.getMana()>=cost) {
                            hero.addProjectile(proj,enemies,currentTime,backgroundX,backgroundY);
                            hero.expendMana(cost);
                        }
                    }
                    else if(key==52 || key==100) //4
                    {
                        String proj = inventory.getEquipment()[3].getProj();
                        int cost = inventory.getEquipment()[3].getCost();
                        if(!proj.equals("") && hero.getMana()>=cost) {
                            hero.addProjectile(proj,enemies,currentTime,backgroundX,backgroundY);
                            hero.expendMana(cost);
                        }
                    }
                    
                    //auto-consumes consumables from inventory
                    if(key==72) //h
                    {
                        hero.quickHeal(inventory);
                    }

                    //interact
                    if(key==10 && progress.equals("")) //ENTER
                    {
                        if(map.getName().equals("Village")) {
                            int centerX = hero.getHurtbox().getX()-backgroundX+hero.getHurtbox().getW()/2;
                            //old man --> 1500
                            if(Math.abs(centerX-1596)<100) {
                                inventory.close();
                                cutscene = true;
                                text.updateWriteTime(currentTime);
                                progress = "Talk to Old Man";
                                writeMapName = false;
                                backgroundY = 0;
                                hero.freeze();
                            }
                            //nurse woman --> 2300
                            else if(Math.abs(centerX-2396)<100) {
                                inventory.close();
                                cutscene = true;
                                text.updateWriteTime(currentTime);
                                progress = "Talk to Nurse";
                                writeMapName = false;
                                backgroundY = 0;
                                hero.freeze();
                            }
                            //well
                            else if(Math.abs(centerX-1910)<100) {
                                if(!foundEasterEgg && aliveBosses[1]) {
                                    inventory.close();
                                    cutscene = true;
                                    text.updateWriteTime(currentTime);
                                    progress = "Entering the Well";
                                    writeMapName = false;
                                    backgroundY = 0;
                                    hero.freeze();
                                }
                                else if(story>=11 && !aliveBosses[1]) {
                                    inventory.close();
                                    backgroundY = 0;
                                    
                                    //cp
                                    hero.setX(500);
                                    friend.setX(500);
                                    hero.setFacingRight(true);
                                    map = new Map("Cave");

                                    backgroundX = 0;
                                    spawnEnemies(true);

                                    end = true;
                                    audio = new GameAudio(map.getSong());
                                    start = true;

                                    writeMapName = true;
                                    text.updateWriteTime(currentTime);
                                }
                            }
                        }
                        else if(map.getName().equals("Clearing")) {
                            int centerX = hero.getHurtbox().getX()-backgroundX+hero.getHurtbox().getW()/2;
                            //tree --> center of tree is at 725
                            if(Math.abs(centerX-725)<200) {
                                inventory.close();
                                cutscene = true;
                                text.updateWriteTime(currentTime);
                                progress = "Talk to Tree";
                                writeMapName = false;
                                backgroundY = 0;
                                hero.freeze();
                            }
                            //squirrel --> center of squirrel at 1048
                            else if(Math.abs(centerX-1048)<100) {
                                if(!audio.getName().equals("pranked")) {
                                    end = true;
                                    audio = new GameAudio("pranked");
                                    start = true;
                                }
                            }
                        }
                        else if(story>=13 && map.getName().equals("Cave")) {
                            int centerX = hero.getHurtbox().getX()-backgroundX+hero.getHurtbox().getW()/2;
                            //rival --> rivalX at 250, width is 270
                            if(Math.abs(centerX-520)<200) {
                                inventory.close();
                                cutscene = true;
                                text.updateWriteTime(currentTime);
                                progress = "Resurface";
                                writeMapName = false;
                                backgroundY = 0;
                                hero.freeze();
                            }
                        }
                        else if(!aliveBosses[2] && map.getName().equals("Deep Cave")) {
                            int centerX = hero.getHurtbox().getX()-backgroundX+hero.getHurtbox().getW()/2;
                            //MC Crabby Boi --> center of crab at 3140
                            if(Math.abs(centerX-1050)<130 && !audio.getName().equals("crabRave")) {
                                end = true;
                                audio = new GameAudio("crabRave");
                                start = true;
                            }
                        }
                    }
                    
                    if(key==16) { //SHIFT - dash
                        hero.startDash(currentTime);
                    } 
                }
            }
            else if(hero.isDead() && !hero.isDefeated()) {
                if(text.getWriteTime()+5000<currentTime) {
                    if(key==83 && hero.canRevive()) { //s - summon your might
                        hero.revive();
                    }
                    else if(key==82 && story>=3 && storage.isGameSaved()) { //r - return to checkpoint
                        //LOADING THE GAME
                        try {
                            //The destination to save the saved object file to
                            FileInputStream file = new FileInputStream("Storage.obj");

                            //ObjectInputStream is responisible for writing the byte file
                            ObjectInputStream inputStream = new ObjectInputStream(file);

                            //Trying to read the object, if the exception is thrown that means
                            //There isn't a previous saved object then is fine, just move on
                            //if there is then good we load in what is saved from the last session
                            storage = (Storage)inputStream.readObject();
                            backgroundX = storage.getBackgroundX();
                            hero = storage.getHero();
                            hero.land();
                            friend = storage.getFriend();
                            friend.setAnimationTime(0); //to prevent RTE
                            friend.setHitTime(0); //to prevent RTE
                            friend.resetProjectiles(); //to prevent RTE
                            map = storage.getMap();
                            inventory = storage.getInventory();
                            story = storage.getStory();
                            foundEasterEgg = storage.isFoundEasterEgg();
                            firstSpell = storage.isFirstSpell();
                            aliveBosses = storage.getAliveBosses();
                            twoPlayers = storage.getTwoPlayers();
                            discovered = storage.getDiscovered();
                            drops = new ArrayList<>();
                            
                            end = true;
                            audio = new GameAudio(map.getSong());
                            start = true;
                            backgroundY = 0;
                            step = 0;
                            text.updateWriteTime(currentTime);
                            progress = "";
                            bossFight = false;

                            hero.reset(currentTime);

                            if(backgroundX==0) {
                                spawnEnemies(true);
                                campX = 125;
                            }
                            else {
                                spawnEnemies(false);
                                campX = -map.getLimit()+WIDTH-150;
                            }

                            //who is who
                            if(hero.isKnight()) {
                                heroFace = "knightFace";
                                rivalFace = "archerFace";
                            }
                            else {
                                heroFace = "archerFace";
                                rivalFace = "knightFace";
                            }
                        }
                        catch(Exception ex) {
                        }
                    }
                    else if(key==69) { //e - embrace the inevitable
                        map = new Map("Menu"); //"Menu" initially
                        end = true;
                        audio = new GameAudio("menuTheme");
                        start = true;
                        backgroundX = 0;
                        backgroundY = 0;
                        step = 0; //0 initially
                        story = 0; //0 initially
                        progress = "Tutorial"; //"Tutorial" initially
                        bossFight = true; //initially true to not let people go too far

                        //audio
                        start = true;
                        end = true;
                        audio = new GameAudio("menuTheme");

                        hero = new Knight();
                        friend = new Enemy();
                        enemies = new ArrayList<Enemy>();
                        spawnRival(2250,false); //initial addition

                        aliveBosses = new boolean[3];
                        aliveBosses[0] = true; //moss dragon in deep forest
                        aliveBosses[1] = true; //king in the castle
                        aliveBosses[2] = true; //sandworm in deep cave (postgame)

                        drops = new ArrayList<Item>();
                    }
                    else if(key==81) { //q - quit
                        System.exit(0);
                    }
                }
            }
        }
    }
    
    
    //All your UI drawing goes in here
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH+10,HEIGHT+30);
        
        if(map.getName().equals("Menu")) {
            g.setColor(new Color(16,18,15));
            g.fillRect(0,0,WIDTH,HEIGHT);

            Image nature = new ImageIcon(this.getClass().getResource("Backgrounds/natureBackground.png")).getImage();
            g2d.drawImage(nature,WIDTH/2-320-backgroundX,HEIGHT/2-160-backgroundY,640+backgroundX*2,320+backgroundY*2,null);

            if(backgroundX<250) {
                backgroundX+=2;
                backgroundY++;
            }
            else {
                g.setFont(new Font("Monospaced",Font.PLAIN,100));
                g.setColor(Color.BLACK);
                g.drawString("Verdure",380,230);
                g.setFont(new Font("Monospaced",Font.PLAIN,20));
                g.setColor(Color.WHITE);
                g.drawString("Version 1.3",1050,660);
                g.setColor(Color.BLACK);
                if(step==0) {
                    g.setFont(new Font("Monospaced",Font.PLAIN,50));
                    g.drawString("Press ENTER to start",290,500);
                }
                else if(step==1) {
                    g.setFont(new Font("Monospaced",Font.PLAIN,45));
                    g.drawString("New Game - N",425,380);
                    g.drawString("Back - ESC",453,430);
                }
                else if(step==11) {
                    g.setFont(new Font("Monospaced",Font.PLAIN,45));
                    g.drawString("New Game - N",420,380);
                    g.drawString("Continue - C",420,430);
                    g.drawString("Back - ESC",453,480);
                }
                else if(step==2 || step==12) {
                    g.setFont(new Font("Monospaced",Font.PLAIN,45));
                    g.drawString("Play as the Knight - K",293,380);
                    g.drawString("Play as the Archer - A",293,430);
                    g.drawString("Back - ESC",453,480);
                }
                else if(step==3 || step==13) {
                    g.setFont(new Font("Monospaced",Font.PLAIN,45));
                    g.drawString("Tutorial - Y",415,380);
                    g.drawString("No Tutorial - N",385,430);
                    if(!storage.getAliveBosses()[1] || storage.getStory()==100) { //if you've already defeated the king
                        g.drawString("I've Played So Many Times Already - T",100,480);
                        g.drawString("Back - ESC",453,530);
                    }
                    else {
                        g.drawString("Back - ESC",453,480);
                    }
                }
                g.setColor(Color.RED);
                //g.drawLine(WIDTH/2,0,WIDTH/2,HEIGHT);
            }
        }
        else if(beatTheGame) {
            backgroundX-=3;
            if(backgroundX<=map.getLimit()) {
                if(map.nextMap().exists())
                    map = map.nextMap();
                else
                    map = new Map("Deep Forest");
                backgroundX = 0;
                spawnEnemies(true);
            }
            
            map.drawBack(g2d, backgroundX, backgroundY);
            for(int i=0; i<enemies.size(); i++)
            {
                if(enemies.get(i).exists()) {
                    enemies.get(i).drawSelf(g2d,currentTime,backgroundX,backgroundY);
                    enemies.get(i).drawEffects(g2d,currentTime,backgroundX,backgroundY);
                }
            }
            map.drawFront(g2d, HEIGHT, backgroundX, backgroundY);
            if(step==0) {
                text.reassignCoordinates(WIDTH/2-90,150);
                boolean finished = text.typeWithTimer("The End",g2d,400,currentTime,1000);
                if(finished)
                    step++;
            }
            else {
                text.reassignCoordinates(WIDTH/2-90,150);
                text.write("The End",g2d);
                
                text.reassignCoordinates(WIDTH/2-140,300);
                text.write("Return to Game - G",g2d,3);
                text.reassignCoordinates(WIDTH/2-107,350);
                text.write("Go to Menu - M",g2d,3);
                text.reassignCoordinates(WIDTH/2-62,400);
                text.write("Quit - Q",g2d,3);
                
                text.reassignCoordinates(WIDTH/2-305,HEIGHT-100);
                text.write("Thank you for playing my game! - Vadim Allayev",g2d,3);
            }
        }
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0,0,WIDTH,HEIGHT);
            /*
            else if(map.equals("Ocean"))
            {
                //768,192
                Image ocean = new ImageIcon(this.getClass().getResource("Backgrounds/oceanBackground.png")).getImage();
                g2d.drawImage(ocean,backgroundX,HEIGHT-768+backgroundY,3072,768,null);
                g2d.drawImage(ocean,backgroundX+3072,HEIGHT-768+backgroundY,3072,768,null);
            }
            */
            
            map.drawBack(g2d, backgroundX, backgroundY);
            drawCampfire(g2d);
            for(int i=0; i<drops.size(); i++) {
                Item item = drops.get(i);
                item.drawSelf(g2d,backgroundX,backgroundY);
            }
            
            //if hero is attacking hero should go on top of enemies
            if(hero.getAction()=='a' || hero.getAction()=='b' || hero.getAction()=='c')
            {
                //enemies
                for(int i=0; i<enemies.size(); i++)
                {
                    if(enemies.get(i).exists()) {
                        enemies.get(i).drawSelf(g2d,currentTime,backgroundX,backgroundY);
                        enemies.get(i).drawEffects(g2d,currentTime,backgroundX,backgroundY);
                    }
                    else {
                        //GAIN EXP HERE
                        int yield = enemies.get(i).getExpYield();
                        int increment = hero.gainExp(yield,currentTime,backgroundX,backgroundY);
                        while(increment>0) {
                            inventory.incrementPoints(hero);
                            increment--;
                        }
                        if(twoPlayers && !(friend instanceof Butterfly)) {
                            boolean incrementFriend = friend.gainExp(yield);
                            if(incrementFriend)
                                inventory.incrementFriendPoints();
                        }
                        
                        //DROP CHANCE HERE
                        Item drop = enemies.get(i).dropItem();
                        if(drop.exists()) {
                            drop.allocateCoordinates(enemies.get(i).getDeathX());
                            drops.add(drop);
                        }
                            
                        enemies.remove(i);
                        i--;
                    }
                }

                //friend
                if(twoPlayers && friend.exists()) {
                    if(displayP2Level) {
                        String display;
                        if(friend.getLevel()==21) {
                            display = "MAX"+" "+friend.getLife()+"/"+friend.getMaxLife();
                            text.reassignCoordinates(friend.getX()+friend.getW()/2+backgroundX-(display.length()*6), friend.getY()-42+backgroundY);
                        }
                        else {
                            display = "Lvl."+friend.getLevel()+" "+friend.getLife()+"/"+friend.getMaxLife();
                            text.reassignCoordinates(friend.getX()+friend.getW()/2+backgroundX-(display.length()*5), friend.getY()-42+backgroundY);
                        }
                        text.writeOnOneLine(display, g2d,3);
                    }
                    else {
                        Image indicator = new ImageIcon(this.getClass().getResource("Images/playerTwoIndicator.png")).getImage();
                        g2d.drawImage(indicator,friend.getX()+friend.getW()/2-25+backgroundX,friend.getY()-42+backgroundY,50,28,null); //25,14
                    }
                    friend.drawSelf(g2d, currentTime, backgroundX, backgroundY);
                    friend.drawEffects(g2d, currentTime, backgroundX, backgroundY);
                    friend.createHitboxes(currentTime, backgroundX, backgroundY);
                }
                
                //hero
                hero.drawSelf(g2d,currentTime,backgroundX,backgroundY);
            }
            else //enemies on top of hero
            {
                //friend
                if(twoPlayers && friend.exists()) {
                    if(displayP2Level) {
                        String display;
                        if(friend.getLevel()==21) {
                            display = "MAX"+" "+friend.getLife()+"/"+friend.getMaxLife();
                            text.reassignCoordinates(friend.getX()+friend.getW()/2+backgroundX-(display.length()*6), friend.getY()-42+backgroundY);
                        }
                        else {
                            display = "Lvl."+friend.getLevel()+" "+friend.getLife()+"/"+friend.getMaxLife();
                            text.reassignCoordinates(friend.getX()+friend.getW()/2+backgroundX-(display.length()*5), friend.getY()-42+backgroundY);
                        }
                        text.writeOnOneLine(display, g2d,3);
                    }
                    else {
                        Image indicator = new ImageIcon(this.getClass().getResource("Images/playerTwoIndicator.png")).getImage();
                        g2d.drawImage(indicator,friend.getX()+friend.getW()/2-25+backgroundX,friend.getY()-42+backgroundY,50,28,null); //25,14
                    }
                    friend.drawSelf(g2d, currentTime, backgroundX, backgroundY);
                    friend.drawEffects(g2d, currentTime, backgroundX, backgroundY);
                    friend.createHitboxes(currentTime, backgroundX, backgroundY);
                }
                
                //hero
                hero.drawSelf(g2d,currentTime,backgroundX,backgroundY);
                
                //enemies
                for(int i=0; i<enemies.size(); i++)
                {
                    if(enemies.get(i).exists()) {
                        enemies.get(i).drawSelf(g2d,currentTime,backgroundX,backgroundY);
                        try {
                            enemies.get(i).drawEffects(g2d,currentTime,backgroundX,backgroundY);
                        }
                        catch(Exception e) {
                        }
                    }
                    else {
                        //GAIN EXP HERE
                        int yield = enemies.get(i).getExpYield();
                        int increment = hero.gainExp(yield,currentTime,backgroundX,backgroundY);
                        while(increment>0) {
                            inventory.incrementPoints(hero);
                            increment--;
                        }
                        if(twoPlayers && !(friend instanceof Butterfly)) {
                            boolean incrementFriend = friend.gainExp(yield);
                            if(incrementFriend)
                                inventory.incrementFriendPoints();
                        }
                        
                        //DROP CHANCE HERE
                        Item drop = enemies.get(i).dropItem();
                        if(drop.exists()) {
                            drop.allocateCoordinates(enemies.get(i).getDeathX());
                            drops.add(drop);
                        }
                        
                        enemies.remove(i);
                        i--;
                    }
                }
            }
            
            
            
            map.drawFront(g2d, HEIGHT, backgroundX, backgroundY);
            
            
            //LIFE + MANA BARS ------------------------------------------------
            int w1,w2;
            //life: 1 hp = 56 pixels
            w1 = hero.getMaxLife()*56;
            
            //mana
            w2 = hero.getMaxMana()*56;
            
            //everything magnified by a factor of 4
            Image top = new ImageIcon(this.getClass().getResource("Images/barTop.png")).getImage();
            Image side = new ImageIcon(this.getClass().getResource("Images/barSide.png")).getImage();
            g2d.drawImage(side,10,10,4,60,null); //leftmost side
            g2d.drawImage(top,14,10,w1,32,null); //hp horizontal lines
            g2d.drawImage(top,14,38,w2,32,null); //mp horizontal lines
            g2d.drawImage(side,14+w1,10,4,32,null); //hp vertical line right
            g2d.drawImage(side,14+w2,38,4,32,null); //mp vertical line right CHANGE Y
            
            Color darkRed = new Color(115,56,56);
            Color red = new Color(130,63,63);
            Color darkBlue = new Color(21,63,84);
            Color blue = new Color(26,76,102);
            
            g.setColor(darkRed);
            g.fillRect(14,14,(int)((w1)*((double)hero.getLife()/hero.getMaxLife())),4);
            g.setColor(red);
            g.fillRect(14,18,(int)((w1)*((double)hero.getLife()/hero.getMaxLife())),20);
            g.setColor(darkBlue);
            g.fillRect(14,42,(int)((w2)*((double)hero.getMana()/hero.getMaxMana())),4);
            g.setColor(blue);
            g.fillRect(14,46,(int)((w2)*((double)hero.getMana()/hero.getMaxMana())),20);
            
            Image icons = new ImageIcon(this.getClass().getResource("Images/barLifeArmorIcons.png")).getImage();
            g2d.drawImage(icons,10,10,28,60,null); //7,15
            
            //displays speed of Hero throughout run
            //g.setColor(Color.PINK);
            //g.fillRect(14,70,hero.getDashSpeed(currentTime)*5,24);
            //------------------------------------------------------------------
            
            //EXP BAR + DASH ICON
            if(!cutscene) {
                //EXP BAR
                int expX = WIDTH/2-204, expY = HEIGHT-37;
                Image exp = new ImageIcon(this.getClass().getResource("Images/barExp.png")).getImage(); //136 x 9
                g2d.drawImage(exp, expX, expY, 408, 27, null);

                int lvl = hero.getLevel();
                if(lvl<13) {
                    text.reassignCoordinates(WIDTH/2-45,HEIGHT-58);
                    text.write("Level "+lvl, g2d, 3);
                    int expWidth = hero.expRatio(360);
                    g.setColor(new Color(104,153,17)); //lime
                    g.fillRect(expX+24, expY+6, expWidth, 15);
                }
                else {
                    text.reassignCoordinates(WIDTH/2-63,HEIGHT-58);
                    text.write("Max Level", g2d, 3);
                    g.setColor(new Color(104,153,17)); //lime
                    g.fillRect(expX+24, expY+6, 360, 15);
                } 
                
                //SPRINT / DASH COOLDOWN INDICATOR
                String dashName = "Images/dashAvailableIcon.png";
                if(!hero.dashAvailable(currentTime)) {
                    if(hero.getDashTime() + hero.getDashDuration() > currentTime)
                        dashName = "Images/dashInUseIcon.png";
                    else
                        dashName = "Images/dashCooldownIcon.png";
                }
                
                //drawing indicator (22 x 22)
                Image dashIcon = new ImageIcon(getClass().getResource(dashName)).getImage();
                g2d.drawImage(dashIcon,15,HEIGHT-59,44,44,null); 
            }
            
            //MUTED ICON/AUDIO ON/OFF ICON
            String name = "audioOnIcon";
            if(muted)
                name = "audioOffIcon";
            Image sound = new ImageIcon(this.getClass().getResource("Images/"+name+".png")).getImage();
            g2d.drawImage(sound,WIDTH-70,HEIGHT-80,57,65,null); //57,65
            
            //INVENTORY
            inventory.drawSelf(g,g2d,hero,friend,discovered,map.getLocation(),story);
            
            //TEXT
            if(cutscene) {
                cutscene(g2d);
            }
            else {
                moreText(g2d);
            }
            
            if(writeMapName) {
                text.reassignCoordinates(WIDTH/2-((map.getName().length()/2)*25), 30);
                boolean finished = text.typeWithTimer(map.getName(),g2d,300,currentTime,1000);
                if(finished)
                    writeMapName = false;
            }
            
            if(hero.isDead()) {
                if(step==0) {
                    inventory.close();
                    step++;
                }
                text.reassignCoordinates(WIDTH/2-90, 200);
                boolean finished = text.typeWithTimer("YOU LOSE", g2d, 500, currentTime,1000);
                if(finished) {
                    if(hero.canRevive() && story>=3 && storage.isGameSaved()) {
                        text.reassignCoordinates(WIDTH/2-170,300);
                        text.write("Return to Checkpoint - R", g2d, 3);
                        text.reassignCoordinates(WIDTH/2-150,340);
                        text.write("Summon your Might - S", g2d, 3);
                        text.reassignCoordinates(WIDTH/2-180,380);
                        text.write("Embrace the Inevitable - E",g2d, 3);
                        text.reassignCoordinates(WIDTH/2-60,420);
                        text.write("Quit - Q",g2d,3);
                    }
                    else if(story>=3 && storage.isGameSaved()) {
                        text.reassignCoordinates(WIDTH/2-170,300);
                        text.write("Return to Checkpoint - R", g2d, 3);
                        text.reassignCoordinates(WIDTH/2-180,340);
                        text.write("Embrace the Inevitable - E",g2d, 3);
                        text.reassignCoordinates(WIDTH/2-60,380);
                        text.write("Quit - Q",g2d,3);
                    }
                    else if (hero.canRevive()) {
                        text.reassignCoordinates(WIDTH/2-150,300);
                        text.write("Summon your Might - S", g2d, 3);
                        text.reassignCoordinates(WIDTH/2-180,340);
                        text.write("Embrace the Inevitable - E",g2d, 3);
                        text.reassignCoordinates(WIDTH/2-60,380);
                        text.write("Quit - Q",g2d,3);
                    }
                    else {
                        text.reassignCoordinates(WIDTH/2-180,300);
                        text.write("Embrace the Inevitable - E",g2d, 3);
                        text.reassignCoordinates(WIDTH/2-60,340);
                        text.write("Quit - Q",g2d,3);
                    }
                }
            }
        }
        
        //text.reassignCoordinates(100,100);
        //text.write("",g2d);
        
        //drawVerticalCenterLine(g);
    }
    
    
    public void loop()
    {
        currentTime = System.currentTimeMillis()-startTime;
        
        for(int i=0; i<enemies.size(); i++) {
            if(map.getName().equals("Deep Forest") && enemies.get(i) instanceof MossDragon && backgroundX==0 && enemies.size()==1 && !bossFight)
            {
                ((MossDragon)enemies.get(i)).wakeUp();
                bossFight = true;
                end = true;
                audio = new GameAudio("vsDragon");
                start = true;
            }
            else if(map.getName().equals("Castle") && enemies.get(i) instanceof King && backgroundX<=map.getLimit() && enemies.size()==1  && !bossFight)
            {
                ((King)enemies.get(i)).grandReveal();
                inventory.close();
                bossFight = true;
                cutscene = true;
                text.updateWriteTime(currentTime);
                progress = "Final Boss";
                backgroundY = 0;
                hero.freeze(true);
                end = true;
                audio = new GameAudio("ominous");
                start = true;
            }
            else if(map.getName().equals("Deep Cave") && enemies.get(i) instanceof Sandworm && backgroundX<=map.getLimit()+200 && enemies.size()==1  && !bossFight)
            {
                ((Sandworm)enemies.get(i)).emerge(currentTime);
                inventory.close();
                backgroundY = 0;
                bossFight = true;
                cutscene = true;
                hero.freeze(true);
                text.updateWriteTime(currentTime);
                writeMapName = false;
                progress = "Demon Underground";
                end = true;
            }
            
            enemies.get(i).act(hero,currentTime,backgroundX,backgroundY);
            enemies.get(i).createHitboxes(currentTime, backgroundX, backgroundY);
            enemies.get(i).getHurt(hero, currentTime,backgroundX,backgroundY);
            if(twoPlayers) {
                enemies.get(i).getHurt(friend,currentTime,backgroundX,backgroundY);
            }
        }
        
        if(bossFight) {
            if(enemies.size()==0 && (map.getName().equals("Deep Forest") || map.getName().equals("Castle") || map.getName().equals("Deep Cave"))) {
                bossFight = false;
                if(map.getName().equals("Deep Forest") && aliveBosses[0]) {
                    aliveBosses[0] = false;
                    Artifact a1 = new Artifact("Sigalita's Sunfire Cape");
                    Artifact a2 = new Artifact("Leon's Wand of Blasting");
                    Artifact a3 = new Artifact("Flamberge of Destruction");
                    Consumable c1 = new Consumable("Amethyst");
                    a1.allocateCoordinates(50);
                    a2.allocateCoordinates(86);
                    a3.allocateCoordinates(122);
                    c1.allocateCoordinates(158);
                    drops.add(a1);
                    drops.add(a2);
                    drops.add(a3);
                    drops.add(c1);
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                }
                else if(map.getName().equals("Castle") && aliveBosses[1]) {
                    inventory.close();
                    story = 10; //for post-game to work
                    aliveBosses[1] = false;
                    enemies = new ArrayList<>();
                    end = true;
                    audio = new GameAudio("victory");
                    cutscene = true;
                    backgroundY = 0;
                    hero.freeze();
                    progress = "Victory";
                    text.updateWriteTime(currentTime);
                    step = 0;
                }
                else if(map.getName().equals("Deep Cave") && aliveBosses[2]) {
                    aliveBosses[2] = false;
                    story++;
                    enemies = new ArrayList<>();
                    end = true;
                    spawnRival(2300,true);
                }
            }
        }
        
        for(int i=0; i<drops.size(); i++) {
            if(hero.getHurtbox().overlaps(drops.get(i),backgroundX,backgroundY) && !inventory.isFull())
            {
                Item item = drops.remove(i);
                inventory.addItem(item);
                i--;
                if(firstSpell) {
                    if(item instanceof Artifact) {
                        if(!((Artifact)item).getProj().equals("")) {
                            bossFight = true;
                            firstSpell = false;
                            progress = "First Spell";
                            text.updateWriteTime(currentTime);
                        }
                    }
                }
            }
        }
        
        hero.act(enemies,currentTime);
        
        if(hero.isDefeated()) {
            text.updateWriteTime(currentTime);
            inventory.close();
            hero.setDefeated(false);
        }
            
        if(!bossFight)
            movement();
        else
            movementBossFight();
        
        if(twoPlayers && friend.exists()) {
            
            friend.getHurt(enemies, currentTime, backgroundX, backgroundY);
            friend.setAlert(false);
            
            if(friend.isReady()) {
                if(friendAttacking) {
                    if(friend instanceof Harpy) {
                        if(!((Harpy)friend).getElement().exists()) {
                            int num = ((Harpy)friend).nextElement();
                            Projectile elem;
                            Hitbox box = friend.getHurtbox();
                            if(num==1) {
                                if(friend.isFacingRight())
                                    elem = new Projectile(currentTime,"geyser",friend.getX()+(3*friend.getW()));
                                else
                                    elem = new Projectile(currentTime,"geyser",friend.getX()-(2*friend.getW()));
                            }
                            else if(num==2) 
                                elem = new Projectile(currentTime,"flare",box.getX()+box.getW()/2-backgroundX,box.getY()+box.getH()/3);
                            else {
                                if(!friend.isFacingRight())
                                    elem = new Projectile(currentTime,"vine",friend.getX()+(3*friend.getW()));
                                else
                                    elem = new Projectile(currentTime,"vine",friend.getX()-(2*friend.getW()));
                            }
                            ((Harpy)friend).setElement(elem);
                        }
                    }
                    else if(friend instanceof Clay) {
                        if(((Clay)friend).getShootTime()+1000<currentTime) {
                            ((Clay)friend).setShootTime(currentTime);
                            ((Clay)friend).getTimes().add(currentTime);
                            if(friend.isFacingRight()) {
                                ((Clay)friend).getWheels().add(new Projectile("clayWheel",friend.getX()+friend.getW(),friend.getY(),6,0));
                                ((Clay)friend).getDirections().add(true);
                            }
                            else {
                                ((Clay)friend).getWheels().add(new Projectile("clayWheel",friend.getX(),friend.getY(),-6,0));
                                ((Clay)friend).getDirections().add(false);
                            }
                        }
                    }
                    else {
                        friend.setAction('a');
                        friend.setAnimationTime(currentTime);
                    }
                }
                else {
                    //finding coordiantes of mouse
                    PointerInfo pInfo = MouseInfo.getPointerInfo();
                    Point p = pInfo.getLocation();
                    int x = (int)p.getX();

                    //finding size of screen to find distance between leftmost side of app and leftmost side of screen
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int extraX = (screenSize.width-WIDTH)/2;

                    //updating next coordinates
                    int nextX = x-extraX;

                    //friend hurtbox x and w
                    int hx = friend.getHurtbox().getX();
                    int hw = friend.getHurtbox().getW();

                    //MOVEMENT
                    if(friend instanceof Butterfly) {
                        /*int y = (int)p.getY();
                        int extraY = (screenSize.height-HEIGHT)/2;
                        int nextY = y-extraY;

                        int distX = nextX-friend.getX()+friend.getW()/2+backgroundX;
                        int distY = nextY-friend.getY()+friend.getH()/2+backgroundY;
                        double distance = Math.sqrt(Math.pow(distX,2)+Math.pow(distY,2));
                        double angle = Math.toDegrees(Math.atan((double)distY/distX));
                        System.out.println("x: "+friend.getX()+", y: "+friend.getY());
                        if(distance>=7) {
                            double sin = Math.toDegrees(Math.sin(angle));
                            double cos = Math.toDegrees(Math.cos(angle));
                            friend.move((int)(distance*sin),(int)(distance*cos));
                        }*/
                        hx = friend.getX()+backgroundX;
                        hw = friend.getW();
                        int hy = friend.getY()+backgroundY;
                        int hh = friend.getH();

                        //x
                        if(friend.isFacingRight()) {
                            if(nextX<hx)
                                friend.turnAround();
                            else if(nextX>hx+hw) {
                                if(friend.getMove()==0)
                                    friend.setAction('i');
                                else
                                    friend.setAction('m');
                                friend.moveX(friend.getMobility());
                            }
                            else {
                                if(friend.getIdle()==0)
                                    friend.setAction('m');
                                else
                                    friend.setAction('i');
                            }
                        }
                        else {
                            if(nextX>hx+hw)
                                friend.turnAround();
                            else if(nextX<hx) {
                                if(friend.getMove()==0)
                                    friend.setAction('i');
                                else
                                    friend.setAction('m');
                                friend.moveX(-friend.getMobility());
                            }
                            else {
                                if(friend.getIdle()==0)
                                    friend.setAction('m');
                                else
                                    friend.setAction('i');
                            }
                        }

                        //y
                        int y = (int)p.getY();
                        int extraY = (screenSize.height-HEIGHT)/2;
                        int nextY = y-extraY;
                        int mob = friend.getMobility();
                        if(nextY<hy)
                            friend.moveY(-mob);
                        else if(nextY>hy+hh) {
                            if(hy+hh+mob<friend.getFLOORY())
                                friend.moveY(mob);
                        }
                    }
                    else {
                        if(friend.isFacingRight()) {
                            if(nextX<hx)
                                friend.turnAround();
                            else if(nextX>hx+hw) {
                                if(friend.getMove()==0)
                                    friend.setAction('i');
                                else
                                    friend.setAction('m');
                                friend.moveX(friend.getMobility());
                            }
                            else {
                                if(friend.getIdle()==0)
                                    friend.setAction('m');
                                else
                                    friend.setAction('i');
                            }
                        }
                        else {
                            if(nextX>hx+hw)
                                friend.turnAround();
                            else if(nextX<hx) {
                                if(friend.getMove()==0)
                                    friend.setAction('i');
                                else
                                    friend.setAction('m');
                                friend.moveX(-friend.getMobility());
                            }
                            else {
                                if(friend.getIdle()==0)
                                    friend.setAction('m');
                                else
                                    friend.setAction('i');
                            }
                        }
                    }
                }
            }
        }
        else if(twoPlayers) {
            friend = new Butterfly(friend.getX()+friend.getW()/2-backgroundX,true);
        }
        
        updateMap();
        checkForChanges();
        
        
        //Do not write below this
        repaint();
    }
    
    public void spawnRival(int locationX, boolean facingRight) {
        if(hero.isKnight())
            enemies.add(new RivalArcher(locationX,facingRight));
        else
            enemies.add(new RivalKnight(locationX,facingRight));
    }
    
    //for the story
    public void checkForChanges()
    {
        if(story==1) {
            if(backgroundX<=-1000 && map.getName().equals("Forest"))
            {
                story++;
                inventory.close();
                cutscene = true;
                text.updateWriteTime(currentTime);
                enemies = new ArrayList<>(); //necessary in case p2 shenanigans
                spawnRival(2250,false);
                progress = "Tutorial3";
                backgroundY = 0;
                hero.freeze(true);
                end = true;
                audio = new GameAudio("rival");
                start = true;
            }
        }
        else if(story==3) {
            if(map.getName().equals("Village"))
                story++;
        }
        else if(story==4) {
            if(map.getName().equals("Village") && backgroundX<-200) {
                enemies = new ArrayList<>();
                spawnEnemies(true);
                spawnRival(2000,false);
                inventory.close();
                cutscene = true;
                writeMapName = false;
                backgroundY = 0;
                hero.freeze();
                progress = "Entering the Village";
                story++;
            }
        }
        else if(story==6 && (map.getName().equals("Ruins") || map.getName().equals("Forest"))) {
            story++;
        }
        else if(story==7 && map.getName().equals("Jungle")) {
            if(backgroundX!=0) {
                enemies = new ArrayList<>();
                spawnRival(-400,true);
                inventory.close();
                cutscene = true;
                writeMapName = false;
                backgroundY = 0;
                hero.freeze();
                progress = "Entering the Jungle";
                story++;
                text.updateWriteTime(currentTime);
                
                end = true;
                audio = new GameAudio("rival");
            }
        }
        else if(story==9 && map.getName().equals("Clearing") && hero.getX()>WIDTH/4)
        {
            story++;
            inventory.close();
            enemies = new ArrayList<>();
            spawnEnemies(true);
            cutscene = true;
            writeMapName = false;
            progress = "The Talking Tree";
            backgroundY = 0;
            hero.freeze();
            text.updateWriteTime(currentTime);
        }
        //post-game, prompted by walking close to old man
        else if(story==10 && map.getName().equals("Village") && !aliveBosses[1] && hero.getX()-backgroundX<1700) {
            story++;
            inventory.close();
            backgroundY = 0;
            enemies = new ArrayList<>();
            spawnEnemies(true);
            enemies.get(2).setFacingRight(true);
            progress = "Rumbling From The Well";
            cutscene = true;
            writeMapName = false;
            hero.freeze(false);
            text.updateWriteTime(currentTime);
        }
        else if(story==11 && map.getName().equals("Cave") && backgroundX<=-730) {
            story++;
            inventory.close();
            backgroundY = 0;
            enemies = new ArrayList<>();
            spawnRival(2300,false);
            progress = "A Dangerous Path";
            cutscene = true;
            writeMapName = false;
            hero.freeze(true);
            text.updateWriteTime(currentTime);
            
            end = true;
            audio = new GameAudio("rival");
            start = true;
        }
        else if(story==12) {
            if(map.getName().equals("Cave")) {
                if(!cutscene) {
                    enemies.get(0).setAction('m');
                    if(enemies.get(0).getX()<250) {
                        enemies.get(0).setAction('i');
                        enemies.get(0).turnAround();
                        story = 13;
                    }
                }
            }
            else {
                story = 13;
            }
        }
        else if(bossFight && map.getName().equals("Deep Cave")) {
            boolean found = false;
            for(int i=0; i<enemies.size() && !found; i++) {
                if(enemies.get(i) instanceof Sandworm) {
                    found = true;
                    Sandworm worm = (Sandworm)enemies.get(i);
                    if(worm.isSpawning()) {
                        enemies.add(new Crab(worm.getSpawnLocation(),worm.getSpawnDirection(hero),true));
                        worm.stopSpawning();
                    }
                }
            }
        }
        else if(story==14 && map.getName().equals("Deep Cave")) {
            if(hero.getX() < WIDTH/3) {
                inventory.close();
                audio = new GameAudio("crabRave");
                start = true;
                cutscene = true;
                backgroundY = 0;
                hero.freeze(false);
                story++; //now 15
                progress = "Defeated the Sandworm";
                text.updateWriteTime(currentTime);
                step = 0;
                bossFight = true;
            }
        }
        
        if((!foundEasterEgg && aliveBosses[1]) && map.getName().equals("Cave"))
        {
            if(backgroundX<=-387) {
                writeMapName = false;
                progress = "Man in a Cave";
                text.updateWriteTime(currentTime);
                inventory.close();
                backgroundY = 0;
                hero.freeze();
                cutscene = true;
                foundEasterEgg = true;
            }
            
        }
    }
    
    public void updateMap()
    {
        if(hero.getX()>WIDTH && !cutscene) {
            Map next = map.nextMap();
            if(next.exists()) {
                map = next;
                backgroundX = 0;
                hero.setX(0);
                if(twoPlayers) {
                    friend.setX(hero.getX());
                    friend.fullHeal();
                }
                spawnEnemies(true);
                hero.resetProjectiles();
                drops = new ArrayList<Item>();
                
                discovered[next.getLocation()] = true;
                
                end = true;
                audio = new GameAudio(map.getSong());
                start = true;

                writeMapName = true;
                text.updateWriteTime(currentTime);
                
                //SAVING THE GAME
                if(story>=3) {
                    saveGame();
                    campX = 125;
                }
            }
            else {
                hero.move(-hero.getMobility(), 0);
            }
        }
        else if(hero.getX()+hero.getW()<0 && !cutscene) {
            Map prev = map.previousMap();
            if(prev.exists()) {
                map = prev;
                backgroundX = map.getLimit();
                hero.setX(WIDTH-hero.getW());
                if(twoPlayers) {
                    friend.setX(hero.getX()-backgroundX);
                    friend.fullHeal();
                }
                spawnEnemies(false);
                hero.resetProjectiles();
                drops = new ArrayList<Item>();
                
                discovered[prev.getLocation()] = true;
                
                end = true;
                audio = new GameAudio(map.getSong());
                start = true;

                writeMapName = true;
                text.updateWriteTime(currentTime);
                
                //SAVING THE GAME
                if (story >= 3) {
                    saveGame();
                    campX = -backgroundX + WIDTH - 150;
                }
            }
            else {
                hero.move(hero.getMobility(), 0);
            }
        }
    }
    
    //next = true if this new map is to the right of the previous map
    public void spawnEnemies(boolean next)
    {
        enemies = new ArrayList<Enemy>();
        String name = map.getName();
        
        if(name.equals("Forest")) {
            if(story==1) {
                spawnRival(2250,false);
            }
            else if(story==3) {
                enemies.add(new Mantis(3000,false));
                enemies.add(new Beetle(3500,false));
                enemies.add(new Slug(4000,false));
            }
            else {
                //if next==true, more enemies should be facing LEFT (false)
                //if next==false, more enemies should be facing RIGHT (true)
                //thus the !next
                enemies.add(new Mantis(randomInt(500,4000),randomInt(1,2)==1));
                enemies.add(new Mantis(randomInt(500,4000),!next));
                enemies.add(new Beetle(randomInt(500,4000),randomInt(1,2)==1));
                enemies.add(new Beetle(randomInt(500,4000),!next));
                enemies.add(new Beetle(randomInt(500,4000),!next));
                enemies.add(new Slug(randomInt(500,4000),randomInt(1,2)==1));
                enemies.add(new Slug(randomInt(500,4000),!next));
            }
        }
        else if(name.equals("Ruins")) {
            int rand = randomInt(1,3);
            if(rand==1)
                enemies.add(new Mantis(randomInt(400,3920),randomInt(1,2)==1));
            else if(rand==2)
                enemies.add(new Beetle(randomInt(400,3920),randomInt(1,2)==1));
            else
                enemies.add(new Slug(randomInt(400,3920),randomInt(1,2)==1));
            enemies.add(new Skeleton(randomInt(400,3920),!next));
            enemies.add(new BirdSpirit(randomInt(400,2120),!next));
            enemies.add(new BirdSpirit(randomInt(2200,3920),!next));
            enemies.add(new Clay(randomInt(400,3920),randomInt(1,2)==1));
            enemies.add(new Clay(randomInt(400,3920),!next));
        }
        else if(name.equals("Jungle")) {
            if(story==7) {
            }
            else if(story==8) {
                enemies.add(new Harpy(randomInt(1000,2120),randomInt(1,2)==1));
                enemies.add(new Harpy(randomInt(2800,3920),!next));
                enemies.add(new Armadillo(randomInt(1000,2500),!next));
                enemies.add(new Armadillo(randomInt(1000,3000),!next));
                enemies.add(new Crab(randomInt(1000,3920),randomInt(1,2)==1));
                enemies.add(new Crab(randomInt(1000,3920),!next));
                enemies.add(new Crab(randomInt(1000,3920),!next));
                story++;
            }
            else {
                enemies.add(new Harpy(randomInt(400,2120),randomInt(1,2)==1));
                enemies.add(new Harpy(randomInt(2200,3920),!next));
                enemies.add(new Armadillo(randomInt(400,3920),!next));
                enemies.add(new Armadillo(randomInt(400,3920),!next));
                enemies.add(new Crab(randomInt(400,3920),randomInt(1,2)==1));
                enemies.add(new Crab(randomInt(400,3920),!next));
                enemies.add(new Crab(randomInt(400,3920),!next));
            }
        }
        else if(name.equals("Aftermath")) {
            enemies.add(new Skeleton(randomInt(400,3920),randomInt(1,2)==1));
            enemies.add(new Skeleton(randomInt(400,3920),randomInt(1,2)==1));
            enemies.add(new Skeleton(randomInt(400,3920),randomInt(1,2)==1));
            enemies.add(new Skeleton(randomInt(400,3920),!next));
            enemies.add(new Skeleton(randomInt(400,3920),!next));
            enemies.add(new Dino(randomInt(400,3920),randomInt(1,2)==1));
            enemies.add(new Dino(randomInt(400,3920),!next));
            enemies.add(new Harpy(randomInt(400,3920),!next));
            enemies.add(new Hound(randomInt(1250,3170),randomInt(1,2)==1));
        }
        else if(name.equals("Deep Forest")) {
            enemies.add(new Mantis(randomInt(1200,5500),true));
            enemies.add(new Slug(randomInt(1200,5500),true));
            enemies.add(new Beetle(randomInt(1200,5500),true));
            enemies.add(new Gorilla(randomInt(1200,5500),randomInt(1,2)==1));
            enemies.add(new Gorilla(randomInt(1200,5500),randomInt(1,2)==1));
            enemies.add(new Hound(randomInt(1950,3700),randomInt(1,2)==1));
            enemies.add(new Hound(randomInt(3000,4750),randomInt(1,2)==1));
            if(aliveBosses[0])
                enemies.add(new MossDragon(-500,true));
        }
        else if(name.equals("Village"))
        {
            enemies.add(new VillagerMaleChild(2750,true));
            enemies.add(new VillagerFemaleChild(2650,true));
            enemies.add(new VillagerMaleElder(1500,!next));
            if(foundEasterEgg)
                enemies.add(new VillagerMaleAdult(2260,!next));
            enemies.add(new VillagerFemaleAdult(2300,!next));
        }
        else if(name.equals("Clearing")) {
            enemies.add(new VillagerSquirrel(1000,!next));
        }
        else if(name.equals("Cave")) {
            if(aliveBosses[1]) //didn't beat the game yet
                enemies.add(new VillagerMaleAdult(1370,false));
            else if(story>=13) { //talked to rival underground, ready for Sandworm boss battle
                spawnRival(250, true);
            }
        }
        else if(name.equals("Deep Cave")) {
            if(aliveBosses[2])
                enemies.add(new Sandworm(3340,false)); 
            else {
                enemies.add(new VillagerCrab(1000, false, true)); //dj
                for (int i = 100; i < 3640; i+=150) {
                    enemies.add(new VillagerCrab(i, Math.random()*2==0));
                }
            }
        }
        else if(name.equals("Castle"))
        {
            enemies.add(new HoundRoyal(750,true));
            if(aliveBosses[1])
                enemies.add(new King(4400,false));
        }
    }
    
    public int randomInt(int i1, int i2)
    {
        int rand = (int)(Math.random()*(i2-i1+1))+i1;
        return rand;
    }
    
    public void cutscene(Graphics2D g2d)
    {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,WIDTH,144);
        
        Image portrait = new ImageIcon(this.getClass().getResource("Images/portrait.png")).getImage();
        g2d.drawImage(portrait,0,0,144,144,null);
        
        String fileName = "Images/";
        text.reassignCoordinates(WRITEX, WRITEY);
        
        if(progress.equals("Test")) //for me
        {
            fileName+=heroFace;
            
            if(step==0) {
                next();
            }
            else if(step==1) {
                text.type("When the physics equation goes owo",g2d,100,currentTime);
            }
        }
        else if(progress.equals("Tutorial")) //the basics
        {
            fileName+=heroFace;
            
            if(step==0) {
                next();
            }
            else if(step==1) {
                boolean finished = text.typeWithTimer("Ugh...%%% W-where am I?",g2d,80,currentTime,1500);
                if(finished)
                    next();
            }
            else if(step==2) {
                boolean finished = text.typeWithTimer("And why does my head hurt so much?",g2d,60,currentTime,1500);
                if(finished)
                    next();
            }
            else if(step==3) {
                boolean finished = text.typeWithTimer("Well...%%% I guess I better go look for some help...",g2d,70,currentTime,1500);
                if(finished) {
                    cutscene = false;
                    step = 0;
                    text.updateWriteTime(currentTime);
                }                       
            }
        }
        else if(progress.equals("Tutorial2")) //unexpected encounter
        {
            if(step==0) {
                fileName+="questionMark";
                text.updateWriteTime(currentTime);
                step++;
                end = true;
                audio = new GameAudio("rival");
            }
            else if(step==1) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("YOOOOOOOOOOO!!!!!",g2d,80,currentTime,1000);
                if(finished)
                    next();
            }
            else if(step==2) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("?!?!",g2d,80,currentTime,1000);
                if(finished) {
                    next();
                    start = true;
                }
            }
            else if(step==3) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("Over here!! Phew,% I finally found you.",g2d,80,currentTime,1000);
                
                if(backgroundX>-1500) {
                    backgroundX-=5;
                    hero.move(-5, 0);
                }
                
                if(finished && backgroundX<=-1500)
                    next();
            }
            else if(step==4) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("You have no idea what I've had to go through to find you.",g2d,50,currentTime,1000);
                
                if(finished)
                    next();
            }
            else if(step==5) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("Well, come on now! Get over here!",g2d,80,currentTime,1000);
                
                if(backgroundX<0)
                {
                    backgroundX+=10;
                    hero.move(10, 0);
                    if(backgroundX>0)
                        backgroundX = 0;
                }
                if(finished && backgroundX==0) {
                    cutscene = false;
                    bossFight = false;
                    step = 0;
                    text.updateWriteTime(currentTime);
                    progress = "";
                    story++;
                    end = true;
                    audio = new GameAudio("forestTheme");
                    start = true;
                }
            }
        }
        else if(progress.equals("Tutorial3")) //meeting rival
        {
            if(step==0) {
                fileName+=rivalFace;
                boolean finished = text.type("omgomgomg dude the king is CURSED we NEED to go after him and like kill him or-", g2d, 70, currentTime);
                
                if(backgroundX>-1500) {
                    backgroundX-=2;
                    hero.move(-2, 0);
                }
                
                if(finished && backgroundX<=-1500) {
                    next();
                }
            }
            else if(step==1) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Bro.%%%%%%%% Who are you.", g2d, 80, currentTime,1000);
            
                if(finished)
                    next();
            }
            else if(step==2) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer(".%%%%%.%%%%%.%%%%%%%%%HAHAHAHAHAHAHAHAHA that's a good one you really had me there for a sec.", g2d, 70, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==3) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Anyways, let's go somewhere safe, we'll talk more then.", g2d, 70, currentTime,500);
                if(finished) {
                    next();
                    //rival
                    enemies.get(0).setAction('m');
                    enemies.get(0).setFacingRight(true);
                }
            }
            else if(step==4) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Meet me at the village.%% Don't keep me waiting!", g2d, 70, currentTime,1000);
                
                //rival
                if(finished && enemies.get(0).getX()>2750) {
                    step = 0;
                    story++;
                    progress = "";
                    cutscene = false;
                    spawnEnemies(false); //refreshes enemies (boolean doesn't matter)
                    end = true;
                    audio = new GameAudio("forestTheme");
                    start = true;
                }
            }
        }
        else if(progress.equals("Entering the Village"))
        {
            if(step==0) {
                fileName+="oldManFace";
                next();
            }
            else if(step==1) {
                fileName+="oldManFace";
                
                if(backgroundX>-600) {
                    backgroundX-=3;
                    hero.move(-3, 0);
                }
                
                boolean finished = text.typeWithTimer("Why,% hello there young man.% What brings you here to our humble village?", g2d, 100, currentTime,1000);
                if(finished && backgroundX<=-600)
                    next();
            }
            else if(step==2) {
                fileName+=heroFace;
                boolean finished;
                if(hero.isKnight())
                    finished = text.type("Oh,% hello.% I was just following that ar-", g2d, 80, currentTime);
                else
                    finished = text.type("Oh,% hello.% I was just following that kn-", g2d, 80, currentTime);
                if(finished) {
                    next();
                    end = true;
                    audio = new GameAudio("rival");
                    start = true;
                    enemies.get(4).setAction('m');
                }
            }
            else if(step==3) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("FINALLY!", g2d, 70, currentTime,1000);
                if(finished && enemies.get(4).getX()<=1300) {
                    next();
                    enemies.get(4).setAction('i');
                }
            }
            else if(step==4) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Bro,% you were so slow,% I thought you died or something.", g2d, 70, currentTime, 100);
                if(finished)
                    next();
            }
            else if(step==5) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("WAIT.%%%%%% Who are you??%% What am I doing here??", g2d, 80, currentTime, 1000);
                if(finished)
                    next();
            }
            else if (step==6) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Hm...%%%% You really don't remember,% do you.", g2d, 70, currentTime, 750);
                if(finished)
                    next();
            }
            else if (step==7) {
                fileName+=heroFace;
                text.write("b r e h",g2d);
                if(text.getWriteTime()+200<currentTime) {
                    next();
                    end = true;
                    audio = new GameAudio("ominous");
                }
            }
            else if(step==8) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Okay.%%% I'll tell you what happened.", g2d, 70, currentTime, 1000);
                if(finished) {
                    next();
                    start = true;
                }
            }
            else if(step==9) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("There is a king that rules over this land.", g2d, 70, currentTime, 300);
                if(finished)
                    next();
            }
            else if(step==10) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("He has always treated his people with respect.", g2d, 70, currentTime, 300);
                if(finished)
                    next();
            }
            else if(step==11) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Recently,%% however,%% he grew to become more aggressive,% more violent.", g2d, 70, currentTime, 300);
                if(finished)
                    next();
            }
            else if(step==12) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("He became very particular about how things had to be done.", g2d, 70, currentTime, 300);
                if(finished)
                    next();
            }
            else if(step==13) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Before we knew it, he had banished everyone in the kingdom.", g2d, 70, currentTime, 300);
                if(finished)
                    next();
            }
            else if(step==14) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("My name is Emmanuel.", g2d, 70, currentTime, 600);
                if(finished)
                    next();
            }
            else if(step==15) {
                fileName+=rivalFace;
                text.write("lol hi mr. suriel", g2d);
                if(text.getWriteTime()+400<currentTime)
                    next();
            }
            else if(step==16) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("You must've lost your memory,% but I'm your closest friend.", g2d, 70, currentTime, 300);
                if(finished)
                    next();
            }
            else if(step==17) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("It is true.% The king has stopped protecting our village.", g2d, 95, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==18) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("We fear the creatures of the forest may soon overtake us.", g2d, 95, currentTime,1000);
                if(finished) {
                    next();
                    end = true;
                    audio = new GameAudio("villageTheme");
                    start = true;
                }
            }
            else if(step==19) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer(".%%%.%%%.%%%Wow.%%%%%%%%% That was deep.", g2d, 80, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==20) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("So am I supposed to,%%% like,%%% overthrow the king?", g2d, 80, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==21) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Yaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas.", g2d, 58, currentTime, 100);
                if(finished)
                    next();
            }
            else if(step==22) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("OKAY let's go do that.", g2d, 50, currentTime,1000);
                if(finished) {
                    step = 0;
                    story++;
                    progress = "Learn to Interact";
                    text.updateWriteTime(currentTime);
                    cutscene = false;
                    bossFight = true;
                }
            }
        }
        else if(progress.equals("Entering the Jungle")) {
            if(step==0) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("WAIT UP!", g2d, 70, currentTime,1000);
                
                if(finished) {
                    next();
                    hero.setFacingRight(false);
                    start = true;
                }
            }
            else if(step==1) {
                if(enemies.get(0).getX()+backgroundX>=WIDTH/4)
                    enemies.get(0).setAction('i');
                else
                    enemies.get(0).setAction('m');
                
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Wow! Look's like you've gotten a lot stronger.", g2d, 70, currentTime,400);
                if(finished && enemies.get(0).getX()+backgroundX>=WIDTH/4) {
                    enemies.get(0).setAction('i');
                    next();
                }
            }
            else if(step==2) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("You've already gotten through the Ruins!", g2d, 70, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==3) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Haha,%% yeahhh...", g2d, 80, currentTime,600);
                if(finished)
                    next();
            }
            else if(step==4) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Anyways,% make sure to be careful here in the Jungle.", g2d, 70, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==5) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Those flying harpies are no joke.", g2d, 70, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==6) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Here,% let me heal you up.", g2d, 70, currentTime,1000);
                if(finished) {
                    next();
                    hero.fullHeal();
                }
            }
            else if(step==7) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Thankkk youuuu", g2d, 80, currentTime,200);
                if(finished)
                    next();
            }
            else if(step==8) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("And now,% I shall bid you adieu.%%% Good luck on the rest of your adventure!", g2d, 70, currentTime,1000);
                if(finished) {
                    next();
                    hero.fullHeal();
                    Artifact art = new Artifact("Aether Wisp");
                    art.allocateCoordinates(enemies.get(0).getX()+enemies.get(0).getW()/2);
                    drops.add(art);
                }
            }
            else if(step==9) {
                fileName+=heroFace;
                
                enemies.get(0).setAction('m');
                enemies.get(0).setFacingRight(false);
                
                boolean finished = text.typeWithTimer("Alright,% thank-%% Hey,% wait up!% You dropped something!", g2d, 80, currentTime,1000);
                if(finished)
                    next();
            }
            else if(step==10) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("He's already gone...%%%%%%% Hm...", g2d, 80, currentTime,1000);
                if(finished) {
                    next();
                    progress = "";
                    step = 0;
                    cutscene = false;
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                    enemies = new ArrayList<Enemy>();
                    if(!firstSpell) {
                        spawnEnemies(true);
                    }
                    else {
                        bossFight = true;
                    }
                }
            }
            
            //story should be 8 at this point
        }
        else if(progress.equals("The Talking Tree")) {
            if(step==0) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("Hey!%%%% You there!", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==1) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Wha-?%%% Who's there?", g2d, 80, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==2) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("I'm right here!%% Look at the tree in front of you.", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==3) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Yep,% you found me.%% My name is Eric the Elderly Elm.", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==4) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Y-%%%you can talk?%%% But you're...", g2d, 80, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==5) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("A tree.%% Yeah,% you ain't tree-ming,% I'm really here.", g2d, 90, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==6) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Hm...%%% You must be in-tree-gued.", g2d, 90, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==7) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Well,% there's a pine line between trees that talk and trees that don't.", g2d, 90, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==8) {
                fileName+="treeFace";
                boolean finished = text.type("Oh I hope I'm not ar-boring you with my-", g2d, 90, currentTime);
                if(finished) {
                    next();
                }
            }
            else if(step==9) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Okay what do you want.", g2d, 50, currentTime,400);
                if(finished) {
                    next();
                }
            }
            else if(step==10) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("I heard you were heading to the castle from my friends in the Jungle.", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==11) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("What friends?", g2d, 80, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==12) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Walter Birchhold,% Cherry George,% and Shara Brushholly,% of course!", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==13) {
                fileName+=heroFace;
                text.write("they ask you how you are, and you just have to say you're fine when you're not really fine, but you just can't get into it, because they would never understand",g2d,3);
                if(text.getWriteTime()+650<currentTime) {
                    next();
                }
            }
            else if(step==14) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Well,% you're almost at the castle.%% What a re-leaf!", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==15) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("To reach it, you must cross the remains of a battlefield.", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==16) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Take it slow, though.%% You're indis-pine-sable to bring peace back.", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==17) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Let me heal you.", g2d, 90, currentTime,1000);
                if(finished) {
                    next();
                    hero.fullHeal();
                }
            }
            else if(step==18) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("If you ever need to rest,% let me know.", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==19) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Defeat the king,% achieve vic-tree,% and make his-tree.%% I'm rooting for you!", g2d, 90, currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==20) {
                fileName+="treeFace";
                boolean finished = text.typeWithTimer("Oh,% and please refrain from interacting with Ricky the Squirrel.", g2d, 90, currentTime,1000);
                if(finished) {
                    step = 0;
                    cutscene = false;
                    progress = "";
                }
            
            }
        }
        else if(progress.equals("Final Boss")) {
            if(step==0) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("So,%% you've finally made it to the castle.", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==1) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("Hooray!%%% How inspiring.", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==2) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("Now what?%%% You're gonna beat me and save the kingdom?", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==3) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("Let me tell you something, kid.", g2d, 90, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==4) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("This will be your toughest battle yet.%%% I will not flinch.", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==5) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("To defeat me,% you must do more than just rattle my bones.", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==6) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("You must shatter my spirit!", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                    end = true;
                    audio = new GameAudio("finalBoss");
                }
            }
            else if(step==7) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("If not...", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                    start = true;
                }
            }
            else if(step==8) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("You're gonna have a bad time.", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==9) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer("Now, it's SHOW TIME.", g2d, 95, currentTime,800);
                if(finished) {
                    next();
                    step = 0;
                    cutscene = false;
                    progress = "";
                    ((King)enemies.get(0)).itsShowTime();
                }
            }
        }
        
        else if(progress.equals("Victory")) {
            if(step==0) {
                fileName+="kingFace";
                boolean finished = text.typeWithTimer(".%.%.%%%%%Well done,% kid.",g2d,110,currentTime,1500);
                if(finished) {
                    next();
                    start = true;
                    spawnRival(2500,true);
                    enemies.add(new VillagerMaleElder(2600,true));
                    enemies.add(new VillagerFemaleAdult(2450,true));
                    if(foundEasterEgg)
                        enemies.add(new VillagerMaleAdult(2300,true));
                    enemies.add(new VillagerSquirrel(2150,true));
                    hero.freeze(false);
                }
            }
            else if(step==1) {
                fileName+=rivalFace;
                if(enemies.get(0).getX()>3200)
                    enemies.get(0).setAction('i');
                else
                    enemies.get(0).setAction('m');
                boolean finished = text.typeWithTimer("Hey!!!",g2d,75,currentTime,1000);
                if(finished && enemies.get(0).getX()>3200) {
                    next();
                }
            }
            else if(step==2) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("We heard a ton of racket coming from the castle so we came to help!",g2d,75,currentTime,500);
                if(finished) {
                    next();
                }
            }
            else if(step==3) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("We?",g2d,85,currentTime,800);
                if(finished) {
                    next();
                }
            }
            else if(step==4) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Yeah! We all came for you!",g2d,75,currentTime,500);
                if(backgroundX<-2100) {
                    backgroundX+=4;
                    hero.move(4,0);
                }
                if(finished) {
                    next();
                }
            }
            else if(step==5) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Looks like you were able to defeat the king on your own,% though,% haha.",g2d,75,currentTime,500);
                if(backgroundX<-2100) {
                    backgroundX+=4;
                    hero.move(4,0);
                }
                if(finished) {
                    next();
                }
            }
            else if(step==6) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("Thank you,% brave hero!%%% You have saved us all!",g2d,105,currentTime,800);
                if(finished) {
                    step = 0;
                    progress = "";
                    cutscene = false;
                    text.updateWriteTime(currentTime);
                    beatTheGame = true;
                    saveGame();
                    twoPlayers = false;
                    backgroundX = 0;
                    hero.setX(0);
                    map = new Map("Forest");
                    spawnEnemies(true);
                }
            }
        }
        else if(progress.equals("Rumbling From The Well")) {
            if(step==0) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("Ah!%% Greetings brave hero!%% We're glad you're back.%%", g2d, 95, currentTime, 300);
                if(finished) {
                    hero.turnAround();
                    next();
                }
            }
            else if(step==1) {
                fileName+="nurseFace";
                boolean finished = text.typeWithTimer("That fight with the King must have been tough.%% Thank you again for saving us!%%", g2d, 90, currentTime,300);
                if(finished) {
                    hero.turnAround();
                    next();
                }
            }
            else if(step==2) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("There is a...%%% well...%%% another tiny dilemma...%%%%% There's a-", g2d, 95, currentTime, 40);
                if(finished) {
                    next();
                    end = true;
                }
            }
            else if(step==3) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("R U M B L E", g2d, 200, currentTime,1000);
                if(finished) {
                    next();
                    start = true;
                }
            } 
           else if(step==4) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer(".%%%%.%%%%.%%%%that.%%%%% The sound appears to be coming from the well.%%", g2d, 95, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==5) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("Emmanuel went in to investigate,% but he hasn't returned.%%.%%.%%%%", g2d, 95, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==6) {
                fileName+="oldManFace";
                boolean finished = text.typeWithTimer("Do you think you could perhaps check up on him?%%", g2d, 95, currentTime,300);
                if(finished) {
                    hero.turnAround();
                    next();
                }
            }
            else if(step==7) {
                fileName+="nurseFace";
                boolean finished = text.typeWithTimer("We'll be here supporting you all the way!", g2d, 90, currentTime,1000);
                if(finished) {
                    cutscene = false;
                    step = 0;
                    progress = "";
                }
            }
        }
        else if(progress.equals("A Dangerous Path")) {
            if(step==0) {
                if(enemies.get(0).getX() < 1780)
                    enemies.get(0).setAction('i');
                else
                    enemies.get(0).setAction('m');
                
                if(backgroundX>-1000) {
                    backgroundX-=3;
                    hero.move(-3, 0);
                }
                
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("omg omg omg omg OMG OMG OMG OMG-! Yo.%%%%%%%%%%%%%%% Don't go in there.%%%%%%%%%",g2d,75,currentTime,300);
                if(finished)
                    next();
            }
            else if(step==1) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("...%%%%%%%%why not?",g2d,80,currentTime,300);
                if(finished)
                    next();
            }
            else if(step==2) {
                fileName+=rivalFace;
                text.changeY(12);
                text.write("I ain't never seen a more horrendous sight. On the night of December 3rd, 2006, I was witness to a heinous act of human cruelty. I saw a man take another man's life in front of a Whole Foods in Calabasas, California.",g2d,3);
                if(text.getWriteTime()+650 < currentTime)
                    next();
            }
            else if(step==3) {
                fileName+=rivalFace;
                text.changeY(WRITEY);
                boolean finished = text.typeWithTimer("There's this HUGE Sandworm back there.%% Biggest one I've ever seen.%%",g2d,80,currentTime,300);
                if(finished)
                    next();
            }
            else if(step==4) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("I barely made it out of there alive.%%",g2d,80,currentTime,300);
                if(finished)
                    next();
            }
            else if(step==5) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("But...%%%%%%%%%%%% What about the Village?%%",g2d,85,currentTime,300);
                if(finished)
                    next();
            }
            else if(step==6) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer(".%%%%%%%.%%%%%%%.%%%%%%%%%", g2d, 80, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==7) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("I have to go in there.%% We need to protect the Villagers!%%",g2d,85,currentTime,400);
                if(finished)
                    next();
            }
            else if(step==8) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Well...%%%%%%%%%% alright.%% I'll be here waiting for your return.%%", g2d, 80, currentTime,300);
                if(finished)
                    next();
            }
            else if(step==9) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("If you'd like to resurface,% let me know.%% I'll help you up the well.%%", g2d, 80, currentTime,1000);
                if(finished) {
                    cutscene = false;
                    step = 0;
                    progress = "";
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                }
            }
        }
        else if(progress.equals("Demon Underground")) {
            if(step==0) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("R U M B L E", g2d, 200, currentTime,700);
                
                if(backgroundX>map.getLimit()) {
                    backgroundX-=2;
                    hero.move(-2,0);
                }
                
                if(finished) {
                    next();
                }
            }
            else if(step==1) {
                fileName+=heroFace;
                boolean finished = text.type("?!-", g2d, 100, currentTime);
                if(finished) {
                    next();
                }
            }
            else if(step==2) {
                fileName+="sandwormFace";
                boolean finished = text.typeWithTimer("RRAAAUUUGGHHH!!!", g2d, 50, currentTime,200);
                if(finished) {
                    step = 0;
                    cutscene = false;
                    audio = new GameAudio("vsWorm");
                    start = true;
                    progress = "";
                }
            }
        }
        else if(progress.equals("Defeated the Sandworm")) {
            if(step==0) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("YOOOOO POGS IN THE CHAT MY MANS REALLY JUST BEAT THE SANDWORM!!%% :O%%", g2d, 80, currentTime, 300);
                
                //2400 - midX
                Enemy rival = enemies.get(0);
                int randX = (int)(Math.random()*(WIDTH-rival.getW()))-backgroundX;
                int randY = (int)(Math.random()*(HEIGHT-rival.getH()));
                rival.setX(randX);
                rival.setY(randY);
                
                //scroll
                if(backgroundX<-2200) {
                    backgroundX+=2;
                    hero.move(2,0);
                }
                
                if(finished) {
                    next();
                    rival.setFacingRight(true);
                    rival.setX(2350);
                    rival.setY(rival.getFLOORY()-rival.getH());
                }
            }
            else if(step==1) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("You know,% I always knew you could do it.%% :D%%%%", g2d, 80, currentTime, 300);
                if(finished) {
                    next();
                }
            }
            else if(step==2) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("clickclackclickclackclickclack", g2d, 60, currentTime, 200);
                if(finished) {
                    next();
                }
            }
            else if(step==3) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("...%%%%%%%%%what is that noise??%%", g2d, 80, currentTime, 300);
                if(finished) {
                    enemies.add(new VillagerCrab(2640,true,true)); //dj
                    for(int i=0; i<10; i++)
                        enemies.add(new VillagerCrab(2100+i*120,i%2==0));
                    next();
                }
            }
            else if(step==4) {
                fileName+="questionMark";
                boolean finished = text.typeWithTimer("clickclackclickclackclickclack", g2d, 60, currentTime, 200);
                
                if(finished) {
                    next();
                }
            }
            else if(step==5) {
                fileName+="crabFace";
                boolean finished = text.typeWithTimer("Thank you for freeing us from the Sandworm's control!!!!!%% owo%%%%", g2d, 60, currentTime, 200);
                
                if(finished)
                    next();
            }
            else if(step==6) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Oh,% um...%%%%%% sure,% no problem.%%", g2d, 85, currentTime, 300);
                
                if(finished)
                    next();
            }
            else if(step==7) {
                fileName+="crabFace";
                boolean finished = text.typeWithTimer("Now we can vibe in peace again!!!!!%% Thank you uwu.%%%%", g2d, 60, currentTime, 200);
                
                if(finished)
                    next();
            }
            else if(step==8) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer(".%%%%%%%%%%.%%%%%%%%%%.%%%%%%%%%%%%%%%nice.%%", g2d, 85, currentTime, 1000);
                
                if(finished) {
                    progress = "Thanking the Player";
                    enemies.get(0).turnAround();
                    enemies.get(0).setAction('m');
                    text.updateWriteTime(currentTime);
                    step = 0;
                    cutscene = false;
                }
            }
        }
        
        
        //INTERACTIONS WITH NPCS
        else if(progress.equals("Talk to Old Man")) {
            //first interaction
            if(story==6 && enemies.get(2).getExchanges()==0) {
                if(step==0) {
                    fileName+=heroFace;
                    boolean finished = text.typeWithTimer("I'm sorry for the unfortunate circumstaces.", g2d, 80, currentTime,240);
                    if(finished)
                        next();
                }
                else if(step==1) {
                    fileName+=heroFace;
                    boolean finished = text.typeWithTimer("It must be difficult getting by with such few people.", g2d, 80, currentTime,500);
                    if(finished)
                        next();
                }
                else if(step==2) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("Oh,% don't worry about us.%% We'll manage.", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                }
                else if(step==3) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("Our nurse does a good job keeping us healthy!", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                } 
               else if(step==4) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("You know,% I've heard rumors of a Moss Dragon that resides in the Deep Forest.", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                }
                else if(step==5) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("If you're interested,% keep going left.% Just be careful.", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                }
                else if(step==6) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("Otherwise,% the path to the king's castle is rightwards.%% Good luck!", g2d, 95, currentTime,1000);
                    if(finished) {
                        cutscene = false;
                        step = 0;
                        progress = "";
                        enemies.get(2).incrementExchanges();
                    }
                }
            }
            else if(!aliveBosses[1]) { //postgame
                if(aliveBosses[2]) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("Oh,% please do help Emmanuel!% Save him,% and save our Village!%", g2d, 95, currentTime,1000);
                    if(finished) {
                        cutscene = false;
                        step = 0;
                        progress = "";
                    }
                }
                else {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("Man.%%% I'm old.%", g2d, 95, currentTime,1000);
                    if(finished) {
                        cutscene = false;
                        step = 0;
                        progress = "";
                    }
                }
            }
            else {
                if(step==0) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("I've heard rumors of a Moss Dragon that resides in the Deep Forest.", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                }
                else if(step==1) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("If you're interested,% keep going left.% Just be careful.", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                }
                else if(step==2) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("There also seems to be something wrong with our well...%%%", g2d, 95, currentTime,300);
                    if(finished)
                        next();
                }
                else if(step==3) {
                    fileName+="oldManFace";
                    boolean finished = text.typeWithTimer("Anyways,% the path to the king's castle is rightwards.%% Good luck!", g2d, 95, currentTime,1000);
                    if(finished) {
                        cutscene = false;
                        step = 0;
                        progress = "";
                    }
                }
            }
        }
        else if(progress.equals("Talk to Nurse")) {
            //first interaction
            if(story==6 && enemies.get(3).getExchanges()==0) {
                if(step==0) {
                    fileName+="nurseFace";
                    boolean finished = text.typeWithTimer("Oh!%%% You must be the valiant hero Emmanuel spoke of!", g2d, 90, currentTime,270);
                    if(finished)
                        next();
                }
                else if(step==1) {
                    fileName+="nurseFace";
                    boolean finished = text.typeWithTimer("I'm Kim,% the village nurse.", g2d, 90, currentTime,540);
                    if(finished)
                        next();
                }
                else if(step==2) {
                    fileName+=heroFace;
                    boolean finished = text.typeWithTimer("Nice to meet you.%% My name is%%%%%%%%%%%%.%%%%%.%%%%%.%%%%%", g2d, 80, currentTime,240);
                    if(finished)
                        next();
                }
                else if(step==3) {
                    fileName+=heroFace;
                    text.write("wait wtf is my name",g2d);
                    if(text.getWriteTime()+490<currentTime)
                        next();
                }
                else if(step==4) {
                    fileName+="nurseFace";
                    boolean finished = text.typeWithTimer("Ah,% it's a pleasure.", g2d, 90, currentTime,270);
                    if(finished)
                        next();
                }
                else if(step==5) {
                    fileName+="nurseFace";
                    boolean finished = text.typeWithTimer("Here,% let me heal you up.", g2d, 90, currentTime,1000);
                    if(finished)
                        next();
                }
                else if(step==6) {
                    fileName+="nurseFace";
                    boolean finished = text.typeWithTimer("If you ever need to get back on your feet,% let me know!", g2d, 90, currentTime,540);
                    if(finished)
                        next();
                }
                else if(step==7) {
                    fileName+=heroFace;
                    boolean finished = text.typeWithTimer("Thanks.", g2d, 80, currentTime,500);
                    if(finished) {
                        cutscene = false;
                        hero.fullHeal();
                        enemies.get(3).incrementExchanges();
                        
                        progress = "Learn to Press H";
                        text.updateWriteTime(currentTime);
                        step = 0;
                        bossFight = true;
                    }
                } 
            }
            else {
                //already fully healed
                if(hero.getLife()==hero.getMaxLife() && hero.getMana()==hero.getMaxMana()) {
                    if(step==0) {
                        fileName+="nurseFace";
                        boolean finished = text.typeWithTimer("Dude,% you're at full health.", g2d, 90, currentTime,500);
                        if(finished)
                            next();
                    }
                    else if(step==1) {
                        fileName+=heroFace;
                        boolean finished = text.typeWithTimer("My bad.", g2d, 90, currentTime,500);
                        if(finished) {
                            step = 0;
                            progress = "";
                            cutscene = false;
                        }
                    }
                }
                else {
                    if(step==0) {
                        fileName+="nurseFace";
                        boolean finished = text.typeWithTimer("Here,% let me heal you up.", g2d, 90, currentTime,1000);
                        if(finished)
                            next();
                    }
                    else if(step==1) {
                        fileName+=heroFace;
                        boolean finished = text.typeWithTimer("Thanks.", g2d, 90, currentTime,500);
                        if(finished) {
                            step = 0;
                            progress = "";
                            cutscene = false;
                            hero.fullHeal();
                        }
                    }
                }
            }
        }
        else if(progress.equals("Entering the Well")) {
            if(step==0) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Hmm...%%%%% I really want to jump into this well...%%%%%%%", g2d, 80, currentTime,270);
                if(finished)
                    next();
            }
            else if(step==1) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("Okay yeah I'm getting into this well.", g2d, 70, currentTime,500);
                if(finished) {
                    hero.setX(500);
                    friend.setX(500);
                    hero.setFacingRight(true);
                    map = new Map("Cave");
                    
                    backgroundX = 0;
                    spawnEnemies(true);
                    
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                
                    writeMapName = true;
                    text.updateWriteTime(currentTime);
                    
                    step = 0;
                    cutscene = false;
                    progress = "";
                }
            }
        }
        else if(progress.equals("Talk to Tree")) {
            //already fully healed
            if(hero.getLife()==hero.getMaxLife() && hero.getMana()==hero.getMaxMana()) {
                if(step==0) {
                    fileName+="treeFace";
                    boolean finished = text.typeWithTimer("There's no use in tree-ting the able.", g2d, 90, currentTime,500);
                    if(finished)
                        next();
                }
                else if(step==1) {
                    fileName+=heroFace;
                    boolean finished = text.typeWithTimer("...%%%%%Right.", g2d, 90, currentTime,500);
                    if(finished) {
                        step = 0;
                        progress = "";
                        cutscene = false;
                    }
                }
            }
            else {
                if(step==0) {
                    fileName+="treeFace";
                    boolean finished = text.typeWithTimer("Allow me to amp-leaf-y your essence.", g2d, 90, currentTime,1000);
                    if(finished)
                        next();
                }
                else if(step==1) {
                    fileName+=heroFace;
                    boolean finished = text.typeWithTimer("Thanks.", g2d, 90, currentTime,500);
                    if(finished) {
                        step = 0;
                        progress = "";
                        cutscene = false;
                        hero.fullHeal();
                    }
                }
            }
        }
        else if(progress.equals("Man in a Cave")) {
            if(step==0) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("Hello?%%%%% OH MY GOODNESS-!%%%%% FINALLYYYYY!!!", g2d, 85, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==1) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("You don't know how long I've been waiting for help.", g2d, 85, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==2) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("lmao what are you doing down here", g2d, 35, currentTime,200);
                if(finished)
                    next();
            }
            else if(step==3) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("Oh-!%%% Allow my to introduce myself.% My name is Daniel.% Daniel Salamon.", g2d, 85, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==4) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("I was trying to calculate how deep this well is.", g2d, 85, currentTime,255);
                if(finished)
                    next();
            }
            else if(step==5) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("I would determine this by seeing how long it took a rock to fall to the floor.", g2d, 85, currentTime,255);
                if(finished)
                    next();
            }
            else if(step==6) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("I was just about to determine the depth, when suddenly...!", g2d, 85, currentTime,255);
                if(finished)
                    next();
            }
            else if(step==7) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("I slipped on a frictionless surface and fell into this well.", g2d, 85, currentTime,255);
                if(finished)
                    next();
            }
            else if(step==8) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("And let me tell you,% my kinetic energy was DEFINITELY greater than the rock.", g2d, 85, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==9) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("Regardless,% now I can finally get out of this cave!", g2d, 85, currentTime,255);
                if(finished)
                    next();
            }
            else if(step==10) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("I know this teleportation spell but it takes two people.", g2d, 85, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==11) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("Okay,% come closer now.", g2d, 85, currentTime,500);
                if(backgroundX>-640)
                    hero.setRight(true);
                else
                    hero.setRight(false);
                if(finished && backgroundX<=-640) {
                    next();
                    hero.setAction('i');
                }
            }
            else if(step==12) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("Ready?", g2d, 85, currentTime,500);
                if(finished) 
                    next();
            }
            else if(step==13) {
                fileName+="manFace";
                boolean finished = text.type("3...%%%%%% 2...%%%%%%% 1...%%%%%%%%% GO!!!", g2d, 85, currentTime);
                if(finished) {
                    map = new Map("Village");
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                    next();
                    spawnEnemies(true);
                    backgroundX = -1300;
                }
            }
            else if(step==14) {
                fileName+=heroFace;
                boolean finished = text.typeWithTimer("%%%%%%%%%%%.%%%%.%%%%.%%%%damn that actually worked?", g2d, 80, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==15) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("HONEY I missed you so much!!!", g2d, 85, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==16) {
                fileName+="nurseFace";
                boolean finished = text.typeWithTimer("What???%% You were only gone for like an hour.", g2d, 90, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==17) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("O-%%Oh.%%%%%% Well,% anyways,% THANK YOU young adventurer.", g2d, 85, currentTime,255);
                if(finished)
                    next();
            }
            else if(step==18) {
                fileName+="manFace";
                boolean finished = text.typeWithTimer("You saved me.%% Here's a reward for your troubles.", g2d, 85, currentTime,255);
                if(finished) {
                    Consumable c1 = new Consumable("Emerald");
                    Consumable c2 = new Consumable("Aquamarine");
                    Consumable c3 = new Consumable("Garnet");
                    Consumable c4 = new Consumable("Citrine");
                    c1.allocateCoordinates(2236);
                    c2.allocateCoordinates(2272);
                    c3.allocateCoordinates(2308);
                    c4.allocateCoordinates(2344);
                    drops.add(c1);
                    drops.add(c2);
                    drops.add(c3);
                    drops.add(c4);
                    
                    step = 0;
                    progress = "";
                    cutscene = false;
                }
            }
        }
        if(progress.equals("Resurface")) {
            if(step==0) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Oh,% you want to go back to the surface?%%% Sure thing.%%", g2d, 80, currentTime,500);
                if(finished)
                    next();
            }
            else if(step==1) {
                fileName+=rivalFace;
                boolean finished = text.typeWithTimer("Ready?%%%%%%%%% 3...%%%%% 2...%%%%%%% 1...%%%%%%%%% Y E E T", g2d, 80, currentTime,80);
                if(finished) {
                    map = new Map("Village");
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                    spawnEnemies(true);
                    backgroundX = -1300;
                    hero.setFacingRight(true);
                    hero.setX(550);
                    
                    step = 0;
                    progress = "";
                    cutscene = false;
                }
            }
        }
        
        Image face = new ImageIcon(this.getClass().getResource(fileName+".png")).getImage();
        g2d.drawImage(face,24,24,96,96,null); //13,13 (w,h)
    }
    
    //once someone finishes talking and someone else is going to talk, call this method
    public void next() {
        step++;
        text.updateWriteTime(currentTime);
    }
    
    public void moreText(Graphics2D g2d)
    {
        if(progress.equals("Tutorial"))
        {
            text.changeY(hero.getY()-40);

            if(step==0)
            {
                text.changeX(hero.getX()-70);
                boolean finished = text.typeWithTimer("Use AD to move.",g2d,100,currentTime,2000);

                if(finished)
                    next();
            }
            else if(step==1)
            {
                text.changeX(hero.getX()-110);
                boolean finished = text.type("Press SPACE to jump.",g2d,100,currentTime);

                if(finished && backgroundY!=0)
                    next();
            }
            else if(step==2) {
                text.changeX(hero.getX()-110);
                boolean finished = text.type("Press SHIFT to dash.",g2d,100,currentTime);

                if(finished && hero.getDashTime() + hero.getDashDuration() > currentTime)
                    next();
            }
            else if(step==3)
            {
                text.changeX(hero.getX()-85);
                boolean finished = text.type("Press O to attack.",g2d,100,currentTime);

                if(finished && hero.getAction()=='a')
                    next();
            }
            else if(step==4)
            {
                text.changeX(hero.getX()-250);

                boolean finished = text.type("Hold O to perform multiple attacks.",g2d,60,currentTime);

                if(finished && (hero.getAction()=='b' || hero.getAction()=='c')) {
                    step++;
                    text.updateWriteTime(currentTime);
                }
            }
            else if(step==5)
            {
                text.changeX(hero.getX()-60);

                boolean finished;
                if(hero.isKnight())
                    finished = text.type("Hold P to shield.",g2d,100,currentTime);
                else
                    finished = text.type("Hold P to shoot.",g2d,100,currentTime);
                
                if(finished && hero.getAction()=='s') {
                    step++;
                    text.updateWriteTime(currentTime);
                }
            }
            else if(step==6)
            {
                text.changeX(hero.getX()-50);

                boolean finished;
                finished = text.type("Press S to roll.",g2d,100,currentTime);
                if(finished && hero.getAction()=='r') {
                    next();
                }
            }
            else if(step==7)
            {
                text.changeX(hero.getX()-250);

                boolean finished = text.type("Press Q to toggle your inventory.",g2d,50,currentTime);

                if(finished && inventory.isVisible()) {
                    next();
                }
            }
            else if(step==8) {
                text.reassignCoordinates(20,100);

                boolean finished = text.type("Press LEFT, RIGHT to navigate your inventory.",g2d,50,currentTime);

                if(finished && inventory.getPage()!=2) {
                    step++;
                }
            }
            else if(step==9)
            {
                if(!inventory.isVisible()) {
                    step++;
                    text.updateWriteTime(currentTime);
                }
            }
            else if(step==10)
            {
                text.reassignCoordinates(50,100);
                boolean finished = text.typeWithTimer("The two bars at the top left of your screen represent your health and mana.",g2d,40,currentTime,2000);

                if(finished) {
                    step++;
                    text.updateWriteTime(currentTime);
                }
            }
            else if(step==11)
            {
                text.reassignCoordinates(50,100);
                boolean finished = text.type("You're ready to embark on your adventure now! Just one more thing before-",g2d,40,currentTime);
                if(finished) {
                    step = 0;
                    inventory.close();
                    cutscene = true;
                    progress = "Tutorial2";
                    backgroundY = 0;
                    hero.freeze(true);
                    text.updateWriteTime(currentTime);
                }
            }
        }
        else if(story==1 && backgroundX==0)
        {
            text.reassignCoordinates(hero.getX()-100,hero.getY()-40);
            text.write("Move to the right.", g2d);
        }
        else if(progress.equals("Learn to Interact"))
        {
            //text.reassignCoordinates(hero.getX()-160,hero.getY()-40);
            if(step==0) {
                text.reassignCoordinates(50,100);
                boolean finished = text.type("Press ENTER to interact.", g2d, 100, currentTime);
                if(finished)
                    next();
            }
            else {
                text.reassignCoordinates(50,100);
                text.write("Press ENTER to interact.", g2d);
                text.reassignCoordinates(50,155);
                boolean finished = text.typeWithTimer("Tap ENTER 3-4 times while interacting to skip dialogue.", g2d, 100, currentTime, 2000);

                if(finished) {
                    step = 0;
                    progress = "";
                    bossFight = false;
                }
            }
        }
        else if(progress.equals("Learn to Press H")) {
            text.reassignCoordinates(50, 100);
            boolean finished = text.typeWithTimer("TIP: Press H to automatically consume potions.", g2d, 100, currentTime, 2000);
            if(finished) {
                progress = "";
                bossFight = false;
            }
        }
        else if(progress.equals("First Spell"))
        {
            text.reassignCoordinates(50,100);
            if(step==0) {
                boolean finished = text.typeWithTimer("You have just recieved an artifact capable of casting a spell! These spells expend mana but are helpful additions to your arsenal.",g2d,80,currentTime,1000);
                if(finished)
                    next();
            }
            else if(step==1) {
                boolean finished = text.typeWithTimer("Press 1, 2, 3, and 4 to activate spells. If you have an artifact equipped that is capable of casting a spell and have enough mana the spell will cast.",g2d,80,currentTime,1000);
                if(finished)
                    next();
            }
            else if(step==2) {
                boolean finished = text.typeWithTimer("Press 1 to cast a spell from the artifact in your first equipment slot, 2 for your second equipment slot, etc.",g2d,80,currentTime,1000);
                if(finished) {
                    step = 0;
                    progress = "";
                    bossFight = false;
                    if(map.getName().equals("Jungle") && story==8) {
                        saveGame();
                        spawnEnemies(true);
                    }
                }
            }
        }
        else if(progress.equals("Thanking the Player")) {
            text.reassignCoordinates(50,100);
            boolean finished = text.typeWithTimer("You have officially completed Verdure.%%% I hope you've enjoyed your playthrough!%%% Thank you the time you've put into my game.%%% Wishing you all the best!%%% :)%%%%% - Vadim Allayev",g2d,90,currentTime,1000);
            if(finished) {
                progress = "";
                bossFight = false;
            }
        }
    }
    
    public void movement() {
        int speed = hero.getMobility();
        if(hero.getDashTime() + hero.getDashDuration() > currentTime)
            speed = hero.getDashSpeed(currentTime);
        
        if(hero.getAction()=='m') {
            if(hero.isLeft()) {
                if(backgroundX<0 && hero.getX()<WIDTH/4) {
                    backgroundX+=speed;
                    if(backgroundX>0)
                        backgroundX = 0;
                }
                else
                    hero.move(-speed,0);
            }
            if(hero.isRight()) {
                if(backgroundX>map.getLimit() && hero.getX()+hero.getW()>WIDTH/4*3)
                    backgroundX-=speed;
                else
                    hero.move(speed,0);
            }
        }
        else if(hero.getAction()=='j')
        {
            if(hero.isFacingRight() && hero.isRight())
            {
                if(backgroundX>map.getLimit() && hero.getX()+hero.getW()>WIDTH/4*3)
                    backgroundX-=speed;
                else
                    hero.move(speed,0);
            }
            else if(!hero.isFacingRight() && hero.isLeft())
            {
                if(backgroundX<0 && hero.getX()<WIDTH/4) {
                    backgroundX+=speed;
                    if(backgroundX>0)
                        backgroundX = 0;
                }
                else
                    hero.move(-speed,0);
            }
            
            double x = ((double)currentTime-hero.getAnimationTime())/100;
            int intercept = 7;
            if(!hero.isKnight())
                intercept = 6;
            int velocity = (int)(-2*x+intercept);
            backgroundY+=velocity;
            if(backgroundY<0)
                backgroundY=0;
        }
        else if(hero.getAction()=='r')
        {
            backgroundY = 0; //you can roll from jump, so this is to make sure the background resets
            
            if(hero.isFacingRight())
            {
                if(backgroundX>map.getLimit() && hero.getX()+hero.getW()>WIDTH/4*3)
                    backgroundX-=speed;
                else
                    hero.move(speed,0);
            }
            else //facing left
            {
                if(backgroundX<0 && hero.getX()<WIDTH/4) {
                    backgroundX+=speed;
                    if(backgroundX>0)
                        backgroundX = 0;
                }
                else
                    hero.move(-speed,0);
            }
        }
    }
    
    public void movementBossFight()
    {
        int speed = hero.getMobility();
        if(hero.getDashTime() + 750 > currentTime)
            speed = hero.getDashSpeed(currentTime);
        
        if(hero.getAction()=='m')
        {
            if(hero.isLeft()) {
                if(hero.getX()-speed>0)
                    hero.move(-speed,0);
            }
            if(hero.isRight()) {
                if(hero.getX()+hero.getW()+speed<WIDTH)
                    hero.move(speed,0);
            }
        }
        else if(hero.getAction()=='j')
        {
            if(hero.isFacingRight() && hero.isRight()) {
                if(hero.getX()+hero.getW()+speed<WIDTH)
                    hero.move(speed,0);
            }
            else if(!hero.isFacingRight() && hero.isLeft()) {
                if(hero.getX()-speed>0)
                    hero.move(-speed,0);
            }
            
            double x = ((double)currentTime-hero.getAnimationTime())/100;
            int intercept = 7;
            if(!hero.isKnight())
                intercept = 6;
            int velocity = (int)(-2*x+intercept);
            backgroundY+=velocity;
            if(backgroundY<0)
                backgroundY=0;
        }
        else if(hero.getAction()=='r')
        {
            //backgroundY = 0; //you can roll from jump, so this is to make sure
                                //the background resets
            
            if(hero.isFacingRight()) {
                if(hero.getX()+hero.getW()+speed<WIDTH)
                    hero.move(speed,0);
            }
            else {
                if(hero.getX()-speed>0)
                    hero.move(-speed,0);
            }
        }
    }
    
    
    //These methods are required by the compiler.  
    //You might write code in these methods depending on your goal.
    public void keyTyped(KeyEvent e) 
    {
    }
    public void keyReleased(KeyEvent e) 
    {
        int key = e.getKeyCode();

        if(map.getName().equals("Menu")) {
            if(key==10) { //enter 
                if(step==0 && backgroundX>=250) { //initial screen                 
                    //LOADING THE GAME
                    try {
                        //The destination to save the saved object file to
                        FileInputStream file = new FileInputStream("Storage.obj");

                        //ObjectInputStream is responisible for writing the byte file
                        ObjectInputStream inputStream = new ObjectInputStream(file);

                        //Trying to read the object, if the exception is thrown that means
                        //There isn't a previous saved object then is fine, just move on
                        //if there is then good we load in what is saved from the last
                        //session
                        storage = (Storage)inputStream.readObject();
                        step = 11;
                    }
                    catch(Exception exception) {
                        step = 1;
                    }
                }
            }
            else if(key==78) { //N (new game, no tutorial)
                if(step==1 || step==11) {
                    if(!storage.isGameSaved()) {
                        hero = new Knight();
                        newGameWithTutorial();
                    }
                    else
                        step++;
                }
               else if(step==3 || step==13) {
                    newGameNoTutorial();
                }
            }
            else if(key==84) {//T (i've played so many times already)
                //you must have already beaten the game to play this gamemode
                if((step==3 || step==13) && (!storage.getAliveBosses()[1] || storage.getStory()==100)) {
                    newGamePlayedAlready();
                }
            }
            else if(key==67) { //C (continue)
                if(step==11) {
                    //LOADING THE GAME
                    try {
                        //The destination to save the saved object file to
                        FileInputStream file = new FileInputStream("Storage.obj");

                        //ObjectInputStream is responisible for writing the byte file
                        ObjectInputStream inputStream = new ObjectInputStream(file);

                        //Trying to read the object, if the exception is thrown that means
                        //There isn't a previous saved object then is fine, just move on
                        //if there is then good we load in what is saved from the last session
                        storage = (Storage)inputStream.readObject();
                        backgroundX = storage.getBackgroundX();
                        hero = storage.getHero();
                        hero.land();
                        friend = storage.getFriend();
                        friend.setAnimationTime(0);
                        friend.setHitTime(0);
                        friend.resetProjectiles();
                        map = storage.getMap();
                        inventory = storage.getInventory();
                        story = storage.getStory();
                        foundEasterEgg = storage.isFoundEasterEgg();
                        firstSpell = storage.isFirstSpell();
                        aliveBosses = storage.getAliveBosses();
                        twoPlayers = storage.getTwoPlayers();
                        discovered = storage.getDiscovered();
                        
                        end = true;
                        audio = new GameAudio(map.getSong());
                        start = true;
                        backgroundY = 0;
                        step = 0;
                        text.updateWriteTime(currentTime);
                        progress = "";
                        bossFight = false;
                        cutscene = false;
                        hero.reset(currentTime);
                        
                        if(backgroundX==0) {
                            spawnEnemies(true);
                            campX = 125;
                        }
                        else {
                            spawnEnemies(false);
                            campX = -map.getLimit()+WIDTH-150;
                        }
                        
                        //who is who
                        if(hero.isKnight()) {
                            heroFace = "knightFace";
                            rivalFace = "archerFace";
                        }
                        else {
                            heroFace = "archerFace";
                            rivalFace = "knightFace";
                        }
                    }
                    catch(Exception ex) {
                        step = 0;
                    }
                }
            }
            else if(key==27) { //ESC (back)
                if(step==1 || step==11)
                    step = 0;
                else if(step==2 || step==12 || step==3 || step==13)
                    step--;
            }
            else if(key==89) { //Y (yes)
                if(step==3 || step==13) {
                    newGameWithTutorial();
                }
            }
            else if(key==75) { //K (knight)
                if(step==2 || step==12) {
                    hero = new Knight();
                    step++;
                }
            }
            else if(key==65) { //A (archer)
                if(step==2 || step==12) {
                    hero = new Archer();
                    step++;
                }
            }
        }
        else if(beatTheGame) {
            if(step==1) { //text has finished writing
                if(key==71) //G - return to game
                {
                    map = new Map("Castle");
                    beatTheGame = false;
                    backgroundX = map.getLimit();
                    hero.setX(WIDTH/4*3);
                    hero.setFacingRight(false);
                    bossFight = false;
                    hero.fullHeal();
                    hero.setAction('i');
                    
                    twoPlayers = storage.getTwoPlayers();
                    friend = storage.getFriend();
                    friend.setX(WIDTH/2-backgroundX);
                    friend.setLife(friend.getMaxLife());
                    
                    step = 0;
                    
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                    
                    enemies = new ArrayList<Enemy>();
                    Artifact a1 = new Artifact("Celestial Staff");
                    a1.allocateCoordinates(3200);
                    Artifact a2 = new Artifact("Locket of a Fallen King");
                    a2.allocateCoordinates(3236);
                    drops.add(a1);
                    drops.add(a2);
                    
                    saveGame();
                }
                else if(key==77) //M - go to menu
                {
                    hero.fullHeal();
                    hero.setFacingRight(true);
                    hero.setX(0);
                    hero.fullHeal();
                    hero.setAction('i');
                    beatTheGame = false;
                    step = 0;
                    backgroundX = 0;
                    map = new Map("Clearing");
                    
                    twoPlayers = storage.getTwoPlayers();
                    friend = storage.getFriend();
                    friend.setLife(friend.getMaxLife());
                    friend.setX(0);
                    
                    saveGame();
                    
                    map = new Map("Menu");
                    
                    end = true;
                    audio = new GameAudio(map.getSong());
                    start = true;
                }
                else if(key==81) //Q - quit
                {
                    System.exit(0);
                }
            }
        }
        else if(cutscene) {
            if(key==10) { //enter - skip cutscene
                if(skipTime+2000<currentTime) {
                    if(skipTime==0) {
                        skips = 0;
                        //these cutscenes are triggered, not activated, so the
                        //first enter they press isn't prompting someone to talk
                        //if(progress.equals("Entering the Village") && progress.equals("Entering the Jungle"))
                        //    skips++;
                    }
                    skipTime = currentTime;
                }
                else {
                    skips++;
                }
                
                if(skips==3) {
                    if(progress.equals("Entering the Village")) {
                        enemies.get(4).setX(1300);
                        step = 0;
                        story++;
                        progress = "Learn to Interact";
                        text.updateWriteTime(currentTime);
                        cutscene = false;
                        end = true;
                        audio = new GameAudio("villageTheme");
                        start = true;
                        bossFight = true;
                        backgroundX = -600;
                    }
                    else if(progress.equals("Entering the Jungle")) {
                        progress = "";
                        step = 0;
                        cutscene = false;
                        end = true;
                        audio = new GameAudio(map.getSong());
                        start = true;
                        enemies = new ArrayList<Enemy>();
                        
                        hero.setFacingRight(false);
                        hero.fullHeal();
                        drops = new ArrayList<Item>();
                        Artifact art = new Artifact("Aether Wisp");
                        art.allocateCoordinates(WIDTH/4);
                        drops.add(art);
                        
                        if(!firstSpell)
                            spawnEnemies(true);
                        else
                            bossFight = true;
                    }
                    else if(progress.equals("The Talking Tree")) {
                        hero.fullHeal();
                        step = 0;
                        cutscene = false;
                        progress = "";
                    }
                    //INTERACTIONS WITH NPCS
                    else if(progress.equals("Talk to Old Man")) {
                        cutscene = false;
                        step = 0;
                        progress = "";
                        enemies.get(2).incrementExchanges();
                    }
                    else if(progress.equals("Talk to Nurse")) {
                        step = 0;
                        progress = "";
                        cutscene = false;
                        hero.fullHeal();
                        if(story==6 && enemies.get(3).getExchanges()==0) {
                            progress = "Learn to Press H";
                            text.updateWriteTime(currentTime);
                            step = 0;
                            bossFight = true;
                        }
                        enemies.get(3).incrementExchanges();
                    }
                    else if(progress.equals("Talk to Tree")) {
                        step = 0;
                        progress = "";
                        cutscene = false;
                        hero.fullHeal();
                    }
                    /*else if(progress.equals("Resurface")) {
                        map = new Map("Village");
                        end = true;
                        audio = new GameAudio(map.getSong());
                        start = true;
                        spawnEnemies(true);
                        backgroundX = -1300;
                        hero.setFacingRight(true);
                        hero.setX(550);

                        step = 0;
                        progress = "";
                        cutscene = false;
                    }*/
                    
                    skipTime = 0;
                    skips = 0;
                }
            }
        }
        else {
            if(key==87) //w
            {
                hero.setUp(false);
            }
            if(key==83) //s
            {
                hero.setDown(false);
            }
            if(key==65) //a
            {
                hero.setLeft(false);
            }
            if(key==68) //d
            {
                hero.setRight(false);
            }
            if(key==79) //o
            {
                if(defaultControls)
                    hero.setAttacking(false);
            }
            else if(key==74) //j
            {
                if(!defaultControls)
                    hero.setAttacking(false);
            }
            if(key==80) //p
            {
                if(defaultControls)
                    hero.setUsingAbility(false);
            }
            else if(key==75) //k
            {
                if(!defaultControls)
                    hero.setUsingAbility(false);
            }
            else if(key==77) //m - mute
            {
                if(muted) { 
                    muted = false;
                    start = true;
                }
                else {
                    muted = true;
                    end = true;
                }
            }
        }
    }
    public void mousePressed(MouseEvent e)
    {
        int key = e.getButton();
        char action = friend.getAction();
        
        if(!cutscene) {
            if(twoPlayers) {
                if(inventory.isDashboardVisible()) {
                    if(key==1) //left click
                        inventory.spendPoint(friend, 1);
                    if(key==3) //right click
                        inventory.spendPoint(friend, 2);
                }
                else if(action!='a' && action!='h' && action!='d') {
                    //left click
                    if(key==1) {
                        if(friend instanceof Butterfly) {
                            Hitbox hb = new Hitbox(friend.getX()+backgroundX,friend.getY()+backgroundY,friend.getW(),friend.getH());
                            boolean corrupted = false;
                            int i=0;
                            while(i<enemies.size() && !corrupted) {
                                Enemy currEnemy = enemies.get(i);
                                if(hb.overlaps(currEnemy.getHurtbox()) && !currEnemy.isBoss() && !(currEnemy instanceof BirdSpirit)) {
                                    friend = enemies.remove(i);
                                    friend.setAlert(false); //otherwise there's a glitch where you get stuck attacking
                                    friend.setAction('i');
                                    corrupted = true;
                                }
                                i++;
                            }
                        }
                        //if not Butterfly, friendAttacking should be true unless 
                        //p2 is hound or gorilla (they auto-attack)
                        else if(!(friend instanceof Hound || friend instanceof HoundRoyal || friend instanceof Gorilla)) {
                            friendAttacking = true;
                        }
                    }
                    //mouse click
                    else if(key==2) {
                        if(!(friend instanceof Butterfly))
                            enemies.add(friend);
                        friend = new Enemy();
                        twoPlayers = false;
                        inventory.twoPlayerDisplay(false, WIDTH);
                    }
                    //right click
                    else if(key==3) {
                        if(!(friend instanceof Butterfly)) {
                            Hitbox hb = friend.getHurtbox();
                            int x = hb.getX()+hb.getW()/2-21;
                            int y = hb.getY()+hb.getH()/2-18;
                            enemies.add(friend);
                            friend = new Butterfly(x-backgroundX,true);
                            friend.setY(y);
                            inventory.resetFriendPoints();
                        }
                    }
                }
                //bottom-left click
                if(key==4) {
                    inventory.toggleDashboardVisiblity();
                }
                //top-left click - quick view
                if(key==5) {
                    displayP2Level = !displayP2Level;
                }
            }
            else {
                if(friend instanceof Butterfly) { //in case butterfly breaks/freezes
                    friend.setAction('i');
                }

                //mouse click
                if(key==2) {
                    twoPlayers = true;
                    inventory.twoPlayerDisplay(true, WIDTH);
                    friend = new Butterfly(hero.getHurtbox().getX()+hero.getHurtbox().getW()/2-21-backgroundX,true);
                }
            }
        }
    }
    public void mouseReleased(MouseEvent e)
    {
        friendAttacking = false;
    }
    public void mouseClicked(MouseEvent e)
    {
    }
    public void mouseEntered(MouseEvent e)
    {
    }
    public void mouseExited(MouseEvent e)
    {
    }
    public void start(final int ticks){
        Thread gameThread = new Thread(){
            public void run() {
                while(true) {
                    loop();
                    try {
                        Thread.sleep(1000 / ticks);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };	
        //Thread used to handle music
        Thread music = new Thread(){
            public void run(){
                while(true){
                    if(end) {
                        audio.stop();
                        end = false;
                    }
                    if(start) {
                        if(!muted) {
                            audio.loop();
                        }
                        start = false;
                    }
                    try{
                        Thread.sleep(1000 / ticks);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        gameThread.start();
        music.start();
    }
    
    public void drawCampfire(Graphics2D g2d) {
        if(storage.isGameSaved() && storage.getMap().equals(map)) {
            int cycle = 1;
            if(campfireTime+600<currentTime)
                campfireTime = currentTime;
            else
                cycle = (int)((currentTime-campfireTime-1)/100)+1;
            Image campfire = new ImageIcon(getClass().getResource("Images/firePlace"+cycle+".png")).getImage();
            g2d.drawImage(campfire, campX-96+backgroundX, 550-192+backgroundY, 192,192,null);
        }
    } 
    
    public void saveGame() {
        try {
            FileOutputStream file = new FileOutputStream("Storage.obj");
            ObjectOutputStream outStream = new ObjectOutputStream(file);
            storage.save(hero,friend,map,inventory,story,foundEasterEgg,firstSpell,aliveBosses,backgroundX,twoPlayers,discovered);
            outStream.writeObject(storage);
            //campX = storage.getHero().getHurtbox().getX()+storage.getHero().getHurtbox().getW()/2-backgroundX;
            //System.out.println("success");
        }
        catch(FileNotFoundException f) {
            //System.out.println(f);
        }
        catch(IOException i) {
            /*System.out.println("Error when constructing outStream");
            System.out.println(i);*/
        }
    }
    
    public void newGameWithTutorial() 
    {
        map = new Map("Forest");
        end = true;
        audio = new GameAudio("forestTheme");
        start = true;
        backgroundX = 0;
        backgroundY = 0;
        step = 0;
        story = 0;
        text.updateWriteTime(currentTime);
        cutscene = true;
        bossFight = true;
        progress = "Tutorial";

        writeMapName = false;
        firstSpell = true;
        foundEasterEgg = false;
        beatTheGame = false;

        enemies = new ArrayList<Enemy>();
        spawnRival(2250, false); //initial addition

        aliveBosses = new boolean[3];
        aliveBosses[0] = true; //moss dragon in deep forest
        aliveBosses[1] = true; //king in the castle
        aliveBosses[2] = true; //sandworm in deep cave (postgame)

        inventory = new Inventory(WIDTH, HEIGHT);

        drops = new ArrayList<Item>();

        //who is who
        if (hero.isKnight()) {
            heroFace = "knightFace";
            rivalFace = "archerFace";
        } else {
            heroFace = "archerFace";
            rivalFace = "knightFace";
        }

        storage.delete();
    }
    
    public void newGameNoTutorial()
    {
        map = new Map("Forest");
        progress = "";
        backgroundX = 0;
        backgroundY = 0;
        step = 0;
        story = 3;
        cutscene = false;
        end = true;
        text.updateWriteTime(currentTime);
        audio = new GameAudio("forestTheme");
        start = true;
        inventory = new Inventory(WIDTH, HEIGHT);
        writeMapName = false;
        firstSpell = true;
        foundEasterEgg = false;
        beatTheGame = false;
        bossFight = false;

        spawnEnemies(true);

        aliveBosses = new boolean[3];
        aliveBosses[0] = true; //moss dragon in deep forest
        aliveBosses[1] = true; //king in the castle
        aliveBosses[2] = true; //sandworm in deep cave (postgame)

        drops = new ArrayList<Item>();

        //who is who
        if (hero.isKnight()) {
            heroFace = "knightFace";
            rivalFace = "archerFace";
        } else {
            heroFace = "archerFace";
            rivalFace = "knightFace";
        }

        storage.delete();
    }
    
    public void newGamePlayedAlready()
    {
        map = new Map("Forest");
        progress = "";
        backgroundX = 0;
        backgroundY = 0;
        step = 0;
        story = 100;
        cutscene = false;
        end = true;
        text.updateWriteTime(currentTime);
        audio = new GameAudio("forestTheme");
        start = true;
        inventory = new Inventory(WIDTH, HEIGHT);
        writeMapName = false;
        firstSpell = false;
        foundEasterEgg = false;
        beatTheGame = false;
        bossFight = false;

        spawnEnemies(true);

        aliveBosses = new boolean[3];
        aliveBosses[0] = true; //moss dragon in deep forest
        aliveBosses[1] = true; //king in the castle
        aliveBosses[2] = true; //sandworm in deep cave (postgame)

        drops = new ArrayList<Item>();

        //who is who
        if (hero.isKnight()) {
            heroFace = "knightFace";
            rivalFace = "archerFace";
        } else {
            heroFace = "archerFace";
            rivalFace = "knightFace";
        }

        campX = 125;
        
        saveGame();
    }
    
    public void drawVerticalCenterLine(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(WIDTH/2,0,WIDTH/2,HEIGHT);
    }

    public static void main(String[] args)
    {
        RunningFile r = new RunningFile();
        r.start(60);
    }
}
