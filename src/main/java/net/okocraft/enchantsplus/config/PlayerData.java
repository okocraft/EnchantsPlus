package net.okocraft.enchantsplus.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.okocraft.enchantsplus.EnchantsPlus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData extends CustomConfig {

    public final Map<UUID, Boolean> particlesCache = new HashMap<>();

    public PlayerData(EnchantsPlus plugin) {
        super(plugin, "playerdata.yml");
    }

    @Override
    public void reload() {
        super.reload();
        clearCache();
    }
    
    public void clearCache() {
        particlesCache.clear();
    }

    private void setBoolean(UUID uid, String prop, boolean value) {
        get().set(uid.toString() + get().options().pathSeparator() + prop, value);
        save();
    }

    private boolean getBoolean(UUID uid, String prop) {
        return get().getBoolean(uid.toString() + get().options().pathSeparator() + prop, true);
    }

    public boolean getNotifications(Player player) {
        return getNotifications(player.getUniqueId());
    }

    public void setNotifications(Player player, boolean notifications) {
        setNotifications(player.getUniqueId(), notifications);
    }
    
    public boolean getNotifications(UUID uid) {
        return getBoolean(uid, "notifications");
    }
    
    public void setNotifications(UUID uid, boolean notifications) {
        setBoolean(uid, "notifications", notifications);
    }

    public boolean isCustomEnchantsDisabled(UUID uid) {
        return !getBoolean(uid, "custom-enchants");
    }

    public boolean toggleCustomEnchants(UUID uid) {
        var newValue = !isCustomEnchantsDisabled(uid);
        setBoolean(uid, "custom-enchants", newValue);
        return newValue;
    }
    
    public boolean getParticles(UUID uid) {
        return particlesCache.computeIfAbsent(uid, u -> getBoolean(u, "particles"));
    }
    
    public void setParticles(UUID uid, boolean particles) {
        setBoolean(uid, "particles", particles);
        if (particles) {
            particlesCache.remove(uid);
        } else {
            particlesCache.put(uid, false);
        }
    }

    public boolean getSounds(UUID uid) {
        return getBoolean(uid, "sounds");
    }

    public void setSoulbounded(UUID uid, List<ItemStack> items) {
        get().set(uid + ".soulbound", items);
        save();
    }

    public void addSoulbounded(UUID uid, ItemStack item) {
        List<ItemStack> soulbounded = getSoulbounded(uid);
        soulbounded.add(item);
        setSoulbounded(uid, soulbounded);
    }
    
    public void addAllSoulBounded(UUID uid, List<ItemStack> items) {
        List<ItemStack> soulbounded = getSoulbounded(uid);
        soulbounded.addAll(items);
        setSoulbounded(uid, soulbounded);
    }

    public void clearSoulbounded(UUID uid) {
        get().set(uid + ".soulbound", null);
        save();
    }

    public List<ItemStack> getSoulbounded(UUID uid) {
        List<ItemStack> items = new ArrayList<>();
        List<?> list = get().getList(uid + ".soulbound");
        if (list == null) {
            return items;
        }
        for (Object element : list) {
            if (element instanceof ItemStack) {
                items.add((ItemStack) element);
            }
        }
        return items;

    }
}
