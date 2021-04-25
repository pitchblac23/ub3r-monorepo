package net.dodian.repositories.definitions;

import net.dodian.models.definitions.ItemDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDefinitionRepository extends JpaRepository<ItemDefinition, Integer> {
}
