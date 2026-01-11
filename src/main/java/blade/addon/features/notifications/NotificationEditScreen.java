package blade.addon.features.notifications;

import config.practical.ConfigScroll;
import config.practical.utilities.Constants;
import config.practical.widgets.ConfigBool;
import config.practical.widgets.ConfigString;
import config.practical.widgets.sliders.ConfigInt;
import config.practical.widgets.sound.ConfigSound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

public class NotificationEditScreen extends Screen {

    private final Screen parent;
    private final ConfigScroll scroll;
    private final Notification notification;


    protected NotificationEditScreen(Notification notification) {
        super(Text.empty());

        MinecraftClient client = MinecraftClient.getInstance();
        parent = client.currentScreen;
        Window window = client.getWindow();
        scroll = new ConfigScroll(0,  30, window.getScaledWidth(), window.getScaledHeight(), Constants.WIDGET_WIDTH);
        this.notification = notification;
    }

    private void initScrollWidgets() {
        scroll.children().clear();
        scroll.add(new ConfigString(Text.literal("Trigger on message"), notification::getMatchString, notification::setMatchString, false));
        scroll.add(new ConfigString(Text.literal("Notification message"), notification::getNotificationString, notification::setNotificationString));
        scroll.add(new ConfigBool(Text.literal("Use regex"), notification::useRegex, notification::setUseRegex));
        scroll.add(new ConfigInt(Text.literal("Duration in ticks"), notification::getTicks, notification::setTicks, 1, 10, 40));
        scroll.add(new ConfigSound(Text.literal("Sound"), notification.getSound(), 2, 2, true));
        scroll.add(new ConfigBool(Text.literal("Send command"), notification::sendCommand, notification::setSendCommand));
        scroll.add(new ConfigString(Text.literal("Command"), notification::getCommand, notification::setCommand));
        scroll.update();
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(scroll);
        initScrollWidgets();
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }
}
