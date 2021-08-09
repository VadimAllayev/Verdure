public class Beetle extends Enemy
{
    public Beetle(int locationX, boolean facingRight)
    {
        //22,20
        super("beetle",1,4,1,2,facingRight,locationX,88,80);
        
        initMove(4,50);
        initAttack(6,100);
        initDeath(1,300);
        initHit(1,100);
        
        super.setY(430); //default y-coordinate
        super.setAction('m');
    }
    
    /*public Beetle(int locationX, int locationY, boolean facingRight)
    {
        super("beetle",locationX,locationY,facingRight);
        super.setAction('m');
    }*/
    
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //hurtbox - to be updated
        super.getHurtbox().update(getX()+backgroundX,getY()+backgroundY,getW(),getH());
        
        //hitbox
        int x=0,y=0,w=0,h=0;
        if(getAction()=='a')
        {
            int cycle = (int)((currentTime-getAnimationTime()-1)/getAttackSpeed())+1;
            if(cycle==4) {
                x = getX()+56; //14
                y = getY()+32; //8
                w = 32; //8
                h = 32; //8
            }
            else if(cycle==5) {
                x = getX()+52; //13
                y = getY()+32; //8
                w = 32; //8
                h = 32; //8
            }
            
            if(!isFacingRight())
                x = getX()+getW()-(x+w-getX());
        }
        getHitbox().update(x+backgroundX,y+backgroundY,w,h);
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=35) //35%
            return new Consumable("Health Potion");
        else if(rand<=45) //10%
            return new Consumable("Mana Potion");
        else if(rand<=60) //15%
            return new Artifact("Boots of Speed");
        else if(rand<=65) //5%
            return new Artifact("Ruby Crystal");
        return new Item();
    }
    
}