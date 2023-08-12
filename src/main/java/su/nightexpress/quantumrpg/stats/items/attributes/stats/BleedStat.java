package su.nightexpress.quantumrpg.stats.items.attributes.stats;

import mc.promcteam.engine.utils.NumberUT;
import mc.promcteam.engine.utils.eval.Evaluator;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quantumrpg.manager.effects.main.BleedEffect;
import su.nightexpress.quantumrpg.stats.EntityStats;
import su.nightexpress.quantumrpg.stats.items.attributes.api.AbstractStat;

import java.util.function.Function;

public class BleedStat extends SimpleStat {

    private String  damageFormula;
    private boolean ofMaxHealth;
    private double  duration;
    private double  tickRate;

    public BleedStat(
            @NotNull String name,
            @NotNull String format,
            double cap,

            @NotNull String damageFormula,
            boolean ofMaxHealth,
            double duration,
            double tickRate
    ) {

        super(AbstractStat.Type.BLEED_RATE, name, format, cap);
        this.damageFormula = damageFormula;
        this.ofMaxHealth = ofMaxHealth;
        this.duration = duration;
        this.tickRate = tickRate;
    }

    @NotNull
    public String getDamageFormula() {
        return this.damageFormula;
    }

    public boolean damageOfMaxHealth() {
        return this.ofMaxHealth;
    }

    public double getDuration() {
        return this.duration;
    }

    public void bleed(@NotNull LivingEntity target, double damage) {
        double dmgTick = Evaluator.eval(damageFormula.replace("%damage%", NumberUT.format(damage)), 1);

        Function<LivingEntity, Double> dmgFunc = (entity) -> {
            if (this.ofMaxHealth) {
                return EntityStats.getEntityMaxHealth(target) * dmgTick / 100D;
            }
            return dmgTick;
        };

        BleedEffect bleed = new BleedEffect.Builder(duration, 1.25, dmgFunc).build();
        Bukkit.getConsoleSender().sendMessage("BleedTickRate: " + tickRate);
        bleed.applyTo(target);
    }
}
