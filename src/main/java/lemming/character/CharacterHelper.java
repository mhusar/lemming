package lemming.character;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 * A helper class with methods for special character related tasks.
 */
public class CharacterHelper {
    /**
     * Returns a JsonArray with character data.
     *
     * @return A JsonArray with character data.
     */
    public static JsonArray getCharacterData() {
        List<Character> characters = new CharacterDao().getAll();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (Character character : characters) {
            jsonArrayBuilder.add(character.getCharacter());
        }

        return jsonArrayBuilder.build();
    }
}
