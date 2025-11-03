package blade.addon.utils;

import blade.addon.Bladeaddons;
import blade.addon.utils.dungeon.PositionMessage;
import blade.addon.utils.dungeon.Split;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.math.Vec3d;
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

    public static @NotNull ArrayList<PositionMessage> readPositionalMessages(String path) {

        try (InputStream stream = Bladeaddons.class.getResourceAsStream(path)) {

            if (stream == null) return new ArrayList<>();

            try (Reader reader = new InputStreamReader(stream)) {

                JsonElement element = JsonParser.parseReader(reader);
                return parsePositonalMessages(element);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }

    private static ArrayList<PositionMessage> parsePositonalMessages(JsonElement jsonElement) {
        ArrayList<PositionMessage>positionalMessages = new ArrayList<>();



        if (!jsonElement.isJsonArray()) return positionalMessages;

        JsonArray array = jsonElement.getAsJsonArray();
        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;

            JsonObject obj = element.getAsJsonObject();
            double minX = obj.get("min_x").getAsDouble();
            double minY = obj.get("min_y").getAsDouble();
            double minZ = obj.get("min_z").getAsDouble();
            double maxX = obj.get("max_x").getAsDouble();
            double maxY = obj.get("max_y").getAsDouble();
            double maxZ = obj.get("max_z").getAsDouble();
            String message = obj.get("message").getAsString();

            JsonElement sectionsElement = obj.get("sections");
            JsonArray sectionsArray = sectionsElement.getAsJsonArray();

            int[] sections = new int[sectionsArray.size()];
            int index = 0;
            for (JsonElement jsonSection: sectionsArray) {
                sections[index] = jsonSection.getAsInt();
                index++;
            }


            positionalMessages.add(new PositionMessage(message, new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ), sections));
        }

        return positionalMessages;
    }
}
