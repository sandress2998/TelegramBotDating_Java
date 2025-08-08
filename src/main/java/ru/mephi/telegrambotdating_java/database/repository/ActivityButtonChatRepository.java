package ru.mephi.telegrambotdating_java.database.repository;

import org.springframework.data.jdbc.repository.query.Modifying;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityButtonChatRepository extends CrudRepository<ActivityButtonChat, UUID> {

    ActivityButtonChat getByChatId(String chatId);

    @Modifying
    @Query(value = "UPDATE activity_button_chat a SET a.isCountdownActive = :isActive WHERE a.chatId = :chatId")
    void updateCountdownStatus(String chatId, boolean isActive);

    @Modifying
    @Query(value = "UPDATE activity_button_chat SET activation_time = :activationTime WHERE chat_id = :chatId", nativeQuery = true)
    void saveActivationTime(String chatId, LocalDateTime activationTime);

    @Query(value = "SELECT CASE WHEN :currentTime < a.nextAvailableTime THEN true ELSE false END FROM activity_button_chat a WHERE a.chatId = :chatId")
    boolean isTimeBeforeNextAvailable(String chatId, LocalDateTime currentTime);

    @Modifying
    @Query(value = "UPDATE activity_button_chat a SET a.deactivationCode = CASE WHEN :deactivationCode IS NOT NULL" +
            " THEN :deactivationCode ELSE a.deactivationCode END, a.receiverTag = CASE WHEN :receiverTag IS NOT NULL" +
            " THEN :receiverTag ELSE a.receiverTag END, a.address = CASE WHEN :address IS NOT NULL" +
            " THEN :address ELSE a.address END, a.datingTime = CASE WHEN :datingTime IS NOT NULL" +
            " THEN :datingTime ELSE a.datingTime END WHERE a.chatId = :chatId")
    void updateAlarmSendingForm(String chatId, String receiverTag, Integer deactivationCode, String address, LocalDateTime datingTime);

    @Query(value = "SELECT CASE WHEN deactivation_code = :deactivationCode THEN true ELSE false END FROM activity_button_chat WHERE chat_id = :chatId", nativeQuery = true)
    boolean isDeactivationCodeValid(String chatId, int deactivationCode);

    @Query(value = "SELECT is_countdown_active FROM activity_button_chat WHERE chat_id = :chatId", nativeQuery = true)
    boolean isCountdownActive(String chatId);

    @Modifying
    @Query(value = "UPDATE activity_button_chat SET is_countdown_active = false, receiver_tag = null," +
            " deactivation_code = null, address = null, dating_time = null, activation_time = null," +
            " next_available = :nextAvailableTime WHERE chat_id = :chatId", nativeQuery = true)
    void resetCountdownData(String chatId, LocalDateTime nextAvailableTime);

    List<ActivityButtonChat> getActivityButtonChatByActivationTimeIsAfter(LocalDateTime activationTime);

    @Modifying
    @Query("DELETE FROM AlarmToSend a WHERE a.id IN :ids")
    void deleteAllByIds(@Param("ids") List<UUID> ids);

    void deleteByClientId(UUID clientId);
}