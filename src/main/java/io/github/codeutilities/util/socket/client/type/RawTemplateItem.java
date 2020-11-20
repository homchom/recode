package io.github.codeutilities.util.socket.client.type;

import io.github.codeutilities.util.CompressionUtil;

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
