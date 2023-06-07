package dev.venom;

import dev.venom.command.CommandManager;
import dev.venom.config.Config;
import dev.venom.listener.BukkitEventListener;
import dev.venom.listener.ClientBrandListener;
import dev.venom.listener.JoinQuitListener;
import dev.venom.listener.NetworkListener;
import dev.venom.check.CheckManager;
import dev.venom.manager.PlayerDataManager;
import dev.venom.manager.TickManager;
import dev.venom.packet.processor.ReceivingPacketProcessor;
import dev.venom.packet.processor.SendingPacketProcessor;
import dev.venom.util.ServerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.Messenger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the author.
  Find more about this class on https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/
*/
@Getter
public enum Venom {

    INSTANCE;

    private VenomPlugin plugin;

    private long startTime;

    private TickManager tickManager;
    private ReceivingPacketProcessor receivingPacketProcessor;
    private SendingPacketProcessor sendingPacketProcessor;
    private PlayerDataManager playerDataManager;
    private CommandManager commandManager;
    private ExecutorService packetExecutor;

    public void start(final VenomPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while starting Venom.";
        this.getPlugin().saveDefaultConfig();
        Config.updateConfig();
        
        if(plugin.isEnabled()) {
            tickManager = new TickManager();
            receivingPacketProcessor = new ReceivingPacketProcessor();
            sendingPacketProcessor = new SendingPacketProcessor();
            playerDataManager = new PlayerDataManager();
            commandManager = new CommandManager();
            packetExecutor = Executors.newSingleThreadExecutor();

            CheckManager.setup();

            Bukkit.getOnlinePlayers().forEach(player -> this.getPlayerDataManager().add(player));

            getPlugin().saveDefaultConfig();
            getPlugin().getCommand("venom").setExecutor(commandManager);

            tickManager.start();

            final Messenger messenger = Bukkit.getMessenger();
            messenger.registerIncomingPluginChannel(plugin, "MC|Brand", new ClientBrandListener());

            startTime = System.currentTimeMillis();

            Bukkit.getServer().getPluginManager().registerEvents(new BukkitEventListener(), plugin);
            Bukkit.getServer().getPluginManager().registerEvents(new ClientBrandListener(), plugin);
            Bukkit.getServer().getPluginManager().registerEvents(new JoinQuitListener(), plugin);

            PacketEvents.get().registerListener(new NetworkListener());
        }
    }

    public void stop(final VenomPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while shutting down Venom.";
        if(tickManager != null) tickManager.stop();
    }
}