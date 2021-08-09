import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class VillagerMaleElder extends Enemy
{
    private Text name;
    
    public VillagerMaleElder(int location, boolean facingRight)
    {
        //48, 48
        super("villagerMaleElder", 1, 1, 0, 0, facingRight, location, 192, 192);
        initIdle(4,170);
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
            name.reassignCoordinates(getX()-5+backgroundX, getY()+backgroundY+20);
        else
            name.reassignCoordinates(getX()+getW()-105+backgroundX, getY()+backgroundY+20);
        name.writeOnOneLine("Old Man", g2d, 3);
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //invulnerable
        getHurtbox().update(0,0,0,0);
    }
    
    
}