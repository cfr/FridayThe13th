package com.AustinPilz.FridayThe13th.Manager.Display;

import com.AustinPilz.FridayThe13th.Components.Arena;
import com.AustinPilz.FridayThe13th.FridayThe13th;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;

public class GameCountdownManager
{
    private Arena arena;

    public GameCountdownManager (Arena arena)
    {
        this.arena = arena;
    }

    public void updateCountdown()
    {
        if (arena.getGameManager().getGameTimeLeft() == 0)
        {
            arena.getGameManager().gameTimeUp(); //game ran out of time
        }
        else
        {
            int rem = arena.getGameManager().getGameTimeLeft() % 3600;
            int mn = rem / 60;
            int sec = rem % 60;

            if (mn > 1 && sec == 0)
            {
                //Every whole minute
                Iterator it = arena.getGameManager().getPlayerManager().getPlayers().entrySet().iterator();
                while (it.hasNext())
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    Player player = (Player) entry.getValue();
                    ActionBarAPI.sendActionBar(player, "Time Left: " + mn + " minutes", 60);
                }
            }
            else if (mn == 0 && (sec == 30 || sec == 20 || sec == 10 || sec == 5))
            {
                //Special
                Iterator it = arena.getGameManager().getPlayerManager().getPlayers().entrySet().iterator();
                while (it.hasNext())
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    Player player = (Player) entry.getValue();
                    ActionBarAPI.sendActionBar(player, "Time Left: " + sec + " seconds", 60);
                }
            }
        }
    }
}