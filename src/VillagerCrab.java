import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class VillagerCrab extends Enemy {
    private Text name;
    private boolean dj;
    
    public VillagerCrab(int location, boolean facingRight)
    {
        //25, 14
        super("crab", 1, 6, 0, 0, facingRight, location, 100, 70);
        setY(getFLOORY());
        initIdle(1,100);
        initMove(1, 300);
        initAttack(5,150);
        name = new Text();
        dj = false;
        setAction('e');
    }
    public VillagerCrab(int location, boolean facingRight, boolean isDj)
    {
        //25, 14
        super("crab", 1, 6, 0, 0, facingRight, location, 100, 70);
        initIdle(1,100);
        initMove(1, 300);
        initAttack(5,150);
        initReact(4,130);
        name = new Text();
        dj = isDj;
        setAction('e');
    }
    
    @Override
    public void postAttack()
    {
        int rand = (int)(Math.random()*10);
        if(rand<4 && !dj) { //4 out of 10 chance
            if(getX()<=100 || getX()+getW()>=3740)
                turnAround();
            else {
                int randTurn = (int)(Math.random()*6);
                if(randTurn==0) //1 out of 5 chance
                    turnAround();
            }
            setAction('m');
        }
        else
            setAction('a');
    }
    
    @Override
    public void postMove()
    {
        setAction('a');
        if(dj && !isAlert())
            setAction('i');
    }
    
    @Override
    public void postReact()
    {
        setAction('a');
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(dj) {
            alertness(h, currentTime);
            setFacingRight(getHurtbox().isLeftOf(h.getHurtbox()));
            if(getAction()=='i' && isAlert()) {
                setAction('r');
            }
            else if(!isAlert()) {
                setAction('i');
            }
        }
        
        if(getAction()=='m') {
            if(isFacingRight())
                move(-getMobility(),0);
            else
                move(+getMobility(),0);
        }
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='e') {
            String file = "crabIdle1";
            if (!isFacingRight())
                file += "Left";

            try {
                Image enemy = new ImageIcon(this.getClass().getResource("Images/" + file + ".png")).getImage();
                g2d.drawImage(enemy, getX() + backgroundX, getY() + backgroundY, getW(), getH(), null);
            } catch (Exception e) {
                System.out.println(file);
            }
            
            moveY(-3);
            if(getY()+getH()<=getFLOORY()) {
                setY(getFLOORY()-getH());
                if (dj) {
                    setAction('i');
                } else {
                    setAction('a');
                }
            }
        }
        else {
            super.drawSelf(g2d, currentTime, backgroundX, backgroundY);
        }
        
        if(dj) {
            if(isFacingRight())
                name.reassignCoordinates(getX()-30+backgroundX, getY()+backgroundY-30);
            else
                name.reassignCoordinates(getX()-45+backgroundX, getY()+backgroundY-30);
            name.writeOnOneLine("MC Crabby Boi", g2d, 3);
        }
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //invulnerable
        getHurtbox().update(getX()+getW()/2+backgroundX,0,0,0);
        getHitbox().update(0,0,0,0);
    }
}