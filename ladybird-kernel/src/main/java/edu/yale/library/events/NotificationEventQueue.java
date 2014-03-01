package edu.yale.library.events;

import edu.yale.library.beans.User;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO temp. A memory queue.
 */
public class NotificationEventQueue {
    public static final int CAPACITY = 50;
    static Queue<NotificationItem> notificationEventQueue = new ArrayBlockingQueue(CAPACITY);

    public static boolean addEvent(NotificationItem e) {
        return notificationEventQueue.add(e);
    }

    public static NotificationItem getLastEvent() {
        return notificationEventQueue.poll();
    }

    public class NotificationItem {

        public NotificationItem(Event e, List<User> users) {
            this.e = e;
            this.users = users;
        }

        private Event e;
        private List<User> users;

        public Event getE() {
            return e;
        }

        public List<User> getUsers() {
            return users;
        }
    }

}
