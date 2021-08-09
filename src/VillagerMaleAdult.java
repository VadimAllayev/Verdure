public class VillagerMaleAdult extends Enemy
{
    public VillagerMaleAdult(int location, boolean facingRight)
    {
        //48, 48
        super("villagerMaleAdult", 1, 1, 0, 0, facingRight, location, 192, 192);
        initIdle(4,200);
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //invulnerable
        getHurtbox().update(0,0,0,0);
    }
    
    
}