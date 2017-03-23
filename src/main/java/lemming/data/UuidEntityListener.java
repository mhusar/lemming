package lemming.data;

import lemming.character.Character;
import lemming.context.BaseContext;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.sense.Sense;
import lemming.user.User;

import javax.persistence.PrePersist;

/**
 * Sets a UUID for an entity if its class is recognized.
 */
public class UuidEntityListener {
    /**
     * Called on pre-persist.
     *
     * @param object an object
     */
    @PrePersist
    public void onPrePersist(Object object) {
        setUuid(object);
    }

    /**
     * Sets a UUID for an entity if its class is recognized.
     *
     * @param object an object
     */
    private void setUuid(Object object) {
        if (object instanceof BaseContext) {
            BaseContext context = (BaseContext) object;
            context.setUuid();
        } else if (object instanceof Character) {
            Character character = (Character) object;
            character.setUuid();
        } else if (object instanceof Lemma) {
            Lemma lemma = (Lemma) object;
            lemma.setUuid();
        } else if (object instanceof Pos) {
            Pos pos = (Pos) object;
            pos.setUuid();
        } else if (object instanceof Sense) {
            Sense sense = (Sense) object;
            sense.setUuid();
        } else if (object instanceof User) {
            User user = (User) object;
            user.setUuid();
        } else {
            throw new IllegalStateException("Cannot set UUID to unknown class");
        }
    }
}
