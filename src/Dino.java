public class Dino extends Enemy
{
    public Dino(int locX, boolean facingRight)
    {
        //33 x 30
        super("dino",22,2,5,15,facingRight,locX,264,240);
        
        initIdle(1,100);
        initMove(4,300);
        initAttack(2,400);
        initHit(1,100);
        initDeath(1,300);
    }
    
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()!='a') {
            int hurtX=getX(), hurtY=getY(), hurtW=200, hurtH=240;
            if(!isFacingRight()) {
                hurtX = getX()+getW()-(hurtX+hurtW-getX());
            }
            getHurtbox().update(hurtX+backgroundX,hurtY+backgroundY,hurtW,hurtH); //25 x 30
            getHitbox().update(0,0,0,0);
        }
        else {
            int cycle = (int)((currentTime-getAnimationTime()-1)/getAttackSpeed())+1;
            int hurtX=getX(), hurtY=getY(), hurtW=200, hurtH=240;
            int hitX=0, hitY=0, hitW=0, hitH=0;
            if(cycle==2) {
                hurtY = getY()+24; //0,3,33,27
                hurtW = 264;
                hurtH = 216;
                hitX = getX()+96; //12,6,21,24
                hitY = getY()+48;
                hitW = 168;
                hitH = 192;
            }
            
            if(!isFacingRight()) {
                hurtX = getX()+getW()-(hurtX+hurtW-getX());
                hitX = getX()+getW()-(hitX+hitW-getX());
            }

            getHurtbox().update(hurtX+backgroundX,hurtY+backgroundY,hurtW,hurtH);
            getHitbox().update(hitX+backgroundX,hitY+backgroundY,hitW,hitH);
        }
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=15) //15%
            return new Consumable("Elixir of Vitality");
        else if(rand<=30) //15%
            return new Consumable("Elixir of Sorcery");
        else if(rand<=40) //10%
            return new Consumable("Elixir of Iron");
        else if(rand<=50) //10%
            return new Consumable("Elixir of Energy");
        else if(rand<=58) //8%
            return new Artifact("Catalyst of Aeons");
        else if(rand<=66) //8%
            return new Artifact("Aegis of the Legion");
        else if(rand<=74) //8%
            return new Artifact("Irwin's Deathcap");
        else if(rand<=82) //7%
            return new Artifact("Joshua's Riotmail");
        else if(rand<=86) //4%
            return new Artifact("Arnav's Spirit");
        else if(rand<=90) //4%
            return new Artifact("Alex's Guardian Orb");
        else if(rand<=93) //3%
            return new Artifact("Anisara's Glacial Shroud");
        return new Item();
    }
}