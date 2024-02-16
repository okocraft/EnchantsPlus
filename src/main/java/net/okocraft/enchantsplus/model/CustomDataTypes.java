package net.okocraft.enchantsplus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.okocraft.enchantsplus.enchant.EnchantPlus;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomDataTypes {

    public static final NamespacedKey ID_KEY = new NamespacedKey("enchantsplus", "id");
    public static final NamespacedKey LEVEL_KEY = new NamespacedKey("enchantsplus", "level");

    public static final PersistentDataType<PersistentDataContainer[], EnchantPlusData> ENCHANT_PLUS_DATA = new PersistentDataType<>(){

        @Override
        public Class<PersistentDataContainer[]> getPrimitiveType() {
            return PersistentDataContainer[].class;
        }

        @Override
        public Class<EnchantPlusData> getComplexType() {
            return EnchantPlusData.class;
        }

        @Override
        public PersistentDataContainer[] toPrimitive(EnchantPlusData complex, PersistentDataAdapterContext context) {
            List<PersistentDataContainer> enchantPairContainerList = new ArrayList<>();
            PersistentDataContainer enchantPairContainer;
            for (Map.Entry<EnchantPlus, Integer> entry : complex.enchantments.entrySet()) {
                enchantPairContainer = context.newPersistentDataContainer();
                enchantPairContainer.set(ID_KEY, STRING, entry.getKey().getId());
                enchantPairContainer.set(LEVEL_KEY, INTEGER, entry.getValue());
                enchantPairContainerList.add(enchantPairContainer);
            }
            return enchantPairContainerList.toArray(PersistentDataContainer[]::new);
        }

        @Override
        public EnchantPlusData fromPrimitive(PersistentDataContainer[] primitive, PersistentDataAdapterContext context) {
            Map<EnchantPlus, Integer> enchantments = new HashMap<>();
            for (PersistentDataContainer container : primitive) {
                EnchantPlus enchant = EnchantPlus.fromId(container.getOrDefault(ID_KEY, STRING, "null"));
                int level = container.getOrDefault(LEVEL_KEY, INTEGER, 0);
                if (enchant == null || level == 0) {
                    continue;
                }
                enchantments.put(enchant, level);
            }
            return new EnchantPlusData(enchantments);
        }
    };
}
