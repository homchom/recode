package io.github.homchom.recode.sys.hypercube.templates;

import io.github.homchom.recode.Globals;
import io.github.homchom.recode.mod.commands.IManager;
import io.github.homchom.recode.sys.file.ExternalFile;
import io.github.homchom.recode.sys.file.ISave;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateStorageHandler implements IManager<TemplateItem>, ISave {

    private static final Path PATH = ExternalFile.TEMPLATE_DB.getPath();
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
    // For reference, see HotbarManager#load
    @Override
    public void initialize() {
        try {
            CompoundTag compoundTag = NbtIo.read(PATH);
            if (compoundTag == null) {
                return;
            }

            if (!compoundTag.contains("DataVersion", 99)) {
                compoundTag.putInt("DataVersion", 1343);
            }
            compoundTag = DataFixTypes.HOTBAR.update(
                    Minecraft.getInstance().getFixerUpper(),
                    compoundTag, compoundTag.getInt("DataVersion"),
                    Globals.getCurrentDataVersion()
            );

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
            compoundTag.putInt("DataVersion", Globals.getCurrentDataVersion());
            compoundTag.put("items", ItemUtil.toListTag(registeredTemplates.stream()
                    .map((templateItem -> templateItem.stack)).collect(Collectors.toList())));

            NbtIo.write(compoundTag, PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
