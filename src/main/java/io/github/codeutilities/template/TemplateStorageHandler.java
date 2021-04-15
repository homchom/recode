package io.github.codeutilities.template;

import io.github.codeutilities.util.IManager;
import io.github.codeutilities.util.ISave;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.file.ExternalFile;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateStorageHandler implements IManager<TemplateItem>, ISave {

    private static final File FILE = ExternalFile.TEMPLATE_DB.getFile();
    private static final int MAX_SIZE = 55;
    private static TemplateStorageHandler instance;
    private final List<TemplateItem> registeredTemplates = new ArrayList<>(MAX_SIZE);

    public TemplateStorageHandler() {
        instance = this;
    }

    public static void addTemplate(ItemStack stack) {
        TemplateStorageHandler instance = getInstance();
        List<TemplateItem> registered = instance.getRegistered();

        try {
            TemplateItem template = new TemplateItem(stack);

            if (registered.contains(template)) {
                return;
            }
            if (registered.size() > MAX_SIZE) {
                registered.remove(MAX_SIZE);
            }

            stack.setCount(1);
            registered.add(0, template);
        } catch (Exception ignored) {
        }
    }

    public static TemplateStorageHandler getInstance() {
        return instance;
    }

    // Must contain serialized template list.
    // For reference, see HotbarStorage#load
    @Override
    public void initialize() {
        try {
            CompoundTag compoundTag = NbtIo.read(FILE);
            if (compoundTag == null) {
                return;
            }

            if (!compoundTag.contains("DataVersion", 99)) {
                compoundTag.putInt("DataVersion", 1343);
            }
            compoundTag = NbtHelper.update(MinecraftClient.getInstance().getDataFixer(),
                    DataFixTypes.HOTBAR, compoundTag, compoundTag.getInt("DataVersion"));

            for (ItemStack stack : ItemUtil.fromListTag(compoundTag.getList("items", 10))) {
                TemplateItem templateItem = new TemplateItem(stack);
                this.register(templateItem);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void register(TemplateItem object) {
        this.registeredTemplates.add(object);
    }

    @Override
    public List<TemplateItem> getRegistered() {
        return this.registeredTemplates;
    }

    @Override
    public void save() {
        try {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            compoundTag.put("items", ItemUtil.toListTag(registeredTemplates.stream()
                    .map((templateItem -> templateItem.stack)).collect(Collectors.toList())));

            NbtIo.write(compoundTag, FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
