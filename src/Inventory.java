import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.Serializable;

public class Inventory implements Serializable
{
    private int x,y,w,h,x1,w1;
    private int life,power,mana,mobility; //upgrades
    private int friendLife,friendMobility; //upgrades
    private int points,friendPoints;
    private int page;
    private boolean visible,dashboardVisible;
    private Item[] items;
    private Artifact[] equipment;
    private Text text;
    
    //for equipment page
    private Item selected;
    private int selection;
    private boolean inEquipment;
    
    //for controls page
    private boolean defaultControls;
    
    //player 2 stuff
    private boolean twoPlayers;
    
    public Inventory(int screenWidth, int screenHeight)
    {
        x = screenWidth/5;
        y = 105;
        w = screenWidth/5*3;
        h = screenHeight-210;
        x1 = screenWidth/5*4 - 80;
        w1 = screenWidth/5;
        life = 0;
        power = 0;
        mana = 0;
        mobility = 0;
        friendLife = 0;
        friendMobility = 0;
        points = 0;
        page = 2;
        visible = false;
        dashboardVisible = false;
        items = new Item[16];
        for(int i=0; i<items.length; i++)
            items[i] = new Item();
        equipment = new Artifact[4];
        for(int i=0; i<equipment.length; i++)
            equipment[i] = new Artifact();
        
        selected = new Item();
        selection = -1;
        inEquipment = false;
        
        defaultControls = true;
        
        text = new Text();
    }
    
    public void toggleControls()
    {
        defaultControls = !defaultControls;
    }
    
    public void toggleVisibility()
    {
        visible = !visible;
    }
    
    public void toggleDashboardVisiblity()
    {
        dashboardVisible = !dashboardVisible;
    }
    
    public void incrementPoints(Hero h) {
        if(h.getLevel()<=13)
            points++;
    }
    public void incrementFriendPoints() {
        friendPoints++;
    }
    
    public boolean isFull()
    {
        for(Item i : items)
        {
            if(!i.exists())
                return false;
        }
        return true;
    }
    
    public void spendPoint(Hero h, int i)
    {
        if(points>0)
        {
            if(i==1 && life<3) {
                life++;
                points--;
                h.incrementBaseLife();
                h.updateStats(this);
            }
            else if(i==2 && mana<3) {
                mana++;
                points--;
                h.incrementBaseMana();
                h.updateStats(this);
            }
            else if(i==3 && power<3) {
                power++;
                points--;
                h.incrementBasePower();
                h.updateStats(this);
            }
            else if(i==4 && mobility<3) {
                mobility++;
                points--;
                h.incrementBaseMobility();
                h.updateStats(this);
            }
        }
    }
    
    public void spendPoint(Enemy friend, int i) {
        if(friendPoints>0) {
            if(i==1 && friend.getLifeBuff()<10) {
                friend.lifeUp();
                friendPoints--;
            }
            else if(i==2 && friend.getMobilityBuff()<10) {
                friend.speedUp();
                friendPoints--;
            }
            
        }
    }
    
    public void equip(Hero h, Artifact art, int index)
    {
        int i = 0;
        boolean added = false;
        while(i<equipment.length && !added)
        {
            if(!equipment[i].exists()) { //if it doesn't exist (so there is space)
                equipment[i] = art;
                added = true;
            }
            i++;
        }

        //if successfully added, update stats & remove item from index
        if(added) {
            h.updateStats(this);
            items[index] = new Item();
            selected = new Item();
        }
    }
    public void unequip(Hero h, Artifact art, int index)
    {
        int i = 0;
        boolean added = false; //in reference to inventory
        while(i<items.length && !added)
        {
            if(!items[i].exists()) { //if it doesn't exist (so there is space)
                items[i] = art;
                added = true;
            }
            i++;
        }

        //if successfully added, update stats & remove item from index
        if(added) {
            equipment[index] = new Artifact();
            selected = new Item();
            h.updateStats(this);
        }
    }
    public void consume(Hero h, Consumable con, int index)
    {
        h.heal(con.getLifeRecovery(), con.getManaRecovery());
        if(con.givesPermaBuffs()) {
            for(int i=0; i<con.getPermaLife(); i++) {
                h.incrementBaseLife();
            }
            for(int i=0; i<con.getPermaMana(); i++) {
                h.incrementBaseMana();
            }
            for(int i=0; i<con.getPermaPower(); i++) {
                h.incrementBasePower();
            }
            for(int i=0; i<con.getPermaMobility(); i++) {
                h.incrementBaseMobility();
            }
            h.updateStats(this);
        }
        items[index] = new Item();
        selected = new Item();
    }
    
