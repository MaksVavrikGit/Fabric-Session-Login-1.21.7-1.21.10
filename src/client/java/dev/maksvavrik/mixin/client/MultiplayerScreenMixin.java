package dev.maksvavrik.mixin.client;


import dev.maksvavrik.screens.EditAccountScreen2;

import dev.maksvavrik.screens.LoginScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.maksvavrik.SessionIDLoginMod.hasValidationStarted;
import static dev.maksvavrik.SessionIDLoginMod.isSessionValid;


@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

	protected MultiplayerScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {

		isSessionValid = null;
		hasValidationStarted = false;

		int loginButtonX = this.width - 90;
		int editAccountButtonX = this.width - 180;
		int buttonY = 5;
		int buttonWidth = 80;
		int buttonHeight = 20;

		this.addRenderableWidget(Button.builder(Component.literal("Login"), button -> {
			Minecraft.getInstance().setScreen(new LoginScreen());
		}).bounds(loginButtonX, buttonY, buttonWidth, buttonHeight).build());

		this.addRenderableWidget(Button.builder(Component.literal("Edit Account"), button -> {
			Minecraft.getInstance().setScreen(new EditAccountScreen2());
		}).bounds(editAccountButtonX, buttonY, buttonWidth, buttonHeight).build());
	}
//	@Inject(method = "method_25394", at = @At("TAIL"))
//	private void onRender(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//		String username = SessionUtils.getUsername();
//
//
//		if (isSessionValid == null && !hasValidationStarted) {
//			hasValidationStarted = true;
//			new Thread(() -> {
//				isSessionValid = APIUtils.validateSession(Minecraft.getInstance().getUser().getAccessToken());
//			}, "SessionValidationThread").start();
//		}
//
//
//		Component statusText;
//		if (isSessionValid == null) {
//			statusText = Component.literal("[... Validating]").withStyle(ChatFormatting.GRAY);
//		} else if (isSessionValid) {
//			statusText = Component.literal("[✔] Valid").withStyle(ChatFormatting.GREEN);
//		} else {
//			statusText = Component.literal("[✘] Invalid").withStyle(ChatFormatting.RED);
//		}
//
//		Component display = Component.literal("User: ")
//				.append(Component.literal(username).withStyle(ChatFormatting.WHITE))
//				.append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
//				.append(statusText);
//
//		context.drawString(this.font, display, 5, 10, 0xFFFFFF, false);
//	}


}