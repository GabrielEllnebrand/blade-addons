package blade.addon.utils.dungeon;

import blade.addon.Bladeaddons;
import blade.addon.utils.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Split {
    private final String name, dialogue;
    private int tick;

    public Split(String name, String dialogue) {
        this.name = name;
        this.dialogue = dialogue;
        this.tick = 0;
    }

    public boolean matches(String string) {
        return string.contains(dialogue);
    }

    public void tick() {
        this.tick++;
    }

    public void reset() {
        tick = 0;
    }

    public String formatString() {
        double time = tick * Constants.TICK_DURATION;
        return name + " : " + Constants.DECIMAL_FORMAT.format(time) + "s";
    }

    public static @NotNull HashMap<String, ArrayList<Split>> readSplits(String path) {

        try (InputStream stream = Bladeaddons.class.getResourceAsStream(path)) {

            if (stream == null) return new HashMap<>();

            try (Reader reader = new InputStreamReader(stream)) {

                JsonElement element = JsonParser.parseReader(reader);
                return parseSplits(element);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new HashMap<>();
    }

    private static HashMap<String, ArrayList<Split>> parseSplits(JsonElement jsonElement) {
        HashMap<String, ArrayList<Split>> floors = new HashMap<>();
        JsonObject object = jsonElement.getAsJsonObject();

        for (Map.Entry<String, JsonElement> floorEntry : object.entrySet()) {

            ArrayList<Split> splits = new ArrayList<>();

            String floorName = floorEntry.getKey();
            JsonArray dialoguesArray = floorEntry.getValue().getAsJsonArray();

            for (JsonElement dialogueElement : dialoguesArray) {
                JsonObject dialogueObj = dialogueElement.getAsJsonObject();

                String name = dialogueObj.get("name").getAsString();
                String dialogue = dialogueObj.get("dialogue").getAsString();

                splits.add(new Split(name, dialogue));
            }

            floors.put(floorName, splits);
        }
        return floors;
    }
}
