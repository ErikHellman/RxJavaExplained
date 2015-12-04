package se.hellsoft.rxjavaexplained.eventbus;

/**
 * A different, and maybe better, design for an event bus would be to use this as an
 * abstract base class and create concrete subclasses for the different usages.
 */
public class Event {
    public long timeStamp;
    public String name;
    public String description;
    public Type type;

    // Don't tell Colt!
    public enum Type {
        Kitten, Dog, Rabbit
    }

    @Override
    public String toString() {
        return "Event{" +
                "timeStamp=" + timeStamp +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}
