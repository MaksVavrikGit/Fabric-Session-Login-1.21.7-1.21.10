package dev.maksvavrik.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class FormattingUtils {
    public static Component surroundWithObfuscated(Component baseText, int count) {
        Style baseStyle = baseText.getStyle().withObfuscated(false);

        Style obfStyle = baseStyle.withObfuscated(true);

        String padding = "@".repeat(count);

        Component obfuscatedLeft  = Component.literal(padding + " ").setStyle(obfStyle);
        Component obfuscatedRight = Component.literal(" " + padding).setStyle(obfStyle);
        Component middle          = baseText.copy().setStyle(baseStyle);

        return Component.empty()
                .append(obfuscatedLeft)
                .append(middle)
                .append(obfuscatedRight);
    }
}
