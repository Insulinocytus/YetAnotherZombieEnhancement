package me.forever17.yetanotherzombieenhancement;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// 这里的值应与 META-INF/neoforge.mods.toml 文件中的条目匹配
@Mod(YetAnotherZombieEnhancement.MODID)
public class YetAnotherZombieEnhancement {
    // 在一个公共的地方定义 mod id，以便所有地方引用
    public static final String MODID = "yetanotherzombieenhancement";
    // 直接引用 slf4j 日志记录器
    public static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟注册器来持有将在“yetanotherzombieenhancement”命名空间下注册的所有方块
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // 创建一个延迟注册器来持有将在“yetanotherzombieenhancement”命名空间下注册的所有物品
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // 创建一个延迟注册器来持有将在“yetanotherzombieenhancement”命名空间下注册的所有创造模式标签页
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 创建一个 id 为“yetanotherzombieenhancement:example_block”的新方块，结合了命名空间和路径
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // 创建一个 id 为“yetanotherzombieenhancement:example_block”的新方块物品，结合了命名空间和路径
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // 创建一个 id 为“yetanotherzombieenhancement:example_id”的新食物物品，营养为 1，饱和度为 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 为示例物品创建一个 id 为“yetanotherzombieenhancement:example_tab”的创造模式标签页，该标签页位于战斗标签页之后
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
            CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.yetanotherzombieenhancement")) //你创造模式标签页标题的语言键
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // 将示例物品添加到标签页。对于你自己的标签页，此方法优于事件
            }).build());

    // mod 类的构造函数是加载 mod 时运行的第一个代码。
    // FML 会识别一些参数类型，如 IEventBus 或 ModContainer，并自动传入它们。
    public YetAnotherZombieEnhancement(IEventBus modEventBus, ModContainer modContainer) {
        // 注册 commonSetup 方法用于 mod 加载
        modEventBus.addListener(this::commonSetup);

        // 将延迟注册器注册到 mod 事件总线，以便注册方块
        BLOCKS.register(modEventBus);
        // 将延迟注册器注册到 mod 事件总线，以便注册物品
        ITEMS.register(modEventBus);
        // 将延迟注册器注册到 mod 事件总线，以便注册标签页
        CREATIVE_MODE_TABS.register(modEventBus);

        // 为我们感兴趣的服务器和其他游戏事件注册自己。
        // 请注意，这仅在并且只有当我们希望 *这个* 类 (YetAnotherZombieEnhancement) 直接响应事件时才是必需的。
        // 如果这个类中没有 @SubscribeEvent 注解的函数（如下面的 onServerStarting()），请不要添加此行。
        NeoForge.EVENT_BUS.register(this);

        // 将物品注册到创造模式标签页
        modEventBus.addListener(this::addCreative);

        // 注册我们 mod 的 ModConfigSpec，以便 FML 可以为我们创建和加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 一些通用设置代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // 将示例方块物品添加到建筑方块标签页
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // 你可以使用 SubscribeEvent 让事件总线发现要调用的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 服务器启动时执行某些操作
        LOGGER.info("HELLO from server starting");
    }
}
