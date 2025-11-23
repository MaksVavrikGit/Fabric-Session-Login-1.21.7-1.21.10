package dev.maksvavrik.mixin.client;

import dev.maksvavrik.utils.APIUtils;
import dev.maksvavrik.utils.SessionUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.maksvavrik.SessionIDLoginMod.hasValidationStarted;
import static dev.maksvavrik.SessionIDLoginMod.isSessionValid;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected Font font;

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics g, int mouseX, int mouseY, float delta, CallbackInfo ci) {

        // Делаем код ТОЛЬКО если это JoinMultiplayerScreen
        if (!(((Object)this) instanceof JoinMultiplayerScreen)) {
            return;
        }

        String username = SessionUtils.getUsername();


        if (isSessionValid == null && !hasValidationStarted) {
            hasValidationStarted = true;
            new Thread(() -> {
                isSessionValid = APIUtils.validateSession(Minecraft.getInstance().getUser().getAccessToken());
            }, "SessionValidationThread").start();
        }


        Component statusText;
        if (isSessionValid == null) {
            statusText = Component.literal("[... Validating]").withStyle(ChatFormatting.GRAY);
        } else if (isSessionValid) {
            statusText = Component.literal("[✔] Valid").withStyle(ChatFormatting.GREEN);
        } else {
            statusText = Component.literal("[✘] Invalid").withStyle(ChatFormatting.RED);
        }

        Component display = Component.literal("User: ")
                .append(Component.literal(username).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                .append(statusText);

        g.drawString(this.font, display, 5, 10, 0xFFFFFFFF, false);
    }

}
