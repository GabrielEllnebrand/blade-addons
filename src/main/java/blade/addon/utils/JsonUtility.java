package blade.addon.utils;

import blade.addon.Bladeaddons;
import blade.addon.utils.dungeon.Split;
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

public class JsonUtility {

    public static @NotNull HashMap<String, ArrayList<Split>> readSplits(String path) {

        try (InputStream stream = Bladeaddons.class.getResourceAsStream(path)) {

            if (stream == null) return new HashMap<>();

            try (Reader reader = new InputStreamReader(stream)) {

                JsonElement element = JsonParser.parseReader(reader);
                return parseSplits(element);
            }
        } catch (IOException e) {
            Debug.LOGGER.error("Failed to parse a split");
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

                int color = dialogueObj.get("color").getAsInt();
                String name = dialogueObj.get("name").getAsString();
                String start = dialogueObj.get("start").getAsString();
                String end = dialogueObj.get("end").getAsString();


                splits.add(new Split(name, start, end, color));
            }

            floors.put(floorName, splits);
        }
        return floors;
    }
}
