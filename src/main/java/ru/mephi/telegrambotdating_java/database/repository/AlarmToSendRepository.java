package ru.mephi.telegrambotdating_java.database.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mephi.telegrambotdating_java.database.entity.AlarmToSend;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlarmToSendRepository extends CrudRepository<AlarmToSend, UUID> {
    @Modifying
    @Query(
            value = """
    WITH updated AS (
        UPDATE alarm_to_send 
        SET retries = retries - 1 
        WHERE id = :id
        RETURNING *
    )
    DELETE FROM alarm_to_send 
    WHERE id IN (
        SELECT id FROM updated 
        WHERE retries <= 0
    )
    """,
            nativeQuery = true
    )
    void decrementAndCleanup(@Param("id") UUID id);
}
