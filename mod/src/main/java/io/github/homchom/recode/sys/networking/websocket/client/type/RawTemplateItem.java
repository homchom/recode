package io.github.homchom.recode.sys.networking.websocket.client.type;

import io.github.homchom.recode.sys.hypercube.templates.CompressionUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RawTemplateItem extends AbstractTemplateItem {

    @Override
    public String getIdentifier() {
        return "raw_template";
    }

    @Override
    public String parseJsonData(String templateData) throws IOException {
        byte[] bytes = CompressionUtil.toBase64(CompressionUtil.toGZIP(templateData.getBytes(StandardCharsets.UTF_8)));

        return new String(bytes);
    }
}
