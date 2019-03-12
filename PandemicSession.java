package net.runelite.client.plugins.pandemic;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PandemicSession {

    private int stepsTaken = 0;
    private int damageTaken = 0;
    private final int INITIAL_STEPS = 10;

    private Map<Integer, Integer> regionStepsMap = new HashMap<Integer, Integer>();

    @Getter
    @Setter
    private boolean alert = false;

    private int hitpoints;

    public PandemicSession(int hitpoints){
        this.hitpoints = hitpoints;
    }

    void incrementStepCount(int amount, int region){
        int oldAmount = getStepsGainedInRegion(region);
        int newAmount = oldAmount - amount;
        if(newAmount < 0) newAmount = 0;
        regionStepsMap.put(region, newAmount);

        stepsTaken += amount;
    }

    void incrementDamageCount(int amount, int region){
        int oldAmount = getStepsGainedInRegion(region);
        int newAmount = oldAmount + amount;
        if(newAmount > 10*hitpoints) newAmount = 10*hitpoints;
        regionStepsMap.put(region, newAmount);
        damageTaken += newAmount - oldAmount;
    }

    int getAllowedSteps(){
        return damageTaken - stepsTaken + INITIAL_STEPS;
    }

    boolean canGainSteps(int region){

        if(getStepsGainedInRegion(region)>=hitpoints*10){
            return false;
        }
        return true;
    }

    public int getStepsGainedInRegion(int region){
        if(regionStepsMap.get(region) == null){
            regionStepsMap.put(region, 0);
            return 0;
        }
        return  regionStepsMap.get(region);
    }

    public  int getStepsGainableInRegion(int region){
        return hitpoints*10 - getStepsGainedInRegion(region);
    }

}
