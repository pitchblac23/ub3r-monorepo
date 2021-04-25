package net.dodian.repositories.definitions.spawns;

import net.dodian.models.definitions.spawn.GroundItemSpawnDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroundItemSpawnRepository extends JpaRepository<GroundItemSpawnDefinition, Integer> {
}
