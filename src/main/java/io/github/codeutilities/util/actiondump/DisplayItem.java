package io.github.codeutilities.util.actiondump;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.util.chat.TextUtil;
import io.github.codeutilities.util.misc.ItemUtil;
import io.github.codeutilities.util.misc.StringUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DisplayItem {
    private final String material;
    private final String name;
    private final String[] deprecatedNote;
    private final String[] description;
    private final String[] example;
    private final String[] worksWith;
    private final HashSet<String[]> additionalInfo;
    private final String requiredRank;
    private final boolean requireTokens;
    private final boolean requireRankAndTokens;
    private final boolean advanced;
    private final String loadedItem;
    private final int tags;
    private final Argument[] arguments;

    DisplayItem(JsonObject jsonObject){
        this.material = jsonObject.get("material").getAsString();
        this.name = jsonObject.get("name").getAsString();
        this.deprecatedNote = StringUtil.toStringArray(jsonObject.get("deprecatedNote").getAsJsonArray());
        this.description = StringUtil.toStringArray(jsonObject.get("description").getAsJsonArray());
        this.example = StringUtil.toStringArray(jsonObject.get("example").getAsJsonArray());
        this.worksWith = StringUtil.toStringArray(jsonObject.get("worksWith").getAsJsonArray());
        this.additionalInfo = StringUtil.toStringListHashSet(jsonObject.get("additionalInfo").getAsJsonArray());
        this.requiredRank = jsonObject.get("requiredRank").getAsString();
        this.requireTokens = jsonObject.get("requireTokens").getAsBoolean();
        this.requireRankAndTokens = jsonObject.get("requireRankAndTokens").getAsBoolean();
        this.advanced = jsonObject.get("advanced").getAsBoolean();
        this.loadedItem = jsonObject.get("loadedItem").getAsString();
        this.tags = jsonObject.has("tags") ? jsonObject.get("tags").getAsInt() : 0;
        if(jsonObject.has("arguments")) {
            JsonArray args = jsonObject.get("arguments").getAsJsonArray();
            this.arguments = new Argument[args.size()];
            int i = 0;
            for(JsonElement tag : args){
                this.arguments[i] = new Argument(tag.getAsJsonObject());
                i++;
            }
        }else this.arguments = null;
    }

    public String getLoadedItem() {
        return loadedItem;
    }

    public String getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String getRequiredRank() {
        return requiredRank;
    }

    public HashSet<String[]> getAdditionalInfo() {
        return additionalInfo;
    }

    public String[] getDeprecatedNote() {
        return deprecatedNote;
    }

    public String[] getDescription() {
        return description;
    }

    public String[] getExample() {
        return example;
    }

    public String[] getWorksWith() {
        return worksWith;
    }

    public boolean isRequireTokens() {
        return requireTokens;
    }

    public boolean isRequireRankAndTokens() {
        return requireRankAndTokens;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public int getTags() {
        return tags;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public ItemStack toItemStack(){
        try {
            ItemStack itemStack = ItemUtil.fromID(this.material);
            itemStack.setCustomName(TextUtil.colorCodesToTextComponent(this.name.replaceAll("&", "§")));

            ArrayList<String> lore = new ArrayList<>();

            if (this.deprecatedNote.length > 0) {
                itemStack.addEnchantment(Enchantment.byRawId(0), 1);
                lore.add(TextUtil.toTextString("§4§l! §r§x§f§f§a§a§a§aMarked for Removal"));
                for (String depnote : this.deprecatedNote) {
                    lore.add(TextUtil.toTextString("§7" + depnote));
                }
                lore.add(TextUtil.toTextString(""));
            }

            if (this.deprecatedNote.length == 0 && this.advanced) {
                lore.add(TextUtil.toTextString("§x§F§F§7§F§5§5Advanced"));
            }

            for (String desc : this.description) {
                lore.add(TextUtil.toTextString((this.deprecatedNote.length > 0 ? "§x§8§0§8§0§8§0§m" : "§7") + desc.replaceAll("&", "§")));
            }

            if (this.deprecatedNote.length == 0) {

                Boolean optional = false;

                if (this.arguments != null || this.tags > 0) {
                    lore.add(TextUtil.toTextString(""));
                    lore.add(TextUtil.toTextString("§fChest Parameters:"));
                    if (this.arguments != null) {
                        for (Argument arg : this.arguments) {
                            String[] desc = arg.getDescription();
                            String firstdesc = arg.getText() == null ? (desc.length > 0 ? desc[0].replaceAll("&", "§") : "") : "";
                            firstdesc = arg.getText() == null ? (desc.length > 0 ? (arg.getType().equals("NONE") ? (firstdesc.endsWith(")") ? " §7(" + firstdesc : " §7(" + firstdesc + "§7)") : (" §8- §7" + firstdesc)) : "") : "";
                            String arglore = arg.getText() == null ? (ActionDump.valueOf(arg.getType()).getColor() + ActionDump.valueOf(arg.getType()).getName() + (arg.isPlural() ? "(s)" : "") + (arg.isOptional() ? "§f*" : "") + firstdesc) : (arg.getText().equals("") ? "" : arg.getText().replaceAll("&", "§"));

                            if (arg.isOptional()) optional = true;

                            lore.add(TextUtil.toTextString(arglore));

                            for (int i = 1; i < (desc == null ? 0 : desc.length); i++) {
                                lore.add(TextUtil.toTextString("§7" + desc[i]));
                            }

                            HashSet<String[]> allnotes = arg.getNotes();
                            if (allnotes != null) {
                                for (String[] notes : allnotes) {
                                    int i = 0;
                                    for (String note : notes) {
                                        lore.add(TextUtil.toTextString((i == 0 ? "§9⏵ " : "") + "§7" + note));
                                        i++;
                                    }
                                }
                            }
                        }
                        if (this.arguments.length == 0 && this.tags == 0) {
                            lore.add(TextUtil.toTextString(ActionDump.valueOf("NONE").getColor() + "None"));
                        }
                    }
                    if (this.tags > 0) {
                        lore.add(TextUtil.toTextString("§3# §7" + tags + " Tag" + (tags > 1 ? "s" : "")));
                    }
                }

                if (optional) {
                    lore.add(TextUtil.toTextString(""));
                    lore.add(TextUtil.toTextString("§7*Optional"));
                }

                if (this.worksWith.length > 0) {
                    lore.add(TextUtil.toTextString(""));
                    lore.add(TextUtil.toTextString("§x§A§A§7§F§F§FWorks With:"));
                    for (String with : this.worksWith) {
                        lore.add(TextUtil.toTextString("§b» §7" + with));
                    }
                }

                if (this.additionalInfo.size() > 0) {
                    lore.add(TextUtil.toTextString(""));
                    lore.add(TextUtil.toTextString("§9Additional Info:"));
                    for (String[] addInfo : this.additionalInfo) {
                        int i = 0;
                        for (String info : addInfo) {
                            lore.add(TextUtil.toTextString((i == 0 ? "§b» " : "") + "§7" + info));
                            i++;
                        }
                    }
                }

                if (this.example.length > 0) {
                    lore.add(TextUtil.toTextString(""));
                    lore.add(TextUtil.toTextString("§fExamples:"));
                    int i = 0;
                    for (String ex : this.example) {
                        lore.add(TextUtil.toTextString("§7" + ex));
                        i++;
                    }
                }

                if (!this.requiredRank.equals("") || this.requireTokens) {
                    lore.add(TextUtil.toTextString(""));
                }

                if (this.requireTokens) {
                    lore.add(TextUtil.toTextString("§x§f§f§d§4§2§aUnlock with Tokens"));
                }

                if (!this.requiredRank.equals("") && this.requireTokens) {
                    lore.add(TextUtil.toTextString("§x§f§f§5§5§a§aOR"));
                }

                if (!this.requiredRank.equals("")) {
                    Types rank = ActionDump.valueOf(this.requiredRank.toUpperCase());
                    lore.add(TextUtil.toTextString(rank.getColor() + "Unlocked with " + rank.getName()));
                }

            }

            String[] fullLore = new String[lore.size()];
            int i = 0;
            for (String loretext : lore) {
                fullLore[i] = loretext;
                i++;
            }
            ItemUtil.setLore(itemStack, fullLore);

            itemStack.getTag().putInt("HideFlags", 127);

            return itemStack;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "DisplayItem{" +
                "material='" + material + '\'' +
                ", name='" + name + '\'' +
                ", deprecatedNote=" + Arrays.toString(deprecatedNote) +
                ", description=" + Arrays.toString(description) +
                ", example=" + Arrays.toString(example) +
                ", worksWith=" + Arrays.toString(worksWith) +
                ", additionalInfo=" + additionalInfo +
                ", requiredRank='" + requiredRank + '\'' +
                ", requireTokens=" + requireTokens +
                ", requireRankAndTokens=" + requireRankAndTokens +
                ", advanced=" + advanced +
                ", loadedItem='" + loadedItem + '\'' +
                ", tags=" + tags +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}