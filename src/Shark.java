public class Shark extends Enemy
{
    private int contact; //x coordinate where shark made contact with user
    
    public Shark(int location, boolean facingRight)
    {
        //40 x 42
        super("shark",1,5,3,0,facingRight,location,200,210);
        
        initMove(8,80);
        initReact(5,80);
        initDeath(6,80);
        initHit(1,80);
        
        contact = -1;
        setAction('m');
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='m' || getAction()=='h')
        {
            if(isFacingRight())
                move(getMobility(),0);
            else
                move(-getMobility(),0);
        }
        
        if(getHurtbox().overlaps(h.getHurtbox()))
        {
            contact = getX();
        }
        
        if(contact!=-1 && Math.abs(contact-getX())>300)
        {
            setAnimationTime(currentTime);
            setAction('r');
            contact = -1;
        }
    }
    
    @Override
    public void postReact()
    {
        setFacingRight(!isFacingRight());
        setAction('m');
        if(isFacingRight())
            move(-50,0);
        else
            move(50,0);
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //children will call this themselves
        //this is default hurtbox
        getHurtbox().update(getX()+backgroundX,getY()+backgroundY,getW(),getH());
    }
}