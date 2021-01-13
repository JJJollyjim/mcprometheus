package nz.memes.mcprometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorldCollector extends Collector {
    private static final long[] UNLOADED = new long[] {0};
    MinecraftServer server;

    WorldCollector(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public List<Collector.MetricFamilySamples> collect() {
        GaugeMetricFamily tickTimeGauge = new GaugeMetricFamily("world_tick_time", "Time spent ticking each dimension.", Collections.singletonList("world"));
        GaugeMetricFamily chunksLoadedGauge = new GaugeMetricFamily("world_chunks_loaded", "Number of chunk in memory for each dimension.", Collections.singletonList("world"));

        for (ServerWorld dim : server.getWorlds()) {
            long[] times = server.getTickTime(dim.getDimensionKey());

            if (times == null)
                times = UNLOADED;

            // func_244267_aX == getDynamicRegistries
            double worldTickTime = mean(times) * 1.0E-6D;

            List<String> labels = Collections.singletonList(dim.getDimensionKey().getLocation().toString());
            tickTimeGauge.addMetric(labels, worldTickTime);
            chunksLoadedGauge.addMetric(labels, dim.getChunkProvider().getLoadedChunkCount());
        }

        return Arrays.asList(tickTimeGauge, chunksLoadedGauge);
    }

    private static long mean(long[] values)
    {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
