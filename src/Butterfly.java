import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Butterfly extends Enemy {
    public Butterfly(int locX, boolean facingRight) {
        //14 x 12
        super("butterfly",1,10,0,0,facingRight,locX,42,36);
    }
    
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY) {
        if(getAction()=='m') {
            if(isFacingRight())
                move(getMobility(),0);
            else
                move(getMobility(),0);
        }
    }
    
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY) {
        int animSpeed = 50;
        int cycle = (int)((currentTime-getAnimationTime()-1)/animSpeed)+1;
        if(getAnimationTime()+3*animSpeed<currentTime) {
            cycle = 3;
            setAnimationTime(currentTime);
        }
        
        String file = "butterfly"+cycle;
        if(!isFacingRight())
            file+="Left";
        
        Image enemy = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
        g2d.drawImage(enemy,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //children will call this themselves
        //this is default hurtbox
        getHurtbox().update(0,0,0,0);
    }
}