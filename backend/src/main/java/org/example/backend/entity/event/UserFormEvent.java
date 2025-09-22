package org.example.backend.entity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserFormEvent extends ApplicationEvent {
    final Integer userId;
    final Integer forumId;
    final ActionType actionType;

    public UserFormEvent(Object source, Integer userId, Integer forumId, ActionType actionType) {
        super(source);
        this.userId = userId;
        this.forumId = forumId;
        this.actionType = actionType;
    }

    public enum ActionType {
        JOIN, QUIT
    }
}
