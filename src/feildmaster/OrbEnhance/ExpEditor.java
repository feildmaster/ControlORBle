package feildmaster.OrbEnhance;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ExpEditor {
    private CraftPlayer cp;

    public ExpEditor(Player player) {
        cp = (CraftPlayer)player;
    }

    // Handle experience
    public void setExp(int exp) {
        cp.setExp(0);
        cp.setLevel(0);
        cp.setTotalExperience(0);

        if(exp <= 0) return;

        cp.giveExp(exp);
    }

    public void giveExp(int exp) {
        if(exp < 0) return;

        cp.giveExp(exp);
    }

    public void takeExp(int exp) {
        takeExp(exp, true);
    }

    public void takeExp(int exp, boolean fromTotal) {
        if(exp < 0) return;

        setExp(fromTotal?getTotalExp():getExp()-exp);
    }

    // Get experience functions
    public int getExp() {
        return (int) (getExpToLevel()*cp.getExp());
    }

    public int getTotalExp() {
        return cp.getTotalExperience();
    }

    public int getLevel() {
        return cp.getLevel();
    }

    public int getExpToLevel() {
        return cp.getHandle().getExpTolevel();
    }
}
