
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class VillagerSquirrel extends Enemy
{
    private Text name;
    
    public VillagerSquirrel(int location, boolean facingRight)
    {
        //32, 32
        super("villagerSquirrel", 1, 1, 0, 0, facingRight, location, 96, 96);
        initIdle(3,100);
        name = new Text();
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = drawIdle(g2d,currentTime,backgroundX,backgroundY);
        
        if(!isFacingRight())
            file+="Left";
        
        try {
            Image villager = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
            g2d.drawImage(villager,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
        } catch(Exception e) {
            System.out.println(file);
        }
        
        if(isFacingRight())
            name.reassignCoordinates(getX()-65+backgroundX, getY()-35+backgroundY);
        else
            name.reassignCoordinates(getX()-70+backgroundX, getY()-35+backgroundY);
        name.writeOnOneLine("Ricky the Squirrel", g2d, 3);
        
        name.reassignCoordinates(600+backgroundX, 80+backgroundY);
        name.writeOnOneLine("Eric the Elderly Elm", g2d, 3);
        
    }
    
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //invulnerable
        getHurtbox().update(0,0,0,0);
    }
}