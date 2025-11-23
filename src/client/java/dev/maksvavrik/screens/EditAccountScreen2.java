package dev.maksvavrik.screens;

import dev.maksvavrik.SessionIDLoginMod;
import dev.maksvavrik.utils.APIUtils;
import dev.maksvavrik.utils.SessionUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import static dev.maksvavrik.utils.FormattingUtils.surroundWithObfuscated;

public class EditAccountScreen2 extends Screen {


    private EditBox nameField;
    private EditBox skinUrlField;
    private Button nameButton;
    private Button skinButton;
    private Component currentTitle;

    private Component Username;
    private Component Skin;

    public EditAccountScreen2() {
        // Заголовок экрана (верхняя строка ванильного GUI)
        super(Component.literal("SessionID Login"));
        // Стартовый текст по центру экрана
        this.currentTitle = surroundWithObfuscated(Component.literal("Edit Account").withStyle(ChatFormatting.AQUA), 5);
        this.Username = Component.literal("Username:").withStyle(ChatFormatting.GRAY);
        this.Skin = Component.literal("Skin URL:").withStyle(ChatFormatting.GRAY);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        nameField = new EditBox(
                this.font,
                centerX - 100,
                centerY - 40,
                200,
                20,
                Component.literal("New Username")
        );
        nameField.setMaxLength(16);
        nameField.setFocused(true);
        this.addRenderableWidget(nameField);

        skinUrlField = new EditBox(
                this.font,
                centerX - 100,
                centerY,
                200,
                20,
                Component.literal("Skin URL")
        );
        skinUrlField.setMaxLength(2048);
        this.addRenderableWidget(skinUrlField);

        nameButton = Button.builder(Component.literal("Change Name"), button -> {
            String newName = nameField.getValue().trim();
            if (!newName.isEmpty()) {
                if(newName.matches("^[a-zA-Z0-9_]{3,16}$")) {
                    int statusCode = APIUtils.changeName(newName, SessionIDLoginMod.currentSession.getAccessToken());
                    currentTitle = switch (statusCode) {
                        case 200 -> {
                            SessionIDLoginMod.currentSession = SessionUtils.createSession(newName, SessionIDLoginMod.currentSession.getProfileId(), SessionIDLoginMod.currentSession.getAccessToken());
                            yield surroundWithObfuscated(Component.literal("Successfully changed name").withStyle(ChatFormatting.GREEN), 4);
                        }
                        case 429 -> surroundWithObfuscated(Component.literal("Too many requests").withStyle(ChatFormatting.RED), 5);
                        case 400 -> surroundWithObfuscated(Component.literal("Invalid name").withStyle(ChatFormatting.RED), 7);
                        case 401 -> surroundWithObfuscated(Component.literal("Invalid token").withStyle(ChatFormatting.RED), 7);
                        case 403 -> surroundWithObfuscated(Component.literal("Name is unavailable or Player already changed name in the last 35 days").withStyle(ChatFormatting.RED), 2);
                        default -> surroundWithObfuscated(Component.literal("Unknown error").withStyle(ChatFormatting.RED), 2);
                    };

                }else{
                    currentTitle = surroundWithObfuscated(Component.literal("Invalid name").withStyle(ChatFormatting.RED), 7);
                }
            }else{
                currentTitle = surroundWithObfuscated(Component.literal("Please input a name").withStyle(ChatFormatting.RED), 5);
            }
        }).bounds(centerX - 100, centerY + 25, 97, 20).build();
        this.addRenderableWidget(nameButton);

        skinButton = Button.builder(Component.literal("Change Skin"), button -> {
            String skinUrl = skinUrlField.getValue().trim();
            if (!skinUrl.isEmpty()) {
                int statusCode = APIUtils.changeSkin(skinUrl, SessionIDLoginMod.currentSession.getAccessToken());
                currentTitle = switch (statusCode){
                    case 200 -> surroundWithObfuscated(Component.literal("Successfully changed skin").withStyle(ChatFormatting.GREEN), 4);
                    case 429 -> surroundWithObfuscated(Component.literal("Too many requests").withStyle(ChatFormatting.RED), 5);
                    case 401 -> surroundWithObfuscated(Component.literal("Invalid token").withStyle(ChatFormatting.RED), 7);
                    case -1 -> surroundWithObfuscated(Component.literal("Unknown error").withStyle(ChatFormatting.RED), 7);
                    default  -> surroundWithObfuscated(Component.literal("Invalid Skin").withStyle(ChatFormatting.RED), 7);
                };
            }else{
                currentTitle = surroundWithObfuscated(Component.literal("Please input an URL").withStyle(ChatFormatting.RED), 5);
            }
        }).bounds(centerX + 3, centerY + 25, 97, 20).build();
        this.addRenderableWidget(skinButton);

        Button backButton = Button.builder(Component.literal("Back"), button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen(new TitleScreen()));
        }).bounds(centerX - 100, centerY + 50, 200, 20).build();
        this.addRenderableWidget(backButton);

        if (SessionIDLoginMod.originalSession.equals(SessionIDLoginMod.currentSession)){
            nameButton.active = false;
            skinButton.active = false;

            currentTitle = surroundWithObfuscated(Component.literal("Cannot modify original session").withStyle(ChatFormatting.YELLOW), 4);

        }

    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        // Фон
//        this.renderBackground(gui, mouseX, mouseY, delta);

        gui.drawCenteredString(this.font, currentTitle, this.width / 2, this.height / 2 - 75, 0xFFFFFFFF);

        gui.drawString(this.font, Username, this.width / 2 - 100, this.height / 2 - 52, 0xFFFFFFFF);

        gui.drawString(this.font, Skin, this.width / 2 - 100, this.height / 2 - 10, 0xFFFFFFFF);


        super.render(gui, mouseX, mouseY, delta);

        // Рисуем поле ввода вручную (ОБЯЗАТЕЛЬНО!)
        // Заголовок


        // Debug
    }


    @Override
    public boolean keyPressed(KeyEvent event) {
        return nameField.keyPressed(event)
                || skinUrlField.keyPressed(event)
                || super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return nameField.charTyped(event)
                || skinUrlField.charTyped(event)
                || super.charTyped(event);
    }

}
