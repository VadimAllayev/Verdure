import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class BirdSpirit extends Enemy
{    
    public BirdSpirit(int locX, boolean facingRight)
    {
        //22 x 22
        super("birdSpirit",1,10,1,5,facingRight,locX,88,88);
        
        initIdle(1,0);
        initDeath(1,300);
        
        super.setY(430); //default y-coordinate
        setAction('i');
    }
    
    @Override
    public void postReact()
    {
        
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        //alertness simulator
        if(isAlert())
        {
            int dist = Math.abs(getHurtbox().distanceFrom(h.getHurtbox()));
            if(dist>=800)
                setAlert(false);
        }
        else
        {
            int dist = Math.abs(getHurtbox().distanceFrom(h.getHurtbox()));
            
            if(isFacingRight() && getHurtbox().isLeftOf(h.getHurtbox()) || 
                    (!isFacingRight() && getHurtbox().isRightOf(h.getHurtbox())))
            {
                if(dist<500)
                {
                    setAlert(true);
                }
            }
        }
        
        if(getAction()=='i' && isAlert()) {
            //prevents bird from going off screen
            if(isFacingRight() && getX()+getW()+800>4320)
                turnAround();
            else if(!isFacingRight()&& getX()-800<0)
                turnAround();
            
            setAction('a');
            setAnimationTime(currentTime);
        }
        
        if(getAction()=='b') {
            if(isFacingRight()) { //initially right
                int t = (int)((currentTime-getAnimationTime())/100);
                int v = 6-t;
                if(v<0) {
                    setFacingRight(getHurtbox().isLeftOf(h.getHurtbox()));
                    if(isAlert()) {
                        setAction('a');
                        setAnimationTime(currentTime);
                    }
                    else {
                        setAction('i');
                    }
                }
                move(v,0);
            }
            else { //initially left
                int t = (int)((currentTime-getAnimationTime())/100);
                int v = -6+t;
                if(v>0) {
                    setFacingRight(getHurtbox().isLeftOf(h.getHurtbox()));
                    if(isAlert()) {
                        setAction('a');
                        setAnimationTime(currentTime);
                    }
                    else {
                        setAction('i');
                    }
                }
                move(v,0);
            }
        }
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='d') {
            super.drawSelf(g2d, currentTime, backgroundX, backgroundY);
        }
        else {
            String file = "birdSpirit";
            if(getAction()=='i') {
                file+=1;
            }
            else if(getAction()=='a') {

                if(getAnimationTime()+5*80<currentTime)
                {
                    file+=5;
                    setAction('b');
                    if(isFacingRight())
                        move(800,0);
                    else
                        move(-800,0);
                    setAnimationTime(currentTime);
                }
                else
                {
                    int cycle = (int)((currentTime-getAnimationTime()-1)/80)+1;
                    file+=cycle;
                }
            }
            else if(getAction()=='b') {
                
                file+=1;
                if(getAnimationTime()+100>currentTime) {
                    if(isFacingRight()) {
                        for(int i=2; i<6; i++) {
                            Image birb = new ImageIcon(this.getClass().getResource("Images/birdSpirit"+i+".png")).getImage();
                            g2d.drawImage(birb,getX()+backgroundX-((i-1)*200),getY()+backgroundY,getW(),getH(),null);
                        }
                    }
                    else {
                        for(int i=2; i<6; i++) {
                            Image birb = new ImageIcon(this.getClass().getResource("Images/birdSpirit"+i+"Left.png")).getImage();
                            g2d.drawImage(birb,getX()+backgroundX+((i-1)*200),getY()+backgroundY,getW(),getH(),null);
                        }
                    }
                }
            }
            
            if(!isFacingRight()) {
                file+="Left";
            }

            try {
                Image birb = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(birb,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);

                /*getHurtbox().drawSelf(g2d, false);
                getHitbox().drawSelf(g2d, true);*/
            }
            catch(Exception e) {
                System.out.println(file+", "+getAction());
            }
        }
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //children will call this themselves
        //this is default hurtbox
        getHurtbox().update(getX()+backgroundX,getY()+backgroundY,getW(),getH());
        getHitbox().update(0,0,0,0);
        if(getAction()=='b' && getAnimationTime()+100>currentTime) {
            if(isFacingRight()) { //rightwards
                getHitbox().update(getX()-800+backgroundX,getY()+backgroundY,800,getH());
            }
            else { //leftwards
                getHitbox().update(getX()+getW()+backgroundX,getY()+backgroundY,800,getH());
            }
        }
    }
    
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=4) //4%
            return new Consumable("Elixir of Iron");
        else if(rand<=8) //4%
            return new Consumable("Elixir of Energy");
        else if(rand<=28) //20%
            return new Consumable("Health Potion");
        else if(rand<=48) //20%
            return new Consumable("Mana Potion");
        else if(rand<=58) //10%
            return new Artifact("Boots of Mobility");
        else if(rand<=78) //20%
            return new Artifact("Boots of Speed");
        else if(rand<=80) //2%
            return new Artifact("Boots of Swiftness");
        else //20%
            return new Artifact("Cloak of Agility");
    }
}