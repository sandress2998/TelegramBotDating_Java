package ru.mephi.telegrambotdating_java.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.telegrambotdating_java.database.entity.AuthorizationCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuthorizationCodeRepository extends CrudRepository<AuthorizationCode, UUID> {
    public AuthorizationCode getByCode(UUID code);

    public void deleteByCode(UUID code);

    public void deleteAllByExpiresAtBefore(LocalDateTime expiresAt);
}
