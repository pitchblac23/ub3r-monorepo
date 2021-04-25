package net.dodian.repositories.definitions.spawns;

import net.dodian.models.definitions.spawn.NpcSpawnDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcSpawnsRepository extends JpaRepository<NpcSpawnDefinition, Integer> {
}
