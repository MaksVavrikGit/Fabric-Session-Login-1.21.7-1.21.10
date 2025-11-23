package dev.maksvavrik.screens;

import dev.maksvavrik.SessionIDLoginMod;
import dev.maksvavrik.utils.APIUtils;
import dev.maksvavrik.utils.SessionUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static dev.maksvavrik.utils.FormattingUtils.surroundWithObfuscated;

public class LoginScreen extends Screen {

    private EditBox sessionField;
    private Button loginButton;
    private Button restoreButton;

    private Component currentTitle;

    public LoginScreen() {
        // Заголовок экрана (верхняя строка ванильного GUI)
        super(Component.literal("SessionID Login"));
        // Стартовый текст по центру экрана
        this.currentTitle = surroundWithObfuscated(
                Component.literal("Input Session ID").withStyle(ChatFormatting.GOLD),
                5
        );
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Поле ввода Session ID
        this.sessionField = new EditBox(
                this.font,
                centerX - 100,
                centerY,
                200,
                20,
                Component.literal("Session ID")
        );
        this.sessionField.setMaxLength(32767);
        this.sessionField.setValue("");
        this.sessionField.setFocused(true);

        // addWidget — только в children, сам не рисуется, поэтому
        // ниже мы его вручную рендерим в render().
        this.addWidget(this.sessionField);

        // Кнопка Login
        this.loginButton = Button.builder(Component.literal("Login"), button -> {
            String sessionInput = this.sessionField.getValue().trim();

            if (!sessionInput.isEmpty()) {
                try {
                    String[] sessionInfo = APIUtils.getProfileInfo(sessionInput);

                    // Здесь ты подменяешь сессию
                    SessionUtils.setSession(SessionUtils.createSession(
                            sessionInfo[0],
                            sessionInfo[1],
                            sessionInput
                    ));

                    this.currentTitle = surroundWithObfuscated(
                            Component.literal("Logged in as: " + sessionInfo[0]).withStyle(ChatFormatting.GREEN),
                            5
                    );

                    this.loginButton.active = true;
                    this.restoreButton.active = true;
                } catch (IOException | RuntimeException e) {
                    this.currentTitle = surroundWithObfuscated(
                            Component.literal("Invalid Session ID").withStyle(ChatFormatting.RED),
                            7
                    );
                }
            } else {
                this.currentTitle = surroundWithObfuscated(
                        Component.literal("Session ID cannot be empty").withStyle(ChatFormatting.RED),
                        5
                );
            }
        }).bounds(centerX - 100, centerY + 25, 97, 20).build();
        this.addRenderableWidget(this.loginButton);

        // Кнопка Restore
        this.restoreButton = Button.builder(Component.literal("Restore"), button -> {
            SessionUtils.restoreSession();

            this.currentTitle = surroundWithObfuscated(
                    Component.literal("Restored original session").withStyle(ChatFormatting.GREEN),
                    7
            );

            this.loginButton.active = true;
            this.restoreButton.active = false;
        }).bounds(centerX + 3, centerY + 25, 97, 20).build();
        this.addRenderableWidget(this.restoreButton);

        // Кнопка Back
        Button backButton = Button.builder(Component.literal("Back"), button -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
        }).bounds(centerX - 100, centerY + 50, 200, 20).build();
        this.addRenderableWidget(backButton);

        // Если текущая сессия совпадает с оригинальной — нечего ресторить
        if (SessionIDLoginMod.currentSession != null &&
                SessionIDLoginMod.originalSession != null &&
                SessionIDLoginMod.currentSession.equals(SessionIDLoginMod.originalSession)) {
            this.restoreButton.active = false;
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        // Фон
//        this.renderBackground(gui, mouseX, mouseY, delta);

        gui.fill(0, 0, this.width, this.height, 0xAA000000);


        gui.drawCenteredString(this.font, currentTitle, this.width / 2, this.height  / 2 - 30, 0xFFFFFFFF);

        gui.drawCenteredString(
                this.font,
                currentTitle,
                this.width / 2,
                this.height / 2 - 30,
                0xFFFFFF
        );

        super.render(gui, mouseX, mouseY, delta);

        // Рисуем поле ввода вручную (ОБЯЗАТЕЛЬНО!)
        this.sessionField.render(gui, mouseX, mouseY, delta);

        // Заголовок


        // Debug
    }


    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.sessionField.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.sessionField.charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    public static void open(@Nullable Screen parent) {
        Minecraft.getInstance().setScreen(new LoginScreen());
    }
}