    public void addItem(Item item)
    {
        int i = 0;
        boolean added = false;
        while(i<items.length && !added)
        {
            if(!items[i].exists()) {
                items[i] = item;
                added = true;
            }
            i++;
        }
    }
    
    public void remove(int index)
    {
        if(inEquipment)
            equipment[index] = new Artifact();
        else
            items[index] = new Item();
        selected = new Item();
    }
    
    //true = next page, false = previous page
    public void turnPage(boolean nextPage) 
    {
        //selection==-1 needed for page 1
        if(nextPage && page<5 && selection==-1)
            page++;
        else if(!nextPage && page>1 && selection==-1)
            page--;
    }
    
    public void selectionDown()
    {
        if(inEquipment)
        {
            selection++;
            if(selection==4)
                selection = 0;
            selected = equipment[selection];
        }
        else
        {
            selection+=4;
            if(selection>=16)
                selection-=16;
            selected = items[selection];
        }
        
    }
    public void selectionUp()
    {
        if(inEquipment)
        {
            selection--;
            if(selection==-1)
                selection = 3;
            selected = equipment[selection];
        }
        else
        {
            selection-=4;
            if(selection<=-1)
                selection+=16;
            selected = items[selection];
        }
    }
    public void selectionLeft()
    {
        if(inEquipment)
        {
            inEquipment = false;
            selection = selection*4+3;
            selected = items[selection];
        }
        else
        {
            //on left side of inventory
            if(selection%4==0) {
                inEquipment = true;
                selection/=4;
                selected = equipment[selection];
            }
            else {
                selection--;
                selected = items[selection];
            }
        }
    }
    public void selectionRight()
    {
        if(inEquipment)
        {
            inEquipment = false;
            selection*=4;
            selected = items[selection];
        }
        else
        {
            if(selection%4==3) {
                inEquipment = true;
                selection = (selection-3)/4;
                selected = equipment[selection];
            }
            else {
                selection++;
                selected = items[selection];
            }
        }
    }
    public void toggleSelection(boolean startSelecting)
    {
        if(startSelecting)
        {
            selection = 0;
            selected = items[0];
        }
        else
        {
            selection = -1;
            selected = new Item();
            inEquipment = false;
        }
    }
    //equip, unequip, or consume selected
    public void useSelected(Hero h, char act)
    {
        if(inEquipment) //unequip
        {
            if(equipment[selection].exists())
            {
                if(act=='u')
                    unequip(h,new Artifact(equipment[selection].getName()),selection);
            }
        }
        else //equip or consume
        {
            if(items[selection].exists())
            {
                if(items[selection].isArtifact())
                {
                    if(act=='e')
                        equip(h,new Artifact(items[selection].getName()),selection);
                }
                else //an item
                {
                    if(act=='c')
                        consume(h,new Consumable(items[selection].getName()),selection);
                }
            }
        }
    }
    
    public void trashSelected(Hero h)
    {
        if(inEquipment)
        {
            equipment[selection] = new Artifact();
            h.updateStats(this);
        }
        else
        {
            items[selection] = new Item();
        }
        
        selected = new Item();
    }
    
    
    public void drawSelf(Graphics g, Graphics2D g2d, Hero hero, Enemy friend, boolean[] discovered, int location, int story)
    {
        if(dashboardVisible) {
            Color background = new Color(22,9,9);
            g.setColor(background);
            g.fillRect(x1,y,w1,h);
            g.setColor(Color.BLACK);
            g.drawRect(x1,y,w1,h);
            g.setColor(Color.WHITE);
            g.drawRect(x1+20,y+20,w1-40,h-40);

            drawPlayer2Dashboard(g, g2d, friend);
        }
        
        if(visible)
        {
            //basic background
            Color background = new Color(22,9,9);
            g.setColor(background);
            g.fillRect(x,y,w,h);
            g.setColor(Color.BLACK);
            g.drawRect(x,y,w,h);
            g.setColor(Color.WHITE);
            g.drawRect(x+20,y+20,w-40,h-40);
            
            //boxes in bottom right corner
            g.drawRect(x+w-95,y+h-35,10,10);
            g.drawRect(x+w-80,y+h-35,10,10);
            g.drawRect(x+w-65,y+h-35,10,10);
            g.drawRect(x+w-50,y+h-35,10,10);
            g.drawRect(x+w-35,y+h-35,10,10);
            
            
            if(page==1) //status
            {
                g.fillRect(x+w-95,y+h-35,10,10);
                drawStatusPage(g,g2d,hero,friend,discovered,location,story);
            }
            else if(page==2) //equipment
            {
                g.fillRect(x+w-80,y+h-35,10,10);
                drawEquipmentPage(g,g2d);
            }
            else if(page==3) //upgrades
            {
                g.fillRect(x+w-65,y+h-35,10,10);
                drawUpgradesPage(g,g2d);
            }
            else if(page==4) //controls
            {
                g.fillRect(x+w-50,y+h-35,10,10);
                drawControlsPage(g,g2d);
            }
            else //controls p2
            {
                g.fillRect(x+w-35,y+h-35,10,10);
                drawControlsP2Page(g,g2d);
            }
        }
    }

