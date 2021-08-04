package io.github.codeutilities.mod.config.types;

import com.google.gson.annotations.SerializedName;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.internal.ConfigFile;
import io.github.codeutilities.mod.config.structure.ConfigSetting;

public class EnumSetting<E extends Enum<E> & IConfigEnum> extends ConfigSetting<E> {
    Class<E> eClass;

    public EnumSetting(String key, Class<E> eClass, E defaultValue) {
        super(key, defaultValue);

        this.eClass = eClass;
    }

    public Class<E> getEnumClass() {
        return eClass;
    }

    public void setFromString(String string) {
        this.setValue(Enum.valueOf(eClass,string));
    }

    public enum TestEnum {
        @SerializedName("ONE")
        ONE,
        @SerializedName("TWO")
        TWO,
        @SerializedName("THREE")
        THREE
    }

    public enum TestEnum2 {
        A,
        B,
        C
    }
}
