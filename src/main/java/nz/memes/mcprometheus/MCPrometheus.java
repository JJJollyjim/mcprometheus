package nz.memes.mcprometheus;

import dan200.computercraft.api.ComputerCraftAPI;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("mcprometheus")
public class MCPrometheus {

    private static final Logger LOGGER = LogManager.getLogger();

    static final Gauge ccMetric = Gauge.build()
            .name("computercraft").help("Computer-defined metric.").labelNames("computerId", "name").register();

    public MCPrometheus() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        new PlayerStatsCollector(event.getServer()).register();
        new WorldCollector(event.getServer()).register();
        DefaultExports.initialize();

        ComputerCraftAPI.registerAPIFactory(system -> new LuaAPI(system, ccMetric));

        try {
            new HTTPServer(6969);
            LOGGER.info("Prometheus has started!");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Prometheus startup failed");
        }
    }
}