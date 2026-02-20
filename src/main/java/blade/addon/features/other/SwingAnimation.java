package blade.addon.features.other;

public class SwingAnimation {

    private static int ignoreCount = 0;

    /**
     * tells consumeSwing to ignore next swing
     */
    public static void ignoreNext(int count) {
        ignoreCount = count;
    }

    /**
     * Consumes a swing and checks if it should ignore swing rendering
     */
    public static void consumeSwing() {
        if (ignoreCount > 0) {
            ignoreCount--;
        }
    }

    /**
     * Checks if it should ignore the swing
     * @return true if it should ignore the swing animation, else false
     */
    public static boolean shouldIgnore() {
        return ignoreCount > 0;
    }
}
