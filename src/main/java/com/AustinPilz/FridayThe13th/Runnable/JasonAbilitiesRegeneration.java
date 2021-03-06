package com.AustinPilz.FridayThe13th.Runnable;

import com.AustinPilz.FridayThe13th.Components.Characters.Jason;

public class JasonAbilitiesRegeneration implements Runnable
{
    Jason jason;

    public JasonAbilitiesRegeneration (Jason jason)
    {
        this.jason = jason;
    }

    @Override
    public void run()
    {
        //Check to see if initial generations reached
        if (!jason.hasInitialStalkGenerationCompleted())
        {
            if (jason.getStalkLevelPercentage() == 1)
            {
                jason.setInitialStalkGenerationCompleted(true);
            }
        }

        //Regenerate Values
        if (!jason.getPlayer().isSneaking())
        {
            jason.regenerateStalking();
        }

        //Sense - Requires stalk been init first
        if (!jason.isSenseActive() && jason.hasInitialStalkGenerationCompleted())
        {
            jason.regenerateSense();

            if (!jason.hasInitialSenseGenerationCompleted() && jason.getSenseLevelPercentage() == 1)
            {
                jason.setInitialSenseGenerationCompleted(true);
            }
        }

        //Sense - Depletion
        if (jason.isSenseActive())
        {
            if (jason.getSenseLevel() > 0)
            {
                //It's active, degen
                jason.setSensing(true);
            }
            else
            {
                //It's out
                jason.setSenseActive(false); //Ran out of sense level
            }
        }

        //Warp
        if (!jason.isWarpActive() && jason.hasInitialSenseGenerationCompleted())
        {
            jason.regenerateWarp();

            if (!jason.hasInitialWarpGenerationCompleted() && jason.getWarpLevelPercentage() == 1)
            {
                jason.setInitialWarpGenerationCompleted(true);
            }
        }

        if (jason.isWarpActive())
        {
            if (jason.getWarpLevelPercentage() > 0)
            {
                jason.setWarping(true);
            }
            else
            {
                //Ran out
                jason.setWarpActive(false);
            }
        }
    }
}