    public void drawStatusPage(Graphics g, Graphics2D g2d, Hero hero, Enemy friend, boolean[] discovered, int location, int story) {
        text.edit(x+40,y+40,x+w-40);
        text.write("Status",g2d);
        
        text.changeY(y+100);
        String word;
        if(hero.isKnight())
            word = "KNIGHT";
        else 
            word = "ARCHER"; 
        text.write(word,g2d,3);
        text.changeY(y+130);
        String levelInfo;
        if(hero.getLevel()<13)
            levelInfo = "Level "+hero.getLevel()+"  Experience: "+hero.getExp()+"/"+hero.getMaxExp();
        else
            levelInfo = "Max Level (13)";
        if(hero.canRevive())
            levelInfo+="  Can Revive";
        text.write(levelInfo,g2d,3);
        text.changeY(y+160);
        text.write("Life: "+hero.getLife()+"/"+hero.getMaxLife()+"  Mana: "+hero.getMana()+"/"+hero.getMaxMana()+"  Power: "+hero.getPower()+"  Speed: "+hero.getMobility(),g2d,3);
        
        //MAP
        //y+235 is the center line of the map
        g.setColor(Color.WHITE);
        int distance = (w-110)/7;
        int current = 0;
        text.changeY(y+225);
        for(int i = x+40; current<8; i+=distance) {
            g.drawRect(i, y+220, 30, 30);
            if(current==location) {
                g.drawRect(i-1, y+219, 32, 32);
                g.drawRect(i-2, y+218, 34, 34);
            }
            if(current < 7) {
                g.drawLine(i+30, y+235, i+distance, y+235);
            }
            if(discovered[current]) {
                text.changeX(i+10);
                String letter;
                if(current==0) letter = "D";
                else if(current==1) letter = "F";
                else if(current==2) letter = "V";
                else if(current==3) letter = "R";
                else if(current==4) letter = "J";
                else if(current==5) letter = "C";
                else if(current==6) letter = "A";
                else letter = "C";
                text.write(letter,g2d,3);
            }
            current++;
        }
        
        if(friend.exists()) {
            text.reassignCoordinates(x+40, y+290);
            String enemyName = friend.getName().toUpperCase();
            text.write(enemyName,g2d,3);
            text.changeY(y+320);
            if(friend.getLevel()<21)
                levelInfo = "Level "+friend.getLevel()+"  Experience: "+friend.getExp()+"/"+friend.getMaxExp();
            else
                levelInfo = "Max Level";
            text.write(levelInfo,g2d,3);
            text.changeY(y+350);
            text.write("Life: "+friend.getLife()+"/"+friend.getMaxLife()+"  Power: "+friend.getStrength()+"  Mobility: "+friend.getMobility(),g2d,3);
        }
        else {
            text.reassignCoordinates(x+40, y+290);
            text.write(getQuote(location,story), g2d, 3);
        }
        
        text.reassignCoordinates(x+25,y+h-45);
        text.write("Navigate: RIGHT", g2d, 3);
    }
    
    public void drawControlsPage(Graphics g, Graphics2D g2d)
    {
        text.edit(x+40,y+40,x+w-40);
        text.write("Controls",g2d);
        text.changeY(y+100);
        text.write("AD - Move,  S - Roll,  SPACE - Jump",g2d,3);
        text.changeY(y+130);
        if(defaultControls)
            text.write("O - Attack, P - Shield",g2d,3);
        else
            text.write("J - Attack, K - Shield",g2d,3);
        text.changeY(y+160);
        text.write("1234 - Spells - Costs mana and requires an equipped artifact capable of casting a spell.",g2d,3);
        text.changeY(y+225);
        text.write("Q - Open/Close Inventory",g2d,3);
        text.changeY(y+255);
        text.write("ENTER - Interact with NPCs  OR  (during",g2d,3);
        text.changeY(y+285);
        text.write("cutscene) Press 3 times to skip cutscene.",g2d,3);
        text.changeY(y+315);
        text.write("M - Mute/Unmute Audio",g2d,3);
        text.changeY(y+345);
        text.write("H - Quick Heal - Auto-consumes Consumables.",g2d,3);
        
        text.reassignCoordinates(x+25,y+h-45);
        text.write("SHIFT - swap controls, Navigate: LFT, RT", g2d, 3);
    }
    
