public class Crab extends Enemy
{
    private boolean angry;
    
    public Crab(int location, boolean facingRight)
    {
        //25 x 14
        super("crab", 1, 6, 1, 4, facingRight, location, 100,70);
        
        initIdle(1,100);
        initMove(1, 300);
        initReact(4,130);
        initAttack(5,150);
        initDeath(1, 300);
        initHit(1,100);
        
        angry = false;
    }
    
    public Crab(int location, boolean facingRight, boolean isAngry)
    {
        //25 x 14
        super("crab", 1, 6, 1, 4, facingRight, location, 100,70);
        
        initIdle(1,100);
        initMove(1, 300);
        initReact(4,130);
        initAttack(5,150);
        initDeath(1, 300);
        initHit(1,100);
        
        angry = isAngry;
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        alertness(h,currentTime);
        if(angry)
            super.act(h, currentTime, backgroundX, backgroundY);
        else if(getAction()=='m') {
            //moves opposite to direction it faces
            if(isFacingRight())
                move(-getMobility(),0);
            else
                move(getMobility(),0);
        }
    }
    
    @Override
    public void postAttack()
    {
        if(isAlert()) {
            setAction('m');
            if(getX()<=100 || getX()+getW()>=4220)
                turnAround();
        }
        else
            setAction('i');
    }
    
    @Override
    public void postMove()
    {
        if(isAlert())
            setAction('a');
        else
            setAction('i');
    }
    
    @Override
    public void postReact()
    {
        super.setAction('m');
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //5,5,13,9
        int hurtX=getX()+20, hurtY=getY()+20, hurtW=52, hurtH=36;
        int hitX=0, hitY=0, hitW=0, hitH=0;
        if(getAction()=='a') {
            //5,0,20,14
            hitX = getX()+20;
            hitY = getY();
            hitW = 100;
            hitH = 70;
        }
        
        if(!isFacingRight()) {
            hurtX = getX()+getW()-(hurtX+hurtW-getX());
            hitX = getX()+getW()-(hitX+hitW-getX());
        }
        
        getHurtbox().update(hurtX+backgroundX, hurtY+backgroundY, hurtW, hurtH);
        getHitbox().update(hitX+backgroundX,hitY+backgroundY,hitW,hitH);
        
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=4) //4%
            return new Consumable("Elixir of Vitality");
        else if(rand<=8) //4%
            return new Consumable("Elixir of Sorcery");
        else if(rand<=28) //20%
            return new Consumable("Elixir of Iron");
        else if(rand<=48) //20%
            return new Consumable("Elixir of Energy");
        else if(rand<=56) //8%
            return new Artifact("Berserker's Greaves");
        else if(rand<=64) //8%
            return new Artifact("Sorcerer's Shoes");
        else if(rand<=72) //8%
            return new Artifact("Boots of Swiftness");
        else if(rand<=92) //20%
            return new Artifact("Boots of Mobility");
        else if(rand<=94) //2%
            return new Consumable("Suspicious Cocktail");
        return new Item();
    }
    
}