package edu.yale.library.ladybird.kernel.notificaiton;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.event.Event;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO temp. A memory queue.
 */
public class NotificationEventQueue {

    public static final int CAPACITY = 50;

    private static Queue<NotificationItem> notificationEventQueue = new ArrayBlockingQueue<>(CAPACITY);

    public static boolean addEvent(NotificationItem e) {
        return notificationEventQueue.add(e);
    }

    public static NotificationItem getLastEvent() {
        return notificationEventQueue.poll();
    }

    public class NotificationItem {

        public NotificationItem(Event event, List<User> users, String message, String subject) {
            this.event = event;
            this.users = users;
            this.message = message;
            this.subject = subject;
        }

        private Event event;
        private List<User> users;
        private String message;
        private String subject;

        public Event getEvent() {
            return event;
        }

        public List<User> getUsers() {
            return users;
        }

        public String getMessage() {
            return message;
        }

        public String getSubject() {
            return subject;
        }
    }

}
