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

    public int getTotalExp() {
        return player.getTotalExperience();
    }

    public int getLevel() {
        return player.getLevel();
    }

    public int getExpToLevel() {
        return ((CraftPlayer)player).getHandle().getExpTolevel();
    }
}
