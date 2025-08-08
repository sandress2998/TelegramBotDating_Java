package ru.mephi.telegrambotdating_java.database.repository;

import org.springframework.data.jdbc.repository.query.Modifying;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TelegramBotRepository extends CrudRepository<ActivityButtonChat, UUID> {

    ActivityButtonChat getByChatId(UUID chatId);

    @Modifying
    @Query(value = "UPDATE activity_button_chat a SET a.isCountdownActive = :isActive WHERE a.chatId = :chatId")
    int updateCountdownStatus(UUID chatId, boolean isActive);

    @Modifying
    @Query(value = "UPDATE activity_button_chat SET activation_time = :activationTime WHERE chat_id = :chatId", nativeQuery = true)
    void saveActivationTime(UUID chatId, LocalDateTime activationTime);

    @Query(value = "SELECT CASE WHEN :currentTime < a.nextAvailableTime THEN true ELSE false END FROM activity_button_chat a WHERE a.chatId = :chatId")
    boolean isTimeBeforeNextAvailable(UUID chatId, LocalDateTime currentTime);

    @Modifying
    @Query(value = "UPDATE activity_button_chat a SET a.deactivationCode = CASE WHEN :deactivationCode IS NOT NULL" +
            " THEN :deactivationCode ELSE a.deactivationCode END, a.receiverTag = CASE WHEN :receiverTag IS NOT NULL" +
            " THEN :receiverTag ELSE a.receiverTag END, a.address = CASE WHEN :address IS NOT NULL" +
            " THEN :address ELSE a.address END, a.datingTime = CASE WHEN :datingTime IS NOT NULL" +
            " THEN :datingTime ELSE a.datingTime END WHERE a.chatId = :chatId")
    void updateAlarmSendingForm(UUID chatId, String receiverTag, Integer deactivationCode, String address, LocalDateTime datingTime);

    @Query(value = "SELECT CASE WHEN deactivation_code = :deactivationCode THEN true ELSE false END FROM activity_button_chat WHERE chat_id = :chatId", nativeQuery = true)
    boolean isDeactivationCodeValid(UUID chatId, int deactivationCode);

    @Query(value = "SELECT is_countdown_active FROM activity_button_chat WHERE chat_id = :chatId", nativeQuery = true)
    boolean isCountdownActive(UUID chatId);

    @Modifying
    @Query(value = "UPDATE activity_button_chat SET is_countdown_active = false, receiver_tag = null," +
            " deactivation_code = null, address = null, dating_time = null, activation_time = null," +
            " next_available = :nextAvailableTime WHERE chat_id = :chatId", nativeQuery = true)
    void resetCountdownData(UUID chatId, LocalDateTime nextAvailableTime);
}