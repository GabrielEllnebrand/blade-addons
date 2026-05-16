package blade.addon.features.notifications;

import com.mojang.blaze3d.platform.Window;
import config.practical.ConfigScroll;
import config.practical.utilities.Constants;
import config.practical.widgets.ConfigBool;
import config.practical.widgets.ConfigString;
import config.practical.widgets.sliders.ConfigInt;
import config.practical.widgets.sound.ConfigSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NotificationEditScreen extends Screen {

    private final Screen parent;
    private final ConfigScroll scroll;
    private final Notification notification;


    protected NotificationEditScreen(Notification notification) {
        super(Component.empty());

        Minecraft client = Minecraft.getInstance();
        parent = client.screen;
        Window window = client.getWindow();
        scroll = new ConfigScroll(0,  30, window.getGuiScaledWidth(), window.getGuiScaledHeight(), Constants.WIDGET_WIDTH);
        this.notification = notification;
    }

    private void initScrollWidgets() {
        scroll.children().clear();
        scroll.add(new ConfigBool(Component.literal("Enabled"), notification::isEnabled, notification::setEnabled));
        scroll.add(new ConfigString(Component.literal("Trigger on message"), notification::getMatchString, notification::setMatchString, false));
        scroll.add(new ConfigString(Component.literal("Notification message"), notification::getNotificationString, notification::setNotificationString));
        scroll.add(new ConfigBool(Component.literal("Use regex"), notification::useRegex, notification::setUseRegex));
        scroll.add(new ConfigInt(Component.literal("Duration in ticks"), notification::getTicks, notification::setTicks, 1, 10, 40));
        scroll.add(new ConfigSound(Component.literal("Sound"), notification.getSound(), 2, 2, true));
        scroll.add(new ConfigBool(Component.literal("Send command"), notification::sendCommand, notification::setSendCommand));
        scroll.add(new ConfigString(Component.literal("Command"), notification::getCommand, notification::setCommand));
        scroll.update();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(scroll);
        initScrollWidgets();
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }
}
