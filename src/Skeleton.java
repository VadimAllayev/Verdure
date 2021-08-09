public class Skeleton extends Enemy
{
    public Skeleton(int location, boolean facingRight)
    {
        //50 x 37
        super("skeleton",13,2,3,10,facingRight,location,250,185);
        initIdle(11,100);
        initReact(4,150);
        initHit(7,80);
        initAttack(18,80);
        initDeath(15,80);
        initMove(13,80);
    }
    
    @Override
    public void getHurt(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        //if currently not being hit or dying
        if(getAction()!='h' && getAction()!='d')
        {
            Hitbox ouchies = h.getHitbox();
            //if enemy got hit
            if(ouchies.exists() && getHurtbox().overlaps(ouchies) && getHitTime()<currentTime)
            {
                takeDamage(h.getPower());
                if(getLife()==0)
                    setAction('d'); //death
                else if(getAction()!='a')
                    setAction('h'); //hit
                setAnimationTime(currentTime);
                setHitTime(currentTime+500);
            }
            
            for(int i=0; i<h.getProjectiles().size();i++)
            {
                Projectile proj = h.getProjectiles().get(i);
                if(getHurtbox().overlaps(proj,backgroundX,backgroundY) && getHitTime()<currentTime)
                {
                    takeDamage(proj.getDamage());
                    if(proj.isContinuous())
                        proj.reset();
                    if(getLife()==0)
                        setAction('d'); //death
                    else if(getAction()!='a')
                        setAction('h'); //hit
                    setAnimationTime(currentTime);
                    setHitTime(currentTime+500);
                }
            }
        }
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        int hurtX=getX()+50, hurtY=getY()+65, hurtW=80, hurtH=120;
        
        if(!isFacingRight()) {
            hurtX = getX()+getW()-(hurtX+hurtW-getX());
        }
        
        getHurtbox().update(hurtX+backgroundX,hurtY+backgroundY,hurtW,hurtH); //10,13,16,24
        
        if(super.getAction()=='a')
        {
            int cycle = (int)((currentTime-getAnimationTime()-1)/getAttackSpeed())+1;
            int hitX=0, hitY=0, hitW=0, hitH=0;
            if(cycle==8) {
                hitX = getX()+50;
                hitY = getY()+5;
                hitW = 200;
                hitH = 180;
                //10,1,40,36
            }
            else if(cycle==9) {
                hitX = getX()+185;
                hitY = getY()+155;
                hitW = 55;
                hitH = 30;
                //37,31,11,6
            }
            else if(cycle==10) {
                hitX = getX()+180;
                hitY = getY()+150;
                hitW = 55;
                hitH = 35;
                //36,30,11,7
            }
            
            if(!isFacingRight()) {
                hitX = getX()+getW()-(hitX+hitW-getX());
            }
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
        else if(rand<=55) //5%
            return new Artifact("Michelle's Cutlass");
        else if(rand<=60) //5%
            return new Artifact("Allen's Gladius");
        else if(rand<=65) //5%
            return new Artifact("Obsidian Cleaver");
        else if(rand<=69) //4%
            return new Artifact("Blade of Jacob");
        else if(rand<=72) //3%
            return new Artifact("Anahi's Ghostblade");
        else if(rand<=75) //3%
            return new Artifact("Justin's Zeal");
        else if(rand<=77) //2%
            return new Artifact("Trinity Force");
        else if(rand<=80) //3%
            return new Consumable("Suspicious Cocktail");
        else if(rand<=85) //5%
            return new Artifact("Chain Vest");
        return new Item();
    }
    
}