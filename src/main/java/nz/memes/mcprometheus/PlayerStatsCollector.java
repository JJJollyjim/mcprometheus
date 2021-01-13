package nz.memes.mcprometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class PlayerStatsCollector extends Collector {
    MinecraftServer server;

    PlayerStatsCollector(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        // TODO probably need to use server.runImmediately (or runAsync), and get the result somehow, to avoid races
        // That said, I have now hit it pretty heavily with Apache Bench, and not had any errors show up...

        List<MetricFamilySamples> mfs = new ArrayList<>();

        for (StatType<?> type : ForgeRegistries.STAT_TYPES) {
            // This mf eating beans
            GaugeMetricFamily mf = new GaugeMetricFamily("stat_" + type.getRegistryName().toString().replaceAll("minecraft:", "").replaceAll(":", "_"), "1", Arrays.asList("player", "param"));

            for (Stat<?> s : type) {
                Object val = s.getValue();

                for (ServerPlayerEntity p : server.getPlayerList().getPlayers()) {
                    mf.addMetric(Arrays.asList(p.getName().getString(), val.toString()), p.getStats().getValue(s));
                }
            }

            mfs.add(mf);

        }


        return mfs;
    }
}
