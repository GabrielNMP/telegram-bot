package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Reminder;
import pro.sky.telegrambot.repository.ReminderRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final ReminderRepository reminderRepository;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            Message message = update.message();
            if (update.message().text().equals("/start")) {
                logger.info("Bot was started /start");
                telegramBot.execute(new SendMessage(getChatId(message),
                        "<Бот создан чтобы не забыть о важных делах> ;) " +
                                "\nЧтобы создать напоминания, необходимо отправить сообщение в следующем формате \"01.01.2023 20:00 Сделать домашнюю работу\", " +
                                "напишите дату, время и текст."));
            } else {
                try {
                    parseMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void parseMessage(Message message) throws Exception {
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(message.text());
        if (matcher.matches()) {
            String dateTime = matcher.group(1);
            String text = matcher.group(3);
            LocalDateTime reminderDateTime = LocalDateTime.parse(dateTime,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            reminderRepository.save(new Reminder(getChatId(message), text, reminderDateTime));
            telegramBot.execute(new SendMessage(getChatId(message),
                    "Это напоминание \"" + text + "\" " + dateTime));
        } else {
            logger.error("Can not parse reminder message: " + message);
            telegramBot.execute(new SendMessage(getChatId(message),
                    "Неправильный формат сообщения " +
                            "Напишите сообщение вида " +
                            "\"01.01.2023 20:00 Сделать домашнюю работу\"."));
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void getScheduledMessage() {
        try {
            LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            List<Reminder> notifications = reminderRepository.findByDateAndTimeEquals(currentTime);
            logger.info("Reminder was found");
            for (Reminder task : notifications) {
                if (task != null) {
                    telegramBot.execute(new SendMessage(task.getChatId(),
                            "Напоминаю ! " + task));
                    logger.info("Reminder sent");
                }
            }
        } catch (RuntimeException e) {
            logger.info("No reminder");
            throw new RuntimeException();
        }
    }

    private Long getChatId(Message message) {
        return message.chat().id();
    }
}

