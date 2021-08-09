import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.Serializable;

public class Map implements Serializable
{
    private int limit; //until screen stops scrolling and you go to next map
    private int repeats; //how many times the map loops
    private int shiftY; //how far up to push the map so that the floor is in proper position
    private int width,height;
    private String name, next, previous; //map names
    private String back, front; //image names
    private String song; //url
    private final int SCREEN_WIDTH=1200, SCREEN_HEIGHT=675; //dimensions of screen
    private final int LIM_X = -1258; //formula for limit (width x reps - lim_X = limit)
    private int location; //represents location relative to other maps
    
    public Map(String n)
    {
        name = n;
        back = "";
        front = "";
        createMap();
    }
    
    private void createMap()
    {
        song = "forestTheme";
        
        if(name.equals("Menu"))
        {
            front = "natureBackground.png";
            song = "menuTheme";
        }
        else if(name.equals("Forest")) //793 x 928
        {
            width = 1586;
            height = 1856;
            limit = -3500;
            shiftY = 1856;
            repeats = 3;
            previous = "Deep Forest";
            next = "Village";
            back = "geometricForest.png";
            front = "geometricForestFloor.png";
            song = "forestTheme";
            location = 1;
        }
        else if(name.equals("Village")) //just forest with props
        {
            width = 1586;
            height = 1856;
            limit = -1914;
            shiftY = 1856;
            repeats = 2;
            previous = "Forest";
            next = "Ruins";
            back = "geometricForest.png";
            front = "geometricForestFloor.png";
            song = "villageTheme";
            location = 2;
        }
        else if(name.equals("Deep Forest"))
        {
            width = 1586;
            height = 1856;
            limit = -5086;
            shiftY = 1856;
            repeats = 4;
            previous = "";
            next = "Forest";
            back = "geometricForest.png";
            front = "geometricForestFloor.png";
            song = "forestTheme";
            location = 0;
        }
        else if(name.equals("Cave")) //256,250
        {
            width = 768;
            height = 750;
            limit = -1046;
            shiftY = 750;
            repeats = 3;
            previous = "";
            next = "Deep Cave";
            back = "caveBack.png";
            front = "caveFront.png";
            song = "caveTheme";
            location = 8;
        }
        else if(name.equals("Deep Cave")) //256,250
        {
            width = 768;
            height = 750;
            limit = -2582;
            shiftY = 750;
            repeats = 5;
            previous = "Cave";
            next = "";
            back = "caveBack.png";
            front = "caveDeepFront.png";
            song = "deepCaveTheme";
            location = 9;
        }
        else if(name.equals("Castle")) //1920 x 1080
        {
            width = 1440;
            height = 810;
            limit = -3062;
            shiftY = 740;
            repeats = 3;
            previous = "Aftermath";
            next = "";
            back = "castleHallway.png";
            front = "";
            song = "castleTheme";
            location = 7;
        }
        else if(name.equals("Ruins")) //1920 x 1080
        {
            width = 1440;
            height = 810;
            limit = -3062;
            shiftY = 740;
            repeats = 3;
            previous = "Village";
            next = "Jungle";
            back = "ruinsBackground.png";
            front = "";
            song = "ruinsTheme";
            location = 3;
        }
        else if(name.equals("Aftermath")) //1920 x 1080
        {
            width = 1440;
            height = 810;
            limit = -3062;
            shiftY = 740;
            repeats = 3;
            previous = "Clearing";
            next = "Castle";
            back = "aftermathBackground.png";
            front = "aftermathBones.png";
            song = "aftermathTheme";
            location = 6;
        }
        else if(name.equals("Jungle")) //1920 x 1080
        {
            width = 1440;
            height = 810;
            limit = -3062;
            shiftY = 740;
            repeats = 3;
            previous = "Ruins";
            next = "Clearing";
            back = "jungleBackground.png";
            front = "";
            song = "jungleTheme";
            location = 4;
        }
        else if(name.equals("Clearing")) //1920 x 1080
        {
            width = 1440;
            height = 810;
            limit = -182;
            shiftY = 740;
            repeats = 1;
            previous = "Jungle";
            next = "Aftermath";
            back = "jungleBackground.png";
            front = "";
            song = "clearingTheme";
            location = 5;
        }
    }
    
    //draw background
    public void drawBack(Graphics2D g2d, int backgroundX, int backgroundY)
    {
        Image image = new ImageIcon(this.getClass().getResource("Backgrounds/"+back)).getImage();
        for(int i=0; i<repeats; i++)
        {
            g2d.drawImage(image,backgroundX+(width*i),SCREEN_HEIGHT-shiftY+backgroundY,width,height,null);
        }
        
        if(name.equals("Village"))
        {
            //84 x 29
            Image wheelbarrow = new ImageIcon(this.getClass().getResource("Backgrounds/wheelbarrow.png")).getImage();
            Image well = new ImageIcon(this.getClass().getResource("Backgrounds/well.png")).getImage();
            Image axe = new ImageIcon(this.getClass().getResource("Backgrounds/axeInStump.png")).getImage();
            
            
            g2d.drawImage(wheelbarrow,400+backgroundX,550-124-20+backgroundY,400,124,null); //100,31
            g2d.drawImage(well,1800+backgroundX,550-240-15+backgroundY,220,240,null); //44,48
            g2d.drawImage(axe,1300+backgroundX,550-100-20+backgroundY,96,100,null); //24,25
        }
    }
    
    public void drawFront(Graphics2D g2d, int screenH, int backgroundX, int backgroundY)
    {
        if(name.equals("Village"))
        {
            Image lumber = new ImageIcon(this.getClass().getResource("Backgrounds/lumberSmall.png")).getImage();
            Image barrel = new ImageIcon(this.getClass().getResource("Backgrounds/barrel.png")).getImage();
            Image hay = new ImageIcon(this.getClass().getResource("Backgrounds/hay.png")).getImage();
            
            g2d.drawImage(lumber,1050+backgroundX,550-54+backgroundY,152,64,null); //38,16
            g2d.drawImage(barrel,2100+backgroundX,550-82+backgroundY,80,92,null); //20,23
            g2d.drawImage(hay,2500+backgroundX,550-152+12+backgroundY,248,152,null); //62, 38
            
        }
        else if(name.equals("Castle"))
        {
            Image dragon = new ImageIcon(this.getClass().getResource("Backgrounds/dragonStatue.png")).getImage();
            for(int i=1; i<repeats; i++)
                g2d.drawImage(dragon,width*i-278+backgroundX,SCREEN_HEIGHT-450+backgroundY,555,539,null);
        }
        
        //drawing front
        Image image = new ImageIcon(this.getClass().getResource("Backgrounds/"+front)).getImage();
        for(int i=0; i<repeats; i++)
            g2d.drawImage(image,backgroundX+(width*i),screenH-shiftY+backgroundY,width,height,null);
    }
    
    public boolean exists()
    {
        if(name.equals(""))
            return false;
        return true;
    }
    
    public Map nextMap()
    {
        return new Map(next);
    }
    public Map previousMap()
    {
        return new Map(previous);
    }

    public int getLimit() {
        return limit;
    }
    
    public String getName() {
        return name;
    }

    public String getSong() {
        return song;
    }
    
    public int getLocation() {
        return location;
    }
}