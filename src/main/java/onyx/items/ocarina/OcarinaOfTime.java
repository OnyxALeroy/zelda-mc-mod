package onyx.items.ocarina;

import net.minecraft.item.ItemStack;

public class OcarinaOfTime extends OcarinaTemplate {
    public OcarinaOfTime(Settings settings) { super(settings); }

    @Override public boolean hasGlint(ItemStack stack) { return true; }

    public String getOcarinaName(){ return "ocarina_of_time"; }
}
