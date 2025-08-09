package ru.mephi.telegrambotdating_java.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.telegrambotdating_java.database.entity.AuthorizationCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuthorizationCodeRepository extends CrudRepository<AuthorizationCode, UUID> {
    AuthorizationCode getByCodeAndExpiresAtAfter(UUID code, LocalDateTime expiresAt);

    void deleteByCode(UUID code);

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAt);
}
