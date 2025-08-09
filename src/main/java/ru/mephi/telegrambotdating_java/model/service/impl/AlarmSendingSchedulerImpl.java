package ru.mephi.telegrambotdating_java.model.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;
import ru.mephi.telegrambotdating_java.database.entity.AlarmToSend;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.database.repository.AlarmToSendRepository;
import ru.mephi.telegrambotdating_java.model.service.AlarmSendingScheduler;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmSendingSchedulerImpl implements AlarmSendingScheduler {
    @Autowired
    private ActivityButtonChatRepository activityButtonRepository;

    @Autowired
    private AlarmToSendRepository alarmToSendRepository;

    @Autowired
    private TelegramLongPollingBot bot;

    // Запускается каждую минуту
    @Scheduled(fixedRate = 60000)  // 60 000 мс = 1 минута
    @Transactional
    public void addToQueueAndClean() {
        System.out.println("Пытаемся извлечь нужные анкеты и поместить их в таблицу, откуда другой процесс должен их отправить");

        // 1. Получаем анкеты, которые нужно отправить
        List<ActivityButtonChat> row = activityButtonRepository.getByActivationTimeIsBefore(LocalDateTime.now());

        List<AlarmToSend> alarms = convertToAlarms(row);
        if (alarms == null) return;

        // 2. сохраняем в другую таблицу (откуда отдельный процесс/поток и будет читать), которую тоже надо периодически очищать. Своеобразный Outbox
        alarmToSendRepository.saveAll(alarms);

        // 3. Подчищаем данные о полученных анкетах, ставим следующую доступную анкету через сутки
        row.forEach(it -> {
            activityButtonRepository.resetCountdownData(it.chatId, LocalDateTime.now().plusDays(1));
        });
    }


    /** пытается отправить alarm у всех людей, чья анкета в данный момент готова к отправке
     * если не получается, то retries уменьшается на 1, или запись вообще удаляется
     * если получается, то запись удаляется из таблицы, если retries = 0)
     */
    @Scheduled(fixedRate = 60000)  // 60 000 мс = 1 минута
    @Transactional
    @Override
    public void sendAlarms() {
        // извлекаем все alarms, которые необходимо отправить.
        Iterable<AlarmToSend> alarms = alarmToSendRepository.findAll();

        alarms.forEach(alarm -> {
            try {
                Chat newChat = bot.execute(new GetChat(alarm.receiver));
                bot.execute(new SendMessage(newChat.getId().toString(), alarm.text));
                alarmToSendRepository.deleteById(alarm.id);
            } catch (Exception e) {
                alarmToSendRepository.decrementAndCleanup(alarm.id);
            }
        });
    }

    private List<AlarmToSend> convertToAlarms(List<ActivityButtonChat> row) {
        if (row.isEmpty()) return null;

        List<AlarmToSend> alarms = new ArrayList<>();
        row.forEach (it -> {
            if (it.receiverTag != null && it.address!= null && it.datingTime != null) {
                alarms.add(it.convertToAlarmToSend());
            }
        });

        return alarms;
    }
}
