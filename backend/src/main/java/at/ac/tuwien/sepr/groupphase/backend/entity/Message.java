package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Message {


    @Id
    @GeneratedValue
    private Long id;

    @Convert(converter = EncryptorConverter.class)
    @Column(length = 2048)
    private String content;

    @NotNull
    private LocalDateTime timestamp;

    @ManyToOne
    private Treatment treatment;

    @ManyToOne
    private Credential sender;

    private boolean read;

    public Message setId(Long id) {
        this.id = id;
        return this;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public Message setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Message setTreatment(Treatment treatment) {
        this.treatment = treatment;
        return this;
    }

    public Message setSender(Credential sender) {
        this.sender = sender;
        return this;
    }

    public Message setRead(boolean read) {
        this.read = read;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public Credential getSender() {
        return sender;
    }

    public boolean isRead() {
        return read;
    }
}
