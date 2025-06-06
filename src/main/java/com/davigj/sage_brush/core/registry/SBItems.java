package com.davigj.sage_brush.core.registry;

import com.davigj.sage_brush.common.item.BrushItem;
import com.davigj.sage_brush.common.item.DyeBrushItem;
import com.davigj.sage_brush.core.SageBrush;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SageBrush.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SBItems {
    public static final ItemSubRegistryHelper HELPER = SageBrush.REGISTRY_HELPER.getItemSubHelper();

    public static RegistryObject<Item> BRUSH = HELPER.createItem("brush", () -> new BrushItem(new Item.Properties().durability(64).tab(CreativeModeTab.TAB_TOOLS)));
//    public static RegistryObject<Item> RED_BRUSH = HELPER.createItem("red_brush", () -> new DyeBrushItem(new Item.Properties().durability(64).tab(CreativeModeTab.TAB_TOOLS), DyeColor.RED));
}
