package feildmaster.OrbEnhance;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ExpEditor {
    private final Player player;

    public ExpEditor(Player player) {
        this.player = player;
    }

    // Handle experience
    public void setExp(int exp) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        if(exp <= 0) return;

        giveExp(exp);
    }

    public void giveExp(int exp) {
        if(exp <= 0) return;

        while(exp > 0) {
            int xp = getExpToLevel()-getExp();
            if(xp > exp)
                xp = exp;
            player.giveExp(xp);
            exp -= xp;
        }
    }

    public void takeExp(int exp) {
        takeExp(exp, true);
    }

    public void takeExp(int exp, boolean fromTotal) {
        if(exp <= 0) return;

        // Now lets make a better takeExp
        setExp(fromTotal?getTotalExp():getExp()-exp);
    }

    // Get experience functions
    public int getExp() {
        return (int) (getExpToLevel()*player.getExp());
    }

    // This function is ugly!
    public int getTotalExp() {
        return player.getTotalExperience();
    }

    public int getLevel() {
        return player.getLevel();
    }

    public int getExpToLevel() {
        return ((CraftPlayer)player).getHandle().getExpTolevel();
    }

    public void recalcTotalExp() {
        int total = 0;

        int level = player.getLevel(); // Store current level

        CraftPlayer cp = (CraftPlayer) player; // CraftBukkit hax

        cp.getHandle().expLevel = 0; // Set level to 0;

        while(cp.getHandle().expLevel < level) { // While level less than real level
            total+=getExpToLevel(); // Add experience to next level
            ++cp.getHandle().expLevel; // Add a level
        }

        total+=getExp(); // Add current experience to total

        player.setTotalExperience(total);
    }
}
