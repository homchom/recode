package io.github.homchom.recode.sys.hypercube.codeaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.sys.util.StringUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class Argument {
    private final boolean valueArgument;
    private final ValueType type;
    private final boolean plural;
    private final boolean optional;
    private final String[] description;
    private final HashSet<String[]> notes;
    private final String text;

    Argument(JsonObject jsonObject){
        if (jsonObject.has("text")){
            this.valueArgument = false;
            this.type = null;
            this.text = jsonObject.get("text").getAsString();
            this.plural = false;
            this.optional = false;
            this.description = null;
            this.notes = null;
        }else{
            this.valueArgument = true;
            this.type = ValueType.fromID(jsonObject.get("type").getAsString());
            this.plural = jsonObject.get("plural").getAsBoolean();
            this.optional = jsonObject.get("optional").getAsBoolean();
            this.description = StringUtil.toStringArray(jsonObject.get("description").getAsJsonArray());
            this.notes = StringUtil.toStringListHashSet(jsonObject.get("notes").getAsJsonArray());
            this.text = null;
        }
    }

    public String[] getDescription() {
        return description;
    }

    public ValueType getType() {
        return type;
    }

    public HashSet<String[]> getNotes() {
        return notes;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isValueArgument() {
        return valueArgument;
    }

    public boolean isPlural() {
        return plural;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "type='" + type + '\'' +
                ", plural=" + plural +
                ", optional=" + optional +
                ", description=" + Arrays.toString(description) +
                ", notes=" + notes +
                ", text='" + text + '\'' +
                '}';
    }

    public enum ValueType {
        NONE("NONE"),
        VARIABLE("VARIABLE", "var"),
        GAME_VALUE("GAME_VALUE", "g_val"),
        NUMBER("NUMBER", "num"),
        TEXT("TEXT", "txt"),
        PARTICLE("PARTICLE", "part"),
        LOCATION("LOCATION", "loc"),
        VECTOR("VECTOR", "vec"),
        SOUND("SOUND", "snd"),
        POTION("POTION", "pot"),
        BLOCK_TAG("BLOCK_TAG"),
        BLOCK("BLOCK"),
        PROJECTILE("PROJECTILE"),
        VEHICLE("VEHICLE"),
        SPAWN_EGG("SPAWN_EGG"),
        ENTITY_TYPE("ENTITY_TYPE"),
        ITEM("ITEM"),
        LIST("LIST"),
        ANY_TYPE("ANY_TYPE"),
        UNKNOWN("UNKNOWN");


        private final String id;
        private final List<String> aliases;
        private final List<ValueType> compatibleValueTypes = new ArrayList<>();

        ValueType(String id, String... aliases) {
            this.id = id;
            this.aliases = Arrays.asList(aliases);
        }

        static {
            BLOCK_TAG.addCompatibleValueTypes(TEXT);
            BLOCK.addCompatibleValueTypes(ITEM, TEXT);
            PROJECTILE.addCompatibleValueTypes(ITEM);
            VEHICLE.addCompatibleValueTypes(ITEM);
            SPAWN_EGG.addCompatibleValueTypes(ITEM);
            ENTITY_TYPE.addCompatibleValueTypes(ITEM, PROJECTILE, VEHICLE, SPAWN_EGG);
            ITEM.addCompatibleValueTypes(BLOCK, PROJECTILE, VEHICLE, SPAWN_EGG, ENTITY_TYPE);
        }

        private void addCompatibleValueTypes(ValueType... compatibleValueTypes) {
            this.compatibleValueTypes.addAll(List.of(compatibleValueTypes));
        }

        public String getID() {
            return id;
        }

        public String getName() {
            StringBuilder sb = new StringBuilder();
            String[] words = id.split("_");
            for (String word : words) {
                sb.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1).toLowerCase(Locale.ROOT)).append(' ');
            }
            return sb.toString().trim();
        }

        public boolean isCompatibleWith(ValueType valueType) {
            if (this == NONE && valueType != NONE) {
                return false;
            }
            if (this != VARIABLE && valueType == GAME_VALUE) {
                return true;
            }

            return this == ANY_TYPE || this == valueType || valueType == VARIABLE || this.compatibleValueTypes.contains(valueType);
        }

        public static ValueType fromID(String id) {
            for (ValueType valueType : values()) {
                if (valueType.id.equals(id) || valueType.aliases.contains(id)) {
                    return valueType;
                }
            }

            return UNKNOWN;
        }

        public static ValueType fromItemStack(ItemStack item) {
            CompoundTag pbv = item.getTagElement("PublicBukkitValues");
            if (pbv != null) {
                String t = pbv.getString("hypercube:varitem");
                if (t != null) {
                    try {
                        JsonObject o = JsonParser.parseString(t).getAsJsonObject();
                        return fromID(o.get("id").getAsString());
                    } catch (Exception ignored) {}
                }
            }

            Item itemType = item.getItem();
            if (itemType == Items.AIR) {
                return NONE;
            }
            return ITEM;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}

