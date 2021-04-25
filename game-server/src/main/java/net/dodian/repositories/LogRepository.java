package net.dodian.repositories;

import net.dodian.models.LogItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogItem, Integer> {
}
