package me.forever17.yetanotherzombieenhancement;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// 此类不会在专用服务器上加载。在此处访问客户端代码是安全的。
@Mod(value = YetAnotherZombieEnhancement.MODID, dist = Dist.CLIENT)
// 您可以使用 EventBusSubscriber 自动注册类中所有使用 @SubscribeEvent 注解的静态方法
@EventBusSubscriber(modid = YetAnotherZombieEnhancement.MODID, value = Dist.CLIENT)
public class YetAnotherZombieEnhancementClient {
    public YetAnotherZombieEnhancementClient(ModContainer container) {
        // 允许 NeoForge 为此 mod 的配置创建配置屏幕。
        // 通过进入“Mods”屏幕 > 单击您的 mod > 单击“config”来访问配置屏幕。
        // 不要忘记将您的配置选项的翻译添加到 en_us.json 文件中。
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 一些客户端设置代码
        YetAnotherZombieEnhancement.LOGGER.info("HELLO FROM CLIENT SETUP");
        YetAnotherZombieEnhancement.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
