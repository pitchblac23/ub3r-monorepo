package net.dodian;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GameServer implements InitializingBean {

    private final GameBoot gameBoot;

    public static void main(String[] params) {
        SpringApplication.run(GameServer.class, params);
    }

    @Autowired
    public GameServer(GameBoot gameBoot) {
        this.gameBoot = gameBoot;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.gameBoot.boot();
    }
}
