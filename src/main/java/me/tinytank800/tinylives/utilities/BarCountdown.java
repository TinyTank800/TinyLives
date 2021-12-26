package me.tinytank800.tinylives.utilities;

import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class BarCountdown extends BukkitRunnable {
    private final BossBar bar;
    private Integer timeLeft;
    private final Integer TimeSections;
    private final Integer timeTotal;

    public BarCountdown(BossBar b, int times) {
        this.TimeSections = times/8;
        this.bar = b;
        this.timeTotal = times;
        this.timeLeft = times;
    }

    @Override
    public void run() {
        if (timeLeft > 0) {
            bar.setProgress(timeLeft.doubleValue() / timeTotal.doubleValue());

            timeLeft -= TimeSections;
        } else {
            this.cancel();
        }
    }
}
