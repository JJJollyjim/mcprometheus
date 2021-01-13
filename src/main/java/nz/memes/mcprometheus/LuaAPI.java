package nz.memes.mcprometheus;

import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import io.prometheus.client.Gauge;

public class LuaAPI implements ILuaAPI {
    IComputerSystem system;
    Gauge metric;

    public LuaAPI(IComputerSystem system, Gauge metric) {
        this.system = system;
        this.metric = metric;
    }

    @Override
    public String[] getNames() {
        return new String[] { "prometheus" };
    }

    @LuaFunction
    public final void pushMetric(String name, double val) {
        metric.labels(Integer.toString(system.getID()), name).set(val);
    }
}
