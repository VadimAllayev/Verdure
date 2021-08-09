import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.Serializable;
import java.lang.Character;

public class Item implements Serializable
{
    private int x,y,w,h;
    private String name;
    private String imageName;
    private String description = "";
    private char rarity;
    private boolean isArtifact;
    
    public Item()
    {
        name = "";
        imageName = "";
        description = "";
        rarity = ' ';
        isArtifact = false;
        x = 0;
        y = 0;
        w = 0;
        h = 0;
    }
    
    public Item(String n, boolean a)
    {
        name = n;
        imageName = convertToImageName();
        isArtifact = a;
        x = 0;
        y = 0;
        w = 0;
        h = 0;
    }
    
    public String toString()
    {
        return name;
    }
    
    public boolean exists()
    {
        if(!name.equals(""))
            return true;
        return false;
    }

    
    private String convertToImageName()
    {
        String output = "";
        for(int i=0; i<name.length(); i++)
        {
            char current = name.charAt(i);
            if(Character.isUpperCase(current)) //if uppercase make lowercase
                output+=Character.toLowerCase(current);
            else if(current==' ') // if space make dash
                output+='-';
            else if(current!='\'') //otherwise add letter unless it is an apostrophe
                output+=current;
        }
        return output;
    }
    
    public void allocateCoordinates(int centerX)
    {
        //36 x 36
        x = centerX - 18;
        y = 514;//FLOORY - 36
        w = 36;
        h = 36;
    }
    
    public void drawSelf(Graphics2D g2d, int bX, int bY)
    {
        try {
            Image item = new ImageIcon(this.getClass().getResource("Items/"+imageName+".png")).getImage();
            g2d.drawImage(item,x+bX,y+bY,w,h,null);
        }
        catch(Exception e) {
            System.out.println(imageName);
        }
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public char getRarity() {
        return rarity;
    }

    public void setRarity(char rarity) {
        this.rarity = rarity;
    }

    public boolean isArtifact() {
        return isArtifact;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
    
    
}