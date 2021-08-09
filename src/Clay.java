import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Clay extends Enemy
{
    private ArrayList<Projectile> wheels;
    private ArrayList<Long> times;
    private ArrayList<Boolean> directions;
    private boolean shoot;
    private long shootTime;
    
    public Clay(int locX, boolean faceRight)
    {
        //14 x 14
        super("clay",4,0,1,4,faceRight,locX,70,70);
        
        initDeath(1,500);
        initIdle(1,0);
        thisIsASpellCaster();
        
        setAction('i');
        
        wheels = new ArrayList<Projectile>();
        times = new ArrayList<Long>();
        directions = new ArrayList<Boolean>();
        shoot = false;
        shootTime = 0;
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        alertness(h,currentTime);
        
        if(shoot && shootTime+2000<currentTime) {
            shootTime = currentTime;
            times.add(currentTime);
            if(isFacingRight()) {
                wheels.add(new Projectile("clayWheel",getX()+getW(),getY(),6,0));
                directions.add(true);
            }
            else {
                wheels.add(new Projectile("clayWheel",getX(),getY(),-6,0));
                directions.add(false);
            }
            
            if((isFacingRight() && h.getHurtbox().isLeftOf(getHurtbox())) || 
                    !isFacingRight() && h.getHurtbox().isRightOf(getHurtbox()))
                turnAround();
        }
        
        for(int i=0; i<wheels.size(); i++) {
            //CHECKING TO SEE IF WHEELS HIT HERO
            boolean hit = h.actProjectile(wheels.get(i), currentTime, backgroundX, backgroundY);
            if(hit) {
                wheels.remove(i);
                directions.remove(i);
                times.remove(i);
                i--;
            }
        }
    }
    
    @Override
    public void postReact()
    {
        shoot = true;
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        char action = getAction();
        
        if(action=='d') {
            super.drawSelf(g2d, currentTime, backgroundX, backgroundY);
        }
        else {
            String file = "clay";
            
            if(action=='h' && getAnimationTime()+300<currentTime) {
                setAction('i');
                shoot = true;
            }
            
            int life = getLife();
            if(action=='i') {
                file+=life;
            }
            else {
                file+="Hit"+(life+1);
            }
            
            if(!isFacingRight())
                file+="Left";
            
            try {
                Image clay = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(clay,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
            }
            catch(Exception e)
            {
                System.out.println(action);
            }
        }
    }
    
    @Override
    public void takeDamage(int i)
    {
        if(i>1)
            i/=2;
        setLife(getLife()-i);
        if(getLife()<0)
            setLife(0);
    }
    
    @Override
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        for(int i=0; i<wheels.size(); i++) {
            //MOVING THE WHEELS
            int t = (int)((currentTime-times.get(i))/100);
            if(directions.get(i))
                wheels.get(i).changeVelocityX(6-t);
            else
                wheels.get(i).changeVelocityX(-6+t);
            wheels.get(i).move();
            
            //DRAWING THE WHEELS
            Projectile p = wheels.get(i);
            p.drawSelf(g2d,currentTime,backgroundX,backgroundY);
        }
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=25) //25%
            return new Artifact("Cloth Armor");
        else if(rand<=30) //5%
            return new Artifact("Ben's Clay Launcher");
        else if(rand<=35) //5%
            return new Artifact("Chain Vest");
        else if(rand<=45) //10%
            return new Artifact("Melanie's Spectre Cowl");
        else if(rand<=50) //5%
            return new Consumable("Elixir of Iron");
        else if(rand<=55) //5%
            return new Consumable("Elixir of Energy");
        else if(rand<=75) //20%
            return new Consumable("Health Potion");
        else if(rand<=90) //15%
            return new Consumable("Mana Potion");
        else //5%
            return new Artifact("Rachel's Charm");
    }

    public ArrayList<Projectile> getWheels() {
        return wheels;
    }
    public ArrayList<Long> getTimes() {
        return times;
    }
    public ArrayList<Boolean> getDirections() {
        return directions;
    }
    public long getShootTime() {
        return shootTime;
    }
    public void setShootTime(long shootTime) {
        this.shootTime = shootTime;
    }
    
    @Override
    public void resetProjectiles() {
        wheels = new ArrayList<Projectile>();
        times = new ArrayList<Long>();
        directions = new ArrayList<Boolean>();
    }
}