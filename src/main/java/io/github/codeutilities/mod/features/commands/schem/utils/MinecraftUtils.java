package io.github.codeutilities.mod.features.commands.schem.utils;

public class MinecraftUtils {
    public static String GenerateGiveCommandForTemplate(String templateName, String templateData) {
        return "give @p minecraft:ender_chest{display:{Name:'[{\"text\":\"� \",\"color\":\"gold\"},{\"text\":\"DFMatic Schematic ("
                + templateName + ")\",\"color\":\"yellow\",\"bold\":true}]'},PublicBukkitValues:{\"hypercube:codetemplatedata\":'{\"author\":\"DFMatic Program\",\"name\":\"&6� &e&lDFMatic Schematic("
                + templateName + ")\",\"version\":1,\"code\":\"" + templateData + "\"}'}} 1";
    }
}
