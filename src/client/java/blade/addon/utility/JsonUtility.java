package blade.addon.utility;

import blade.addon.BladeAddonsClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class JsonUtility {

    public static @NotNull ArrayList<BlockPos> loadBlockPosList(String path) {
        try (InputStream stream = BladeAddonsClient.class.getResourceAsStream(path)) {

            if (stream == null) return new ArrayList<>();

            try (Reader reader = new InputStreamReader(stream)) {

                JsonElement element = JsonParser.parseReader(reader);
                return JsonUtility.parseBlockPosList(element);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }

    private static ArrayList<BlockPos> parseBlockPosList(JsonElement jsonElement) {
        ArrayList<BlockPos> positions = new ArrayList<>();

        if (!jsonElement.isJsonArray()) return positions;

        JsonArray array = jsonElement.getAsJsonArray();
        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;

            JsonObject obj = element.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            int z = obj.get("z").getAsInt();

            positions.add(new BlockPos(x, y, z));
        }

        return positions;
    }
}
