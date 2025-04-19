package onyx.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class OcarinaOfTime extends Item {
    public OcarinaOfTime(Settings settings) { super(settings); }

    @Override public boolean hasGlint(ItemStack stack) { return true; }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) { return ActionResult.PASS; }

        return ActionResult.SUCCESS;
    }
}
