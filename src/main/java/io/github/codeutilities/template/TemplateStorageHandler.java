package io.github.codeutilities.template;

import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.externalfile.ExternalFile;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateStorageHandler {

    private static final File FILE = ExternalFile.TEMPLATE_DB.getFile();
    private static final int MAX_SIZE = 55;
    private static List<TemplateItem> templates = new ArrayList<>(MAX_SIZE);

    // Must contain serialized template list.
    // For reference, see HotbarStorage#load
    public static void load() {
        try {
            CompoundTag compoundTag = NbtIo.read(FILE);
            if (compoundTag == null) {
                return;
            }

            if (!compoundTag.contains("DataVersion", 99)) {
                compoundTag.putInt("DataVersion", 1343);
            }
            compoundTag = NbtHelper.update(MinecraftClient.getInstance().getDataFixer(), DataFixTypes.HOTBAR, compoundTag, compoundTag.getInt("DataVersion"));
            for (ItemStack stack : ItemUtil.fromListTag(compoundTag.getList("items", 10))) {
                templates.add(new TemplateItem(stack));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addTemplate(ItemStack stack) {
        TemplateItem template = new TemplateItem(stack);
        if (templates.contains(template)) {
            return;
        }
        if (templates.size() > MAX_SIZE) {
            templates.remove(MAX_SIZE);
        }

        stack.setCount(1);
        templates.add(0, template);
    }

    public static List<TemplateItem> getTemplates() {
        return templates;
    }

    public static void save() {
        try {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            compoundTag.put("items", ItemUtil.toListTag(templates.stream().map((templateItem -> templateItem.stack)).collect(Collectors.toList())));

            NbtIo.write(compoundTag, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
