package net.dodian;

import net.dodian.handlers.ConfigHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class GameBoot {
    private final Logger logger = Logger.getLogger(GameBoot.class.getName());

    private final ConfigHandler configHandler;

    @Autowired
    public GameBoot(ConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    public void boot() {
        logger.info("");
        logger.info("    ____            ___               ");
        logger.info("   / __ \\____  ____/ (_)___ _____    ");
        logger.info("  / / / / __ \\/ __  / / __ `/ __ \\  ");
        logger.info(" / /_/ / /_/ / /_/ / / /_/ / / / /    ");
        logger.info("/_____/\\____/\\____/_/\\____/_/ /_/  ");
        logger.info("");
        logger.info("Initializing " + configHandler.getServerConfig().getName() + " game server...");


        logger.info("Server is now running!");
    }
}