    public void drawControlsP2Page(Graphics g, Graphics2D g2d)
    {
        text.edit(x+40,y+40,x+w-40);
        text.write("Controls (Player 2)",g2d);
        text.changeY(y+100);
        text.write("Mouse Click - Join / Exit",g2d,3);
        text.changeY(y+130);
        text.write("Your character will follow your mouse.",g2d,3);
        text.changeY(y+175);
        text.write("BUTTERFLY FORM",g2d,3);
        text.changeY(y+205);
        text.write("Left Click - Mind Control Enemy",g2d,3);
        text.changeY(y+250);
        text.write("ENEMY FORM",g2d,3);
        text.changeY(y+280);
        text.write("Left Click - Attack, Right Click - Leave Host",g2d,3);
        text.changeY(y+310);
        text.write("Mouse 4 Click - Toggle Quick View",g2d,3);
        text.changeY(y+340);
        text.write("Mouse 5 Click - Open Dashboard",g2d,3);
        
        text.reassignCoordinates(x+25,y+h-45);
        text.write("Navigate: LEFT", g2d, 3);
    }
    
    //returns a quote from someone on that area in the map
    public String getQuote(int location, int story) {
        if(location==0) //Deep Forest
            return "\"Blessed are the curious, for they shall have adventures.\" - Lovelle Drachman";
        else if(location==1) //Forest
            return "\"A journey of a thousand miles begins with a single step.\" - Lao Tzu";
        else if(location==2) //Village
            return "\"Take rest. A field that has rested gives a bountiful crop.\" - Ovid";
        else if(location==3) //Ruins
            return "\"The greatest glory in living lies not in never falling, but in rising every time we fall.\" - Nelson Mandela";
        else if(location==4) //Jungle
            return "\"If life were predictable it would cease to be life, and be without flavor.\" - Eleanor Roosevelt";
        else if(location==5) //Clearing
            return "\"There are no secrets to success. It is the result of preparation, hard work, and learning from failure.\" - Colin Powell";
        else if(location==6) //Aftermath
            return "\"It is during our darkest moments that we must focus to see the light.\" - Aristotle";
        else if(location==7) //Castle
            return "\"When you reach the end of your rope, tie a knot in it and hang on.\" - Franklin D. Roosevelt";
        else if(location==8) { //Cave
            if(story<10)
                return "\"Where are you lol why you in a cave is this an easter egg or smthn?\" - Unknown";
            else
                return "\"This do be a cave tho.\" - Unknown";
        }
        else //location==9 //Deep Cave
            return "\"Go confidently in the direction of your dreams! Live the life you've imagined.\" - Henry David Thoreau";
        
    }
    
    public void drawEquipmentPage(Graphics g, Graphics2D g2d)
    {
        //ITEM SIZE IS 36x36 --> 54x54
        g.setColor(Color.WHITE);

        //HERO
        //270x165 --> 224x137
        //g2d.drawImage(heroImage,x+56,y+h/2-112,270,165,null);

        //for inventory
        int startX = 56*4 - 56/2;
        for(int i=0; i<5; i++)
        {
            //EQUIPMENT - ARTIFACTS
            if(i<2) {
                //vertical lines
                g.drawLine(x+75+i*56,y+h/2-112,x+75+i*56,y+h/2+112);
            }
            //horizontal lines
            g.drawLine(x+75,y+h/2-112+i*56,x+131,y+h/2-112+i*56);

            //INVENTORY - ITEMS
            //vertical lines
            g.drawLine(x+startX+i*56,y+h/2-112,x+startX+i*56,y+h/2+112);
            //horizontal lines
            g.drawLine(x+startX,y+h/2-112+i*56,x+startX+56*4,y+h/2-112+i*56);
        }

        //selected item
        g.drawRect(x+startX+56*5-20,y+h/2-112,56*3+40,224);
        if(!selected.getName().equals(""))
        {
            g.drawRect(x+startX+56*5-20,y+h/2-112,56,56);
            g.drawRect(x+startX+56*5-20,y+h/2-112,56*3+40,56);

            text.reassignCoordinates(x+startX+56*5+45, y+h/2-103);
            if(inEquipment) {
                text.write("U: Unequip     T: Trash",g2d,2);
            }
            else if(selected.isArtifact()) {
                text.write("E: Equip       T: Trash",g2d,2);
            }
            else {
                text.write("C: Consume    T: Trash",g2d,2);
            }

            Image chosen = new ImageIcon(this.getClass().getResource("Items/"+selected.getImageName()+".png")).getImage(); 
            g2d.drawImage(chosen,x+startX+56*5-19,y+h/2-112+1,54,54,null);

            text.edit(x+startX+56*5-10, y+h/2-45, x+startX+56*8+20);
            text.write(selected.getName(),g2d,3);

            text.reassignCoordinates(x+startX+56*5-10, y+h/2+20);
            text.write(selected.getDescription(),g2d,2);
        }

        //words
        text.reassignCoordinates(x+56,y+h/2-168);
        text.write("Eqpt.", g2d);
        text.changeX(x+startX);
        text.write("Inventory", g2d);
        text.changeX(x+startX+56*5-7);
        text.write("Selected",g2d);
        
        text.reassignCoordinates(x+25,y+h-45);
        if(selection==-1)
            text.write("Select: ENTER  Navigate: LEFT, RIGHT", g2d, 3);
        else
            text.write("Back: ESC  Move: UP, DOWN, LEFT, RIGHT", g2d, 3);

        //DRAWING ITEMS
        for(int i=0; i<4; i++)
        {
            //DRAWING ITEMS IN EQUIPMENT
            if(equipment[i].exists()) {
                Image equipImage = new ImageIcon(this.getClass().getResource("Items/"+equipment[i].getImageName()+".png")).getImage();
                g2d.drawImage(equipImage,x+76,y+h/2-111+i*56,54,54,null);
            }
            for(int j=0; j<4; j++)
            {
                if(items[i+j*4].exists()) {
                    //DRAWING ITEMS IN INVENTORY
                    try {
                        Image invenImage = new ImageIcon(this.getClass().getResource("Items/"+items[i+j*4].getImageName()+".png")).getImage();
                        g2d.drawImage(invenImage, x+startX+1+i*56, y+h/2-111+j*56, 54,54,null);
                    }
                    catch(Exception e) {
                        System.out.println("imageName: "+items[i+j*4].getImageName());
                    }
                }
            }
        }
        
        //SELECTION SQUARE GUI
        if(selection!=-1)
        {
            Image selector = new ImageIcon(this.getClass().getResource("Images/selector.png")).getImage();
            if(inEquipment) {
                g2d.drawImage(selector,x+75-4,y+h/2-112+selection*56-4, 64,64,null);
            }
            else {
                int row = selection%4;
                int col = selection/4;
                g2d.drawImage(selector,x+startX+row*56-4, y+h/2-112+col*56-4, 64,64,null);
            }
        }
    }
    
