package net.okocraft.enchantsplus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.okocraft.enchantsplus.enchant.EnchantPlus;
import net.okocraft.enchantsplus.util.NamespacedKeyManager;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomDataTypes {

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
                enchantPairContainer.set(NamespacedKeyManager.ID_KEY, STRING, entry.getKey().getId());
                enchantPairContainer.set(NamespacedKeyManager.LEVEL_KEY, INTEGER, entry.getValue());
                enchantPairContainerList.add(enchantPairContainer);
            }
            return enchantPairContainerList.toArray(PersistentDataContainer[]::new);
        }

        @Override
        public EnchantPlusData fromPrimitive(PersistentDataContainer[] primitive, PersistentDataAdapterContext context) {
            Map<EnchantPlus, Integer> enchantments = new HashMap<>();
            for (PersistentDataContainer container : primitive) {
                EnchantPlus enchant = EnchantPlus.fromId(container.getOrDefault(NamespacedKeyManager.ID_KEY, STRING, "null"));
                int level = container.getOrDefault(NamespacedKeyManager.LEVEL_KEY, INTEGER, 0);
                if (enchant == null || level == 0) {
                    continue;
                }
                enchantments.put(enchant, level);
            }
            return new EnchantPlusData(enchantments);
        }
    };

    public static final PersistentDataType<PersistentDataContainer[], String[]> STRING_ARRAY = new PersistentDataType<>(){

        @Override
        public Class<PersistentDataContainer[]> getPrimitiveType() {
            return PersistentDataContainer[].class;
        }

        @Override
        public Class<String[]> getComplexType() {
            return String[].class;
        }

        @Override
        public PersistentDataContainer[] toPrimitive(String[] complex, PersistentDataAdapterContext context) {
            PersistentDataContainer[] result = new PersistentDataContainer[complex.length];
            PersistentDataContainer element;
            for (int i = 0; i < complex.length; i++) {
                element = context.newPersistentDataContainer();
                element.set(NamespacedKeyManager.TEXT_KEY, STRING, complex[i]);
                result[i] = element;
            }
            return result;
        }

        @Override
        public String[] fromPrimitive(PersistentDataContainer[] primitive, PersistentDataAdapterContext context) {
            String[] result = new String[primitive.length];
            for (int i = 0; i < primitive.length; i++) {
                result[i] = primitive[i].get(NamespacedKeyManager.TEXT_KEY, STRING);
            }
            return result;
        }

    };


}
