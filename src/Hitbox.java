import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class Hitbox implements Serializable
{
    private int x,y,w,h;
    
    public Hitbox()
    {
        x = 0;
        y = 0;
        w = 0;
        h = 0;
    }
    
    public Hitbox(int i1, int i2, int i3, int i4)
    {
        x = i1;
        y = i2;
        w = i3;
        h = i4;
    }
    
    public void update(int i1, int i2, int i3, int i4)
    {
        x = i1;
        y = i2;
        w = i3;
        h = i4;
    }
    
    public String toString()
    {
        return "x: "+x+"\ny: "+y+"\nw: "+w+"\nh: "+h;
    }
    
    public boolean overlaps(Hitbox hb)
    {
        if(w>0 && x<=hb.getX()+hb.getW() && x+w>=hb.getX()) //x
        {
            if(y<=hb.getY()+hb.getH() && y+h>=hb.getY()) //y
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean overlaps(Projectile proj)
    {
        if(w>0 && x<=proj.getX()+proj.getW() && x+w>=proj.getX()) //x
        {
            if(y<=proj.getY()+proj.getH() && y+h>=proj.getY()) //y
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean overlaps(Projectile proj, int bX, int bY)
    {
        if(w>0 && x<=proj.getX()+proj.getW()+bX && x+w>=proj.getX()+bX) //x
        {
            if(y<=proj.getY()+proj.getH()+bY && y+h>=proj.getY()+bY) //y
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean overlaps(Item i, int bX, int bY)
    {
        if(w>0 && x<=i.getX()+i.getW()+bX && x+w>=i.getX()+bX) //x
        {
            if(y<=i.getY()+i.getH()+bY && y+h>=i.getY()+bY) //y
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Used to find the distance between two hitboxes.
     * @param hb another Hitbox.
     * @return The distance between the centers (in the x-dimension) of
     *  this Hitbox and Hitbox hb. If positive, this Hitbox is
     * to the right of hb. If negative, this Hitbox is to the left of hb.
     */
    public int distanceFrom(Hitbox hb)
    {
        return x+w/2-(hb.getX()+hb.getW()/2);
    }
    
    /**
     * Draws this hitbox.
     * @param g Graphics.
     * @param hit Whether this is a hitbox (true) or a hurtbox false).
     */
    public void drawSelf(Graphics g,boolean hit)
    {
        if(hit)
            g.setColor(Color.BLUE);
        else
            g.setColor(Color.RED);
        g.drawRect(x,y,w,h);
    }
    
    
    public boolean isRightOf(Hitbox hb)
    {
        if(x+w/2>hb.getX()+hb.getW()/2)
            return true;
        return false;
    }
    
    public boolean isLeftOf(Hitbox hb)
    {
        if(x+w/2<hb.getX()+hb.getW()/2)
            return true;
        return false;
    }
    
    public Hitbox translated(int dx, int dy)
    {
        return new Hitbox(x+dx,y+dy,w,h);
    }
    
    public boolean exists()
    {
        if(w>0)
            return true;
        return false;
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
    
    
}