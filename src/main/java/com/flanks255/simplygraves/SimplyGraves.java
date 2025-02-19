package com.flanks255.simplygraves;

import com.flanks255.simplygraves.WSD.GraveStorage;
import com.flanks255.simplygraves.commands.SGCommands;
import com.flanks255.simplygraves.config.CommonConfig;
import com.flanks255.simplygraves.data.Generator;
import com.flanks255.simplygraves.render.EntityRenders;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;


@Mod(SimplyGraves.MODID)
public class SimplyGraves
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "simplygraves";

    public static final TagKey<Item> NO_GRAVE = TagKey.create(Registries.ITEM, new ResourceLocation("simplygraves", "no_grave"));

    public static final TagKey<Block> FTBCHUNKS = TagKey.create(Registries.BLOCK, new ResourceLocation("ftbchunks", "interact_whitelist"));
    public static final TagKey<Block> GAIA_BLOCK = TagKey.create(Registries.BLOCK, new ResourceLocation("botania", "gaia_break_blacklist"));
    public static final TagKey<Block> GRAVES = TagKey.create(Registries.BLOCK, new ResourceLocation("simplygraves", "graves"));
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimplyGraves.MODID);

    public SimplyGraves(IEventBus bus)
    {
        SGBlocks.init(bus);
        ITEMS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);

        bus.addListener(this::setup);
        bus.addListener(Generator::gatherData);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            bus.addListener(EntityRenders::registerEntityRenderers);
        }

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DropEvent::Event);
        NeoForge.EVENT_BUS.addListener(this::commandsRegister);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void commandsRegister(RegisterCommandsEvent event) {
        SGCommands.register(event.getDispatcher());
    }

    public static Set<String> getUUIDSuggestions() {
        GraveStorage graveStorage = GraveStorage.get();
        Set<String> list = new HashSet<>();

        graveStorage.getData().forEach((uuid, graveData) -> list.add(uuid.toString()));

        if (list.isEmpty())
            list.add("[No graves]");

        return list;
    }
}
