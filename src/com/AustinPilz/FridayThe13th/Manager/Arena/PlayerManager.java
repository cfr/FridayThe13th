package com.AustinPilz.FridayThe13th.Manager.Arena;

import com.AustinPilz.FridayThe13th.Components.Arena;
import com.AustinPilz.FridayThe13th.Components.Counselor;
import com.AustinPilz.FridayThe13th.Components.Jason;
import com.AustinPilz.FridayThe13th.Exceptions.Game.GameFullException;
import com.AustinPilz.FridayThe13th.Exceptions.Game.GameInProgressException;
import com.AustinPilz.FridayThe13th.Exceptions.Player.PlayerAlreadyPlayingException;
import com.AustinPilz.FridayThe13th.FridayThe13th;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager
{
    private Arena arena;

    //Arena Players
    private HashMap<String, Player> players;
    private HashMap<String, Counselor> counselors;
    private Jason jason;

    //Game Stat
    private HashSet<String> alivePlayers;
    private HashSet<String> deadPlayers;

    /**
     * @param arena Arena
     */
    public PlayerManager(Arena arena)
    {
        this.arena = arena;
        this.players = new HashMap<>();
        this.counselors = new HashMap<>();
        this.alivePlayers = new HashSet<>();
        this.deadPlayers = new HashSet<>();
    }

    /**
     * Resets lists of alive and dead players
     */
    public void resetPlayerStorage()
    {
        this.players = new HashMap<>();
        this.counselors = new HashMap<>();
        this.alivePlayers = new HashSet<>();
        this.deadPlayers = new HashSet<>();
    }

    /**
     * Returns the number of players in the game
     * @return
     */
    public int getNumPlayers()
    {
        return players.size();
    }

    /**
     * Returns hash map of current players
     * @return
     */
    public HashMap<String, Player> getPlayers()
    {
        return players;
    }

    /**
     * Returns hash map of current counselors
     * @return
     */
    public HashMap<String, Counselor> getCounselors() { return counselors; }

    public HashSet<String> getAlivePlayers()
    {
        return alivePlayers;
    }

    public HashSet<String> getDeadPlayers()
    {
        return deadPlayers;
    }

    /**
     * Returns the number of players that are alive
     * @return
     */
    public int getNumPlayersAlive()
    {
        return alivePlayers.size();
    }

    /**
     * Returns the number of players that are dead
     * @return
     */
    public int getNumPlayersDead()
    {
        return deadPlayers.size();
    }

    /**
     * Returns the number of counselors
     * @return
     */
    public int getNumCounselors()
    {
        return counselors.size();
    }
    /**
     * Returns if the supplied player is a counselor
     * @param player
     * @return
     */
    public boolean isCounselor(Player player)
    {
        return counselors.containsKey(player.getUniqueId().toString());
    }

    /**
     * Returns if the supplied payer is jason
     * @param player
     * @return
     */
    public boolean isJason(Player player)
    {
        if (jason.getPlayer().equals(player))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns if the player is in the alive players hash map
     * @param player
     * @return
     */
    public boolean isAlive(Player player)
    {
        return alivePlayers.contains(player.getUniqueId().toString());

    }
    /**
     * Returns the counselor object for the player
     * @param player
     * @return
     */
    public Counselor getCounselor (Player player)
    {
        return counselors.get(player.getUniqueId().toString());
    }

    /**
     * Returns the jason object
     * @return
     */

    public Jason getJason () { return jason; }
    /**
     * Returns the counselor object for the player
     * @param playerUUID
     * @return
     */
    public Counselor getCounselor (String playerUUID)
    {
        return counselors.get(playerUUID);
    }

    /**
     * Adds player to the player hash map
     * @param p
     */
    private void addPlayer(Player p)
    {
        players.put(p.getUniqueId().toString(), p);
    }

    /**
     * Removes player from the player hash map
     * @param playerUUID
     */
    private void removePlayer(String playerUUID)
    {
        players.remove(playerUUID);
    }

    /**
     * Calculates if there is room for the player to join the game
     * @return
     */
    private boolean isRoomForPlayerToJoin()
    {
        if ((arena.getLocationManager().getNumberStartingPoints() - getNumPlayers() + 1) > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


   /* Display */

    /**
     * Resets the action bars of all players to nothing
     */
    public void resetPlayerActionBars()
    {
        Iterator it = getPlayers().entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Player player = (Player) entry.getValue();
            ActionBarAPI.sendActionBar(player, "");
        }
    }

    /**
     * Displays the waiting countdown for all players
     */
    public void displayWaitingCountdown()
    {
        Iterator it = getPlayers().entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Player player = (Player) entry.getValue();
            arena.getGameManager().getWaitingCountdownDisplayManager().displayForPlayer(player);
        }
    }

    /**
     * Hides the waiting countdown from all players
     */
    public void hideWaitingCountdown()
    {
        arena.getGameManager().getWaitingCountdownDisplayManager().hideFromAllPlayers();
    }


    /* Player Events */

    /**
     * Adds player to the game, if room is available
     * @param player
     * @throws GameFullException
     */
    public synchronized void playerJoinGame(Player player) throws GameFullException, GameInProgressException
    {
        if (arena.getGameManager().isGameEmpty() || arena.getGameManager().isGameWaiting()) {
            //Determine if there's room for this user
            if (isRoomForPlayerToJoin()) {
                try {
                    //Add to lists
                    FridayThe13th.arenaController.addPlayer(player.getUniqueId().toString(), arena);
                    addPlayer(player);
                    alivePlayers.add(player.getUniqueId().toString());

                    //Waiting actions
                    performWaitingActions(player);

                    //Announce arrival
                    sendMessageToAllPlayers(ChatColor.GRAY + player.getName() + " has joined the game.");

                } catch (PlayerAlreadyPlayingException exception) {
                    //They're already in the controller global player list
                    player.sendMessage(FridayThe13th.pluginPrefix + "Failed to add you to game because you're already registered as playing a game.");
                }
            } else {
                throw new GameFullException();
            }
        }
        else
        {
            throw new GameInProgressException();
        }
    }

    /**
     * Performs actions when a player logs off of server
     * @param playerUUID
     */
    public void onPlayerLogout(String playerUUID)
    {
        //Hurry and see if we can teleport them out and clear inventory
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        player.teleport(arena.getReturnLocation());
        player.getInventory().clear();

        //Cleanup
        performPlayerCleanupActions(playerUUID);

        if (arena.getGameManager().isGameInProgress()) {

            if (isJason(player)) {
                //Jason logged off, so end the game
                sendMessageToAllPlayers(ChatColor.RED + "GAME OVER! " + ChatColor.WHITE + player.getName() + " (Jason) logged off and quit the game.");
                arena.getGameManager().endGame();
            } else {
                //They're a counselor
                if (getNumPlayersAlive() <= 1) {
                    //They were the last one
                    jasonWins();
                    arena.getGameManager().endGame();
                }
            }
        }


        //Message everyone in game
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
        sendMessageToAllPlayers(ChatColor.GRAY + offlinePlayer.getName() + " has logged out and left the game.");
    }

    /**
     * Performs actions when a player quits the game via command
     * @param player
     */
    public void onplayerQuit(Player player)
    {
        //Clean up
        performPlayerCleanupActions(player.getUniqueId().toString());

        if (arena.getGameManager().isGameInProgress()) {

            if (isJason(player)) {
                //Jason quit off, so end the game
                sendMessageToAllPlayers(ChatColor.RED + "GAME OVER! " + ChatColor.WHITE + player.getName() + " (Jason) left the game.");
                arena.getGameManager().endGame();
            } else {
                //They're a counselor
                if (getNumPlayersAlive() <= 1) {
                    //They were the last one
                    jasonWins();
                    arena.getGameManager().endGame();
                }
            }
        }

        //Message everyone in game
        sendMessageToAllPlayers(ChatColor.GRAY + player.getName() + " has left the game.");
    }

    public void onPlayerDeath(Player player)
    {
        if (arena.getGameManager().isGameInProgress()) {
            //Transition from alive to dead hash set
            alivePlayers.remove(player.getUniqueId().toString());
            deadPlayers.add(player.getUniqueId().toString());

            //Check to see if they're jason, which would end the game
            if (isJason(player)) {
                //Counselors win
                counselorsWin();

                arena.getGameManager().endGame(); //Game over kiddos
            } else {
                //They're a normal player, see if there are still others alive
                if (getNumPlayersAlive() > 1) //since jason is still presumably alive
                {
                    //They're are others still alive, enter spectating mode
                    getCounselor(player).enterSpectatingMode();
                } else {
                    //They were the last to die, so end the game
                    jasonWins();
                    arena.getGameManager().endGame();
                }
            }
        }

        //Let everyone know
        sendMessageToAllPlayers(ChatColor.GRAY + player.getName() + " was " + ChatColor.RED + "killed" + ChatColor.WHITE + ".");
    }




    /* Player Preparation Actions */

    /**
     * Performs waiting actions for specific player
     * @param player
     */
    private void performWaitingActions(Player player)
    {
        //Teleport player to waiting location
        teleportPlayerToWaitingPoint(player);

        //Change game mode & clear inventory
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
    }

    /**
     * Transitions and prepares all players to play the game
     */
    protected void performInProgressActions()
    {
        //Assign roles and teleport players there
        assignGameRoles();
        assignSpawnLocations();

        //Display player bars
        Iterator it = getCounselors().entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Counselor counselor = (Counselor) entry.getValue();

            counselor.prepareForGameplay();
        }

        //Jason stuff
        jason.prepapreForGameplay();

    }

    /**
     * Assigns all players a role (counselor or jason)
     */
    private void assignGameRoles()
    {
        Random generator = new Random();
        Object[] playerArray = players.values().toArray();
        int jasonCell = generator.nextInt(playerArray.length);

        //Select Jason
        Player jasonPlayer = (Player)playerArray[jasonCell];
        this.jason = new Jason(jasonPlayer, arena);

        //Make everyone else counselors
        for (int i = 0; i < playerArray.length; i++)
        {
            if (i != jasonCell)
            {
                Player counselorPlayer = (Player)playerArray[i];
                this.counselors.put(counselorPlayer.getUniqueId().toString(), new Counselor(counselorPlayer, arena));
            }
        }
    }

    /**
     * Assigns and teleports players and jason to their spawn locations
     */
    private void assignSpawnLocations()
    {
        //Teleport jason to jason start point
        jason.getPlayer().teleport(arena.getJasonStartLocation());

        //Teleport counselors to starting points

        Location[] counselorLocations = arena.getLocationManager().getAvailableStartingPoints().toArray(new Location[arena.getLocationManager().getAvailableStartingPoints().size()]);

        Iterator it = getCounselors().entrySet().iterator();
        int i = 0;
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Counselor counselor = (Counselor) entry.getValue();
            counselor.getPlayer().teleport(counselorLocations[i++]);
        }
    }

    /**
     * Transtions players from the game once it ends
     */
    protected void performEndGameActions()
    {
        //Game ended
        sendMessageToAllPlayers("Game over! Thanks for playing Friday the 13th.");

        Iterator it = getPlayers().entrySet().iterator();
        int i = 0;
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Player player = (Player) entry.getValue();
            it.remove();
            performPlayerCleanupActions(player.getUniqueId().toString());

        }
    }

    /**
     * Cleans up a player and restores them to pre-game status
     * @param playerUUID
     */
    private void performPlayerCleanupActions(String playerUUID)
    {
        //Get offline player to be able to account for both online and offline players
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

        //Remove player from hash maps (have to remove these first since listeners check to see if player has an arena)
        FridayThe13th.arenaController.removePlayer(playerUUID);
        removePlayer(playerUUID);
        alivePlayers.remove(playerUUID);
        deadPlayers.remove(playerUUID);

        if (arena.getGameManager().isGameWaiting() || arena.getGameManager().isGameEmpty())
        {
            //Waiting mode, so just teleport them out
            if (offlinePlayer.isOnline())
            {
                //Teleport them to the return point
                teleportPlayerToReturnPoint(Bukkit.getPlayer(UUID.fromString(playerUUID)));
            }
            else
            {
                //They're no longer online?
            }
        }
        else
        {
            if (isCounselor((Player)offlinePlayer))
            {
                Counselor counselor = getCounselor(playerUUID);

                //Cancel scheduled tasks
                counselor.cancelTasks();

                //Remove any potions
                counselor.removeAllPotionEffects();

                //Leave spectating mode, if applicable
                if (counselor.isInSpectatingMode())
                {
                    counselor.leaveSpectatingMode();
                }

                //Hide the stats bars
                counselor.getStatsDisplayManager().hideStats();

                //Remove from counselors
                counselors.remove(playerUUID);
            }
            else if (isJason((Player)offlinePlayer))
            {
                //Stop tasks
                jason.cancelTasks();

                //Remove his ability display
                jason.getAbilityDisplayManager().hideAbilities();


            }

            //Actions done only if they're online
            if (offlinePlayer.isOnline())
            {
                Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));

                //Hide scoreboards
                arena.getGameManager().getScoreboardManager().hideSidebars(player);

                //Teleport them to the return point
                teleportPlayerToReturnPoint(player);

                //Return normal walking speed
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.5f);

                //Clear inventory
                player.getInventory().clear();
            }
        }
    }




    /* Teleports */

    /**
     * Teleports the player to the arena's return point
     * @param player
     */
    private void teleportPlayerToReturnPoint(Player player)
    {
        player.teleport(arena.getReturnLocation());
    }

    /**
     * Teleports the player to the arena's waiting point
     * @param player
     */
    private void teleportPlayerToWaitingPoint(Player player)
    {
        player.teleport(arena.getWaitingLocation());
    }





    /* Messaging */

    /**
     * Sends in game message to all players
     * @param message
     */
    private void sendMessageToAllPlayers(String message)
    {
        Iterator it = getPlayers().entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Player player = (Player) entry.getValue();
            player.sendMessage(FridayThe13th.pluginPrefix + message);
        }
    }

    /**
     * Performs actions when counselors win
     */
    private void counselorsWin()
    {
        sendMessageToAllPlayers("Counselors " + ChatColor.GREEN + "WIN" + ChatColor.WHITE + "! Jason was slain.");
    }

    /**
     * Performs actions when jason wins
     */
    private void jasonWins()
    {
        sendMessageToAllPlayers("Jason " + ChatColor.GREEN + "WINS" + ChatColor.WHITE + "! " + getNumPlayersDead() + "/" + getNumCounselors() + " counselors killed.");
    }

}
