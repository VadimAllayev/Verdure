import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.Serializable;

public class Projectile implements Serializable
{
    private String name;
    private boolean continuous; //keeps going until hits floor or enemy
    private int numOfImages;
    private int timePerImage;
    private int x,y,w,h;
    private int velocityX, velocityY;
    private final int FLOORY = 550;
    private long animationTime; //helps to draw animation
    
    //NEW
    private int damage; //damage it deals
    
    public Projectile()
    {
        name = "";
        x = 0;
        y = 0;
        w = 0;
        h = 0;
        velocityX = 0;
        velocityY = 0;
        animationTime = 0;
        numOfImages = 0;
        timePerImage = 0;
        continuous = false;
        damage = 0;
    }
    
    //use this when  projectile is on floor (ex. explosion)
    public Projectile(String name, int centerX)
    {
        this.name = name;
        velocityX = 0;
        velocityY = 0;
        animationTime = 0;
        
        createProjectile();
        
        x = centerX-w/2;
        y = FLOORY-h;
    }
    public Projectile(long currentTime, String name, int centerX)
    {
        this.name = name;
        velocityX = 0;
        velocityY = 0;
        animationTime = currentTime;
        
        createProjectile();
        
        x = centerX-w/2;
        y = FLOORY-h;
    }
    
    public Projectile(String name, int centerX, int centerY)
    {
        this.name = name;
        velocityX = 0;
        velocityY = 0;
        animationTime = 0;
        
        createProjectile();
        
        x = centerX-w/2;
        y = centerY-h/2;
    }
    public Projectile(long currentTime, String name, int centerX, int centerY)
    {
        this.name = name;
        velocityX = 0;
        velocityY = 0;
        animationTime = currentTime;
        
        createProjectile();
        
        x = centerX-w/2;
        y = centerY-h/2;
    }
    
    public Projectile(String name, int centerX, int centerY, int vX, int vY)
    {
        this.name = name;
        velocityX = vX;
        velocityY = vY;
        animationTime = 0;
        
        createProjectile();
        
        x = centerX-w/2;
        y = centerY-h/2;
    }
    public Projectile(long currentTime, String name, int centerX, int centerY, int vX, int vY)
    {
        this.name = name;
        velocityX = vX;
        velocityY = vY;
        animationTime = currentTime;
        
        createProjectile();
        
        x = centerX-w/2;
        y = centerY-h/2;
    }
    
    private void createProjectile()
    {
        if(name.equals("fireball"))
        {
            numOfImages = 1;
            timePerImage = 1;
            continuous = true;
            damage = 3;
            
            w = 90; //18
            h = 100; //20
        }
        else if(name.equals("explosion"))
        {
            numOfImages = 6;
            timePerImage = 60;
            continuous = false;
            damage = 5;
            
            //29,31
            w = 290;
            h = 310;
        }
        else if(name.equals("acidBlob"))
        {
            numOfImages = 7;
            timePerImage = 100;
            continuous = true;
            damage = 2;
            w = 84; //21
            h = 68; //17
        }
        else if(name.equals("acidSplat"))
        {
            numOfImages = 6;
            timePerImage = 80;
            continuous = false;
            damage = 2;
            w = 232; //58
            h = 152; //38
        }
        else if(name.equals("clayWheel"))
        {
            numOfImages = 2;
            timePerImage = 100;
            continuous = true;
            damage = 1;
            w = 60; //10
            h = 60; //10
        }
        else if(name.equals("flare"))
        {
            numOfImages = 5;
            timePerImage = 60;
            continuous = false;
            damage = 3;
            
            //24,23
            w = 168;
            h = 161;
        }
        else if(name.equals("star"))
        {
            numOfImages = 7;
            timePerImage = 60;
            continuous = false;
            damage = 1;
            
            //30,30
            w = 210;
            h = 210;
        }
        else if(name.equals("geyser"))
        {
            numOfImages = 8;
            timePerImage = 60;
            continuous = false;
            damage = 2;
            
            //16,32
            w = 160;
            h = 320;
        }
        else if(name.equals("vine"))
        {
            numOfImages = 8;
            timePerImage = 60;
            continuous = false;
            damage = 2;
            
            //19,29
            w = 171;
            h = 261;
        }
        else if(name.equals("wisp"))
        {
            numOfImages = 8;
            timePerImage = 80;
            continuous = false;
            damage = 1;
            
            //11, 27
            w = 66;
            h = 162;
        }
        else if(name.equals("formBoulder")) {
            numOfImages = 6;
            timePerImage = 80;
            continuous = false;
            damage = 2;
            
            //25, 25
            w = 175;
            h = 175;
        }
        else if(name.equals("boulder")) {
            numOfImages = 1;
            timePerImage = 1;
            continuous = true;
            damage = 5;
            
            //25, 25
            w = 150;
            h = 150;
        }
        else if(name.equals("ice")) {
            numOfImages = 5;
            timePerImage = 80;
            continuous = false;
            damage = 4;
            
            //29,28
            w = 145;
            h = 140;
        }
        else if(name.equals("arrow") || name.equals("arrowLeft")) {
            numOfImages = 1;
            timePerImage = 1;
            continuous = true;
            damage = 1;
            
            w = 113; //25
            h = 14; //3
        }
    }
    
    public String toString()
    {
        return name+" x: "+x+", y: "+y+", w: "+w+", h: "+h;
    }
    
    //hit its target
    public void hit()
    {
        damage = 0;
    }
    
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        if(name.equals(""))
        {
        }
        else if(continuous)
        {
            if(numOfImages==1)
            {
                Image projectile = new ImageIcon(this.getClass().getResource("Projectiles/"+name+".png")).getImage();
                g2d.drawImage(projectile,x+backgroundX,y+backgroundY,w,h,null);
            }
            else
            {
                if(animationTime+(numOfImages*timePerImage) < currentTime)
                {
                    animationTime = currentTime;
                    Image projectile = new ImageIcon(this.getClass().getResource("Projectiles/"+name+numOfImages+".png")).getImage();
                    g2d.drawImage(projectile,x+backgroundX,y+backgroundY,w,h,null);
                }
                else
                {
                    int cycle = (int)((currentTime-animationTime-1)/timePerImage)+1;
                    Image projectile = new ImageIcon(this.getClass().getResource("Projectiles/"+name+cycle+".png")).getImage();
                    g2d.drawImage(projectile,x+backgroundX,y+backgroundY,w,h,null);
                }
            }
        }
        else {
            if(animationTime+(numOfImages*timePerImage) < currentTime) {
                reset();
            }
            else {
                int cycle = (int)((currentTime-animationTime-1)/timePerImage)+1;
                Image projectile = new ImageIcon(this.getClass().getResource("Projectiles/"+name+cycle+".png")).getImage();
                g2d.drawImage(projectile,x+backgroundX,y+backgroundY,w,h,null);
            }
        }
    }
    
    public void changeVelocityX(int vX)
    {
        velocityX = vX;
    }
    
    public void changeVelocityY(int vY)
    {
        velocityY = vY;
    }
    
    public void changeVelocity(int vX, int vY)
    {
        velocityX = vX;
        velocityY = vY;
    }
    
    public void reset()
    {
        name = "";
        x = 0;
        y = 0;
        w = 0;
        h = 0;
        velocityX = 0;
        velocityY = 0;
        animationTime = 0;
        numOfImages = 0;
        timePerImage = 0;
        continuous = false;
    }
    
    public void move()
    {
        x+=velocityX;
        y+=velocityY;
        
        if(y+h>FLOORY) {
            velocityX = 0;
            velocityY = 0;
        }
    }

    public boolean isStationary()
    {
        if(velocityX==0 && velocityY==0)
            return true;
        return false;
    }
    
    public boolean exists()
    {
        if(w>0)
            return true;
        return false;
    }

    public String getName() {
        return name;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void setY(int h) {
        y = h;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public long getAnimationTime() {
        return animationTime;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int d) {
        damage = d;
    }
}