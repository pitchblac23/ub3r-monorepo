package net.dodian.repositories.definitions.spawns;

import net.dodian.models.definitions.spawn.ObjectSpawnDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectSpawnRepository extends JpaRepository<ObjectSpawnDefinition, Integer> {
}
