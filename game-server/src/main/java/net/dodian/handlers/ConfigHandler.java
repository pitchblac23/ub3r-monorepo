package net.dodian.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.dodian.models.config.ServerConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ConfigHandler implements InitializingBean {
    private final File CONFIG_FILE = new File(System.getProperty("user.dir") + "/data/config.yaml");

    private ServerConfig serverConfig;

    public ConfigHandler() {

    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory())
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT);

        if(!CONFIG_FILE.exists()) {
            yamlMapper.writeValue(CONFIG_FILE, new ServerConfig());
            throw new Exception("Make sure to go through the config file before starting the server again...");
        }

        serverConfig = yamlMapper.readValue(CONFIG_FILE, ServerConfig.class);
    }
}
