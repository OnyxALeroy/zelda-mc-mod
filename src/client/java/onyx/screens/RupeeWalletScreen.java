package onyx.screens;

import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import onyx.components.ZeldaComponents;
import onyx.items.ZeldaItems;
import onyx.items.rupees.Rupee;
import onyx.server.GiveRupeeC2SPayload;

@Environment(EnvType.CLIENT)
public class RupeeWalletScreen extends Screen {
    public Screen parent;
    private ItemStack walletStack;
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("zelda-oot-mod", "textures/gui/rupee_wallet.png");
    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 222;

    private final ItemStack[] rupees = new ItemStack[] {
        new ItemStack(ZeldaItems.GREEN_RUPEE, 1),
        new ItemStack(ZeldaItems.BLUE_RUPEE, 1),
        new ItemStack(ZeldaItems.RED_RUPEE, 1),
        new ItemStack(ZeldaItems.PURPLE_RUPEE, 1),
        new ItemStack(ZeldaItems.GOLD_RUPEE, 1)
    };
    private HashMap<Integer, Integer> addedRupees = new HashMap<>();
    private final List<SlotInfo> rupeeSlots = new ArrayList<>();
    private static class SlotInfo {
        int x, y;
        ItemStack stack;

        SlotInfo(int x, int y, ItemStack stack) {
            this.x = x;
            this.y = y;
            this.stack = stack;
        }

        boolean contains(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
        }
    }

    public RupeeWalletScreen(Screen parent, ItemStack walletStack){
        super(Text.empty());
        this.parent = parent;
        this.walletStack = walletStack;
    }

	@Override
	protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.zelda-oot-mod.back"), (btn) -> {
            this.close();
        }).dimensions(centerX - 60, centerY - 50, 120, 20).build());

        // Filling the rupees slots
        rupeeSlots.clear();
        int slotSize = 18;
        int startX = (this.width - (rupees.length * slotSize)) / 2 + 1;
        int y = this.height / 2 - 74;
        for (int i = 0; i < rupees.length; i++) {
            int x = startX + i * slotSize;
            rupeeSlots.add(new SlotInfo(x, y, rupees[i]));
        }

        // Initializing the "append" hashmap
        for (int i = 0; i < rupees.length; i++) {
            Rupee r = (Rupee) rupees[i].getItem();
            addedRupees.put(r.getValue(), 0);
        }
	}

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    // ---------------------------------------------------------------------------------------------------------------------

    // Rendering
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw the background
        this.renderBackgroundTexture(context);

        super.render(context, mouseX, mouseY, delta);

        // Draw rupee items
        for (SlotInfo slot : rupeeSlots) {
            context.drawItem(slot.stack, slot.x, slot.y);
            // Draw tooltip when hovering
            if (slot.contains(mouseX, mouseY)) {
                Rupee rupee = (Rupee) slot.stack.getItem();
                context.drawTooltip(
                    this.textRenderer,
                    Text.of("Value: " + rupee.getValue()),
                    mouseX, mouseY
                );
            }
        }

        // Displaying rupee informations
        renderAvailableAmount(context);
        renderSessionRupeeInfo(context);
    }

    private void renderBackgroundTexture(DrawContext context) {
        int centerX = (this.width - BG_WIDTH) / 2;
        int centerY = (this.height - BG_HEIGHT) / 2;
        
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, centerX, centerY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
    }
    private void renderAvailableAmount(DrawContext context){
        int centerX = this.width / 2;
        int currentRupees = walletStack.getOrDefault(ZeldaComponents.RUPEES_POSSESSED, 0);

        // Draw "Available Rupees: X" text
        Text availableText = Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.available_rupees", currentRupees).formatted(Formatting.GREEN);
        context.drawText(
            this.textRenderer,
            availableText,
            centerX - this.textRenderer.getWidth(availableText) / 2,
            this.height / 2 - 95,
            0xFFFFFF,
            true
        );
    }
    private void renderSessionRupeeInfo(DrawContext context){
        int startY = this.height / 2;
        int centerX = this.width / 2;
        
        // Title for the added rupees section
        Text titleText = Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.rupees_added").formatted(Formatting.GOLD);
        context.drawText(
            this.textRenderer,
            titleText,
            centerX - this.textRenderer.getWidth(titleText) / 2,
            startY - 15,
            0xFFFFFF,
            true
        );
        
        // Draw each rupee type and count
        int rowY = startY + 10;
        int leftX = centerX - 70;
        
        for (ItemStack rupeeStack : rupees) {
            Rupee rupee = (Rupee) rupeeStack.getItem();
            Integer count = addedRupees.get(rupee.getValue());
            if (count == null) count = 0;
            
            if (count > 0) {
                // Draw the rupee item
                context.drawItem(rupeeStack, leftX, rowY);
                
                // Draw the count
                String rupeeTypeName = rupeeStack.getName().getString();
                Text countText = Text.literal(rupeeTypeName + ": " + count);
                context.drawText(
                    this.textRenderer,
                    countText,
                    leftX + 20,
                    rowY + 4,
                    0xFFFFFF,
                    false
                );
                
                rowY += 16;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------

    // Clicking behavior
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.client != null || this.client.player != null){
            int currentRupees = walletStack.getOrDefault(ZeldaComponents.RUPEES_POSSESSED, 0);
            for (SlotInfo slot : rupeeSlots) {
                if (slot.contains((int) mouseX, (int) mouseY)) {
                    Rupee selectedRupee = (Rupee) slot.stack.copy().getItem();
                    if (selectedRupee.getValue() <= currentRupees){
                        // Asking the server to give a rupee
                        GiveRupeeC2SPayload payload = new GiveRupeeC2SPayload(slot.stack.copy());
                        ClientPlayNetworking.send(payload);

                        // Decrementing the wallet's capacity and adding the transaction to the history
                        walletStack.set(ZeldaComponents.RUPEES_POSSESSED, currentRupees - selectedRupee.getValue());
                        addedRupees.computeIfPresent(selectedRupee.getValue(), (k, v) -> v + 1);

                        return true;
                    } else {
                        this.client.getToastManager().add(
                            SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.no_rupee_left.title"), Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.no_rupee_left.text"))
                        );
                        return false;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