    public void drawUpgradesPage(Graphics g, Graphics2D g2d)
    {
        int iconSize = 105; //35
        int coordX = x+w/5-iconSize/2; //to help center (x)
        int middleY = y+h/5+iconSize/2; //middle of icons (y)
        int ovalSize = 25;
        int ovalX = x+w/5-ovalSize/2;
        int ovalY = y+h/10*3+iconSize-ovalSize/2;

        //LINES
        int l = life, a = mana, p = power, m = mobility;
        if(l==3) l=2;
        if(a==3) a=2;
        if(p==3) p=2;
        if(m==3) m=2;
        g.setColor(Color.WHITE);
        g.drawLine(coordX,middleY,coordX+w/5*3,middleY);
        g.drawLine(x+w/5,middleY,x+w/5,ovalY+(h/10*l));
        g.drawLine(x+w/5*2,middleY,x+w/5*2,ovalY+(h/10*a));
        g.drawLine(x+w/5*3,middleY,x+w/5*3,ovalY+(h/10*p));
        g.drawLine(x+w/5*4,middleY,x+w/5*4,ovalY+(h/10*m));

        //CIRCLES
        for(int i=0; i<4; i++)
        {
            for(int j=0; j<3; j++)
            {
                if(i==0) { //life
                    if(life>j)
                        g.fillOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                    else
                        g.drawOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                }
                else if(i==1) { //mana
                    if(mana>j)
                        g.fillOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                    else
                        g.drawOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                }
                else if(i==2) { //power
                    if(power>j)
                        g.fillOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                    else
                        g.drawOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                }
                else { //mobility
                    if(mobility>j)
                        g.fillOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                    else
                        g.drawOval(ovalX+(w/5*i),ovalY+(h/10*j),ovalSize,ovalSize);
                }
            }
        }

        Image pow = new ImageIcon(this.getClass().getResource("Images/iconPower.png")).getImage();
        Image lif = new ImageIcon(this.getClass().getResource("Images/iconLife.png")).getImage();
        Image man = new ImageIcon(this.getClass().getResource("Images/iconMana.png")).getImage();
        //Image arm = new ImageIcon(this.getClass().getResource("Images/iconArmor.png")).getImage();
        Image mob = new ImageIcon(this.getClass().getResource("Images/iconMobility.png")).getImage();

        g2d.drawImage(lif,coordX,y+h/5,iconSize,iconSize,null);
        g2d.drawImage(man,coordX+w/5,y+h/5,iconSize,iconSize,null);
        g2d.drawImage(pow,coordX+w/5*2,y+h/5,iconSize,iconSize,null);
        g2d.drawImage(mob,coordX+w/5*3,y+h/5,iconSize,iconSize,null);
        
        text.reassignCoordinates(coordX+17,y+h/5-30);
        text.write("Life(1)",g2d,3);
        text.changeX(coordX+w/5+7);
        text.write("Mana(2)",g2d,3);
        /*text.changeX(coordX+w/5);
        text.write("Armor(2)",g2d,3);*/
        text.changeX(coordX+w/5*2);
        text.write("Power(3)",g2d,3);
        text.changeX(coordX+w/5*3);
        text.write("Speed(4)",g2d,3);
        
        text.reassignCoordinates(x+25,y+h-90);
        text.write("Available Points: "+points, g2d);
        text.reassignCoordinates(x+25,y+h-45);
        text.write("Invest: 1,2,3,4  Navigate: LEFT, RIGHT", g2d, 3);
    }
    
    public void drawPlayer2Dashboard(Graphics g, Graphics2D g2d, Enemy friend) {
        int adjWidth = (w1-60);
        g.setColor(Color.WHITE);
        g.drawRect(x1+30,y+h/2+20,adjWidth,30);
        g.drawRect(x1+30,y+h/2+135,adjWidth,30);
        for(int i=0; i<=1; i++) {
            for(int j=adjWidth/10; j<adjWidth; j+=adjWidth/10) {
                g.drawLine(x1+30+j, y+h/2+20+(i*115), x1+30+j, y+h/2+50+(i*115));
            }
        }
        
        g.fillRect(x1+30,y+h/2+20,adjWidth/10*friend.getLifeBuff(),30);
        g.fillRect(x1+30,y+h/2+135,adjWidth/10*friend.getMobilityBuff(),30);
        
        //words
        text.edit(x1+62,y+50,1150);
        text.write("Stats", g2d);
        text.reassignCoordinates(x1+40, y+h/2-100);
        text.write("Points: "+friendPoints, g2d, 4);
        text.reassignCoordinates(x1+25, y+h/2-40);
        text.write("Life Up:", g2d, 3);
        text.changeY(y+h/2-10);
        text.write("Left-Click to Upgrade", g2d, 2);
        text.changeY(y+h/2+75);
        text.write("Speed Up:", g2d, 3);
        text.reassignCoordinates(x1+22, y+h/2+105);
        text.write("Right-Click to Upgrade", g2d, 2);
        
    }
    
    public void twoPlayerDisplay(boolean two, int screenWidth) {
        twoPlayers = two;
        if(twoPlayers) {
            x = 80;
        }
        else {
            x = screenWidth/5;
        }
    }
    
    //closes all menus
    public void close()
    {
        visible = false;
        dashboardVisible = false;
        reset();
    }
    
    //used when user exists inventory
    public void reset()
    {
        selection = -1;
        selected = new Item();
        page = 2;
    }
    
    public void resetFriendPoints() {
        friendPoints = 0;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }
    
    public int getMobility() {
        return mobility;
    }

    public void setMobility(int mobility) {
        this.mobility = mobility;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public boolean isDashboardVisible() {
        return dashboardVisible;
    }

    public int getSelection() {
        return selection;
    }

    public Artifact[] getEquipment() {
        return equipment;
    }

    public Item[] getItems() {
        return items;
    }
    
    
    
    
}