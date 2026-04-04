package de.rayzs.vit.launch;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.AddonManager;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.event.EventManager;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.gui.GUI;
import de.rayzs.vit.api.gui.Screen;
import de.rayzs.vit.api.image.ImageProvider;
import de.rayzs.vit.api.objects.game.Game;
import de.rayzs.vit.api.objects.items.Agent;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.session.Session;
import de.rayzs.vit.api.session.SessionState;
import de.rayzs.vit.api.utils.FileUtils;

import java.io.File;

public class ImplVITAPI implements VITAPI {

    private final ImageProvider imageProvider;
    private final EventManager eventManager;
    private final AddonManager addonManager;

    private final Configuration settings;
    private final Session session;


    private SessionState state = SessionState.VALORANT_NOT_OPEN;
    private Weapon selectedWeapon = Weapon.VANDAL;
    private Agent[] owningAgents;

    private Screen mainGui;
    private Game game;

    public ImplVITAPI() {

        // Load default settings
        this.settings = loadConfig(
                "configs/settings.json",
                FileDir.CONFIGS
        );


        // Initialize all implementations
        this.session = new ImplSession();
        this.imageProvider = new ImplImageProvider();

        this.eventManager = new ImplEventManager();
        this.addonManager = new ImplAddonManager(this);
    }

    @Override
    public Screen getMainGui() {
        return this.mainGui;
    }

    @Override
    public void setMainGui(final Screen mainGui) {
        if (this.mainGui != null) {
            throw new IllegalStateException("Main GUI is already set!");
        }

        this.mainGui = mainGui;
    }

    @Override
    public Configuration getSettings() {
        return this.settings;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public SessionState getSessionState() {
        return this.state;
    }

    @Override
    public void updateSessionState(final SessionState state) {
        this.state = state;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public AddonManager getAddonManager() {
        return this.addonManager;
    }

    @Override
    public ImageProvider getImageProvider() {
        return this.imageProvider;
    }

    @Override
    public Agent[] getOwningAgents() {
        return this.owningAgents;
    }

    @Override
    public void updateOwningAgents(Agent... agents) {
        if (this.owningAgents != null) {
            throw new RuntimeException("Cannot set owning agents, since it's already set!");
        }

        this.owningAgents = agents;
    }

    @Override
    public boolean hasGame() {
        return this.game != null;
    }

    @Override
    public Game getGame() {
        if (this.game == null) {
            throw new NullPointerException("There's no running game!");
        }

        return this.game;
    }

    @Override
    public void setGame(final Game game) {
        this.game = game;
    }

    @Override
    public Weapon getSelectedWeapon() {
        if (this.selectedWeapon == null) {
            throw new NullPointerException("No weapon has been selected!");
        }

        return this.selectedWeapon;
    }

    @Override
    public void setSelectedWeapon(final Weapon selectedWeapon) {
        this.selectedWeapon = selectedWeapon;
    }

    /**
     * Extracts a default config file and initializes it.
     * In case the file is already extracted, it won't override
     * it and initialize {@link Configuration} with the already
     * existing file. Purpose is only for this class only and
     * is not relevant for anyone else here.
     *
     * @param innerFilePath Inner file path. (Resource path)
     * @param outerFileDir Folder where to extract the file to.
     *
     * @return Extracted {@link Configuration}.
     */
    private Configuration loadConfig(final String innerFilePath, final FileDir outerFileDir) {
        final File settingsFile = FileUtils.exportResourceFile(
                null,
                innerFilePath,
                outerFileDir
        );

        return new Configuration(settingsFile);
    }
}
