package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String reminderText;
    private LocalDateTime dateAndTime;

    public Reminder(){
    }

    public Reminder(Long chatId, String reminderText, LocalDateTime dateAndTime) {
        this.chatId = chatId;
        this.reminderText = reminderText;
        this.dateAndTime = dateAndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id) && Objects.equals(chatId, reminder.chatId) && Objects.equals(reminderText, reminder.reminderText) && Objects.equals(dateAndTime, reminder.dateAndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, reminderText, dateAndTime);
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", reminderText='" + reminderText + '\'' +
                ", dateTime=" + dateAndTime +
                '}';
    }
}
