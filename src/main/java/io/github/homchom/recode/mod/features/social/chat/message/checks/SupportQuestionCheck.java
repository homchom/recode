package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

import java.util.regex.Pattern;

public class SupportQuestionCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final Pattern SUPPORT_QUESTION_REGEX = Pattern.compile("^.*?» Support Question: \\(Click to answer\\)\\nAsked by \\w+ \\[[a-zA-Z]+]\\n.+$");

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.SUPPORT_QUESTION;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return SUPPORT_QUESTION_REGEX.matcher(stripped).matches();
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideSupport();
    }
}
