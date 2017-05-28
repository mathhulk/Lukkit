package nz.co.jammehcow.lukkit.environment.plugin;

import com.avaje.ebean.EbeanServer;
import nz.co.jammehcow.lukkit.Main;
import nz.co.jammehcow.lukkit.environment.LuaEnvironment;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.*;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * The type Lukkit plugin.
 *
 * @author jammehcow
 */
public class LukkitPlugin implements Plugin {
    private String name;
    private LukkitPluginFile pluginFile;
    private LuaValue pluginMain;
    private LuaFunction loadCB;
    private LuaFunction enableCB;
    private LuaFunction disableCB;
    private File pluginConfig;
    private LukkitPluginLoader pluginLoader;
    private LukkitConfigurationFile config;
    private PluginDescriptionFile descriptor;
    private Globals globals;
    private File dataFolder;
    private boolean enabled = false;

    private HashMap<String, LuaFunction> commands = new HashMap<>();

    /**
     * Instantiates a new Lukkit plugin.
     *
     * @param loader the loader
     * @param file   the file
     */
    public LukkitPlugin(LukkitPluginLoader loader, LukkitPluginFile file) throws InvalidPluginException {
        this.pluginFile = file;

        try {
            this.descriptor = new PluginDescriptionFile(this.pluginFile.getPluginYML());
        } catch (InvalidDescriptionException e) {
            throw new InvalidPluginException("The description provided was invalid or missing.");
        }

        this.pluginMain = LuaEnvironment.globals.load(new InputStreamReader(this.pluginFile.getResource(this.descriptor.getMain())), this.descriptor.getMain());
        this.dataFolder = new File(Main.instance.getDataFolder().getAbsolutePath() + File.separator + this.name);
        this.pluginLoader = loader;
        this.globals = LuaEnvironment.globals;

        // Sets callbacks (if any) and loads the commands & events into memory.
        Optional<String> isValid = this.checkValidity();
        if (isValid.isPresent())
            throw new InvalidPluginException("An issue occurred when loading the plugin: \n" + isValid.get());

        this.pluginMain.call();
    }

    @Override
    public File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return this.descriptor;
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    @Override
    public InputStream getResource(String path) {
        return this.pluginFile.getResource(path);
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {
        try {
            Files.copy(this.pluginFile.getDefaultConfig(), new File(this.dataFolder.getAbsolutePath() + File.separator + "config.yml").toPath());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        File resourceOutput = new File(this.dataFolder.getAbsolutePath() + File.separator + "");

        if (!resourceOutput.exists() || replace) {
            // TODO
        } else {
            getLogger().warning("You tried to access a resource that doesn't exist");
        }
    }

    @Override
    public void reloadConfig() {
        this.config = new LukkitConfigurationFile(this.pluginConfig);
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    @Override
    public Server getServer() {
        return Main.instance.getServer();
    }

    @Override
    public boolean isEnabled() {
        return LukkitPluginLoader.loadedPlugins.contains(this) && this.enabled;
    }

    @Override
    public void onEnable() {
        if (this.enableCB != null) this.enableCB.call();
        this.enabled = true;
    }

    @Override
    public void onDisable() {
        if (this.disableCB != null) this.disableCB.call();
        this.enabled = false;
    }

    @Override
    public void onLoad() {
        if (this.loadCB != null) this.loadCB.call();
    }

    @Override
    public boolean isNaggable() {
        // TODO: lookup
        return false;
    }

    @Override
    public void setNaggable(boolean b) {
        // TODO: lookup
    }

    @Override
    public EbeanServer getDatabase() {
        // Will be replaced with HSQLDB
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        // TODO: lookup
        return null;
    }

    @Override
    public Logger getLogger() {
        // TODO: create plugin-individual logger
        return Main.logger;
    }

    @Override
    public String getName() { return this.getDescription().getName(); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (commands.containsKey(command.getName())) {

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] strings) {
        // TODO
        return null;
    }

    private Optional<String> checkValidity() {
        if (this.pluginMain == null) {
            return Optional.of("Unable to load the main Lua file. It may be missing from the plugin file or corrupted.");
        } else if (this.descriptor == null) {
            return Optional.of("Unable to load the plugin's description file. It may be missing from the plugin file or corrupted.");
        }

        return Optional.empty();
    }
}