package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import blade.addon.utils.times.PersonalBests;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PracticeSS {

    private static final Box NORTH_BOX = new Box(5 / 16.0, 6 / 16.0, 14 / 16.0, 11 / 16.0, 10 / 16.0, 1);
    private static final Box SOUTH_BOX = new Box(5 / 16.0, 6 / 16.0, 0, 11 / 16.0, 10 / 16.0, 2 / 16.0);
    private static final Box EAST_BOX = new Box(0, 6 / 16.0, 5 / 16.0, 2 / 16.0, 10 / 16.0, 11 / 16.0);
    private static final Box WEST_BOX = new Box(14 / 16.0, 6 / 16.0, 5 / 16.0, 1, 10 / 16.0, 11 / 16.0);

    private static final List<BlockPos> START_POSES = List.of(new BlockPos(-17, 5, -26), new BlockPos(-37, 5, -26), new BlockPos(-51, 5, -26), new BlockPos(-74, 5, -26), new BlockPos(7, 5, -26), new BlockPos(-60, 5, -26), new BlockPos(-6, 5, -26), new BlockPos(-28, 5, -26));
    private static final CopyOnWriteArrayList<BlockPos> buttons = new CopyOnWriteArrayList<>();

    private static BlockPos startPos = null;
    private static Direction direction = null;

    private static final long SKIP_TIME_MS = 500;
    private static final int LAST_SHOWN_DURATION = 9;
    private static final int UNLUCKY_DURATION = 36;
    private static final int START_WAIT_DURATION = 6;

    private static boolean started = false;
    private static boolean showingPattern = false;

    private static int currentIndex = 0;
    private static int endIndex = 4;

    private static int ticksLeft = 0;
    private static int totalTicks = 0;

    private static long startTime = 0;

    private static boolean inSkipPhase = false;
    private static int clicks = 0;
    private static long skippedTime = 0;
    private static boolean skipped = false;

    private static boolean wasLuckyButton = false;
    private static int ticksSinceLuckyButton = 0;

    public static void init() {

        Events.ON_BLOCK_INTERACTION.register((BlockHitResult result, ItemStack item) -> {
            if (!ExtraOptions.practiceSSAnywhere && !Location.in(Location.PRIVATE_ISLAND)) return false;
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world == null) return false;
            BlockPos pos = result.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!isValidBlock(block)) return false;

            if (isStartButton(pos)) {
                parseStartButton(world, state, pos);
            }

            if (started && !showingPattern) {
                return parseNormalButton(pos);
            }

            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ticksSinceLuckyButton--;
            if (showingPattern) {
                ticksLeft--;
                if (ticksLeft <= 0) {
                    showingPattern = false;
                }
            }

            if (System.currentTimeMillis() - startTime > SKIP_TIME_MS && inSkipPhase) {
                inSkipPhase = false;

                if (skipped) {
                    Misc.addChatMessage(Text.literal("Skip successful!"));
                    genBoard(client.world, startPos);
                    setStage(2);
                } else {
                    Misc.addChatMessage(Text.literal("Failed to skip"));
                    skipped = false;
                }
            }
        });

        RenderingEvents.FILLED_BLOCK.register(PracticeSS::render);
        Events.ON_LOCATION_CHANGE.register(location -> {
            reset();
            return false;
        });

    }

    private static void parseStartButton(ClientWorld world, BlockState state, BlockPos pos) {
        if (started && ExtraOptions.autoSkip) {
            start(world, state, pos);
            startPos = pos;
            Misc.sendSound(ExtraOptions.ssSound);
        } else if (inSkipPhase || System.currentTimeMillis() - skippedTime < SKIP_TIME_MS) {
            clicks++;
            if (clicks >= 2 && inSkipPhase) {
                skipped = true;
                skippedTime = System.currentTimeMillis();
            }

            if (clicks < 3) {
                Misc.sendSound(ExtraOptions.ssSound);
            }

        } else {
            Misc.sendSound(ExtraOptions.ssSound);
            start(world, state, pos);
            startPos = pos;
        }
    }

    private static boolean parseNormalButton(BlockPos pos) {
        if (currentIndex == 1 && ticksSinceLuckyButton > 0) {
            return ExtraOptions.blockUnluckyButtonClick;
        }

        BlockPos nextButton = buttons.get(currentIndex);
        if (samePosition(pos, nextButton)) {
            currentIndex++;
            Misc.sendSound(ExtraOptions.ssSound);


            if (currentIndex == 5) {
                recordTime();
            } else if (currentIndex == endIndex) {
                incStage();
            }
        }

        return false;
    }

    private static void start(ClientWorld world, BlockState state, BlockPos pos) {
        reset();
        direction = state.get(Properties.HORIZONTAL_FACING);
        genBoard(world, pos);
        startTime = System.currentTimeMillis();

        if (ExtraOptions.autoSkip) {
            setStage(2);
        } else {
            setStage(1);
            inSkipPhase = true;
        }

        started = true;
    }

    private static void genBoard(ClientWorld world, BlockPos pos) {
        if (world == null || pos == null) {
            Misc.addChatMessage(Text.literal("World or start position cant be found"));
            return;
        }

        buttons.clear();

        int sx, ex, sz, ez;
        switch (direction) {
            case SOUTH -> {
                sx = 1;
                ex = 5;
                sz = 0;
                ez = 1;
            }
            case NORTH -> {
                sx = -4;
                ex = 0;
                sz = 0;
                ez = 1;
            }
            case EAST -> {
                sx = 0;
                ex = 1;
                sz = -4;
                ez = 0;
            }
            case WEST -> {
                sx = 0;
                ex = 1;
                sz = 1;
                ez = 5;
            }
            default -> {
                Misc.addChatMessage(Text.literal("Failed to generate board"));
                return;
            }
        }


        for (int x = pos.getX() + sx; x < pos.getX() + ex; x++) {
            for (int y = pos.getY() - 1; y < pos.getY() + 3; y++) {
                for (int z = pos.getZ() + sz; z < pos.getZ() + ez; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockState currentState = world.getBlockState(currentPos);
                    Block block = currentState.getBlock();

                    if (isValidBlock(block)) {
                        buttons.add(currentPos);

                    } else {
                        started = false;
                        Misc.addChatMessage(Text.literal("Block at: " + x + ", " + y + ", " + z + " is not a stone button"));
                        return;
                    }
                }
            }
        }

        Collections.shuffle(buttons);
    }

    private static boolean isValidBlock(Block block) {
        return block == Blocks.STONE_BUTTON;
    }

    private static boolean isStartButton(BlockPos pos) {
        if (samePosition(pos, ExtraOptions.startButton)) return true;
        for (BlockPos startPos : START_POSES) {
            if (samePosition(pos, startPos)) return true;
        }

        return false;
    }

    private static boolean samePosition(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null) return false;
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
    }

    private static void recordTime() {
        if (ExtraOptions.realisticDelay) {
            if (ExtraOptions.includeLuckyButton) {
                if (wasLuckyButton) {
                    PersonalBests.practiseSSRealisticTime.testNewTime(Text.literal("SS with Realistic Time (Lucky Button) Took: "), startTime);
                } else {
                    PersonalBests.practiseSSRealisticUnluckyTime.testNewTime(Text.literal("SS with Realistic Time Took: "), startTime);
                }
            } else {
                PersonalBests.practiseSSRealisticTime.testNewTime(Text.literal("SS with Realistic Time Took: "), startTime);
            }

        } else {
            if (ExtraOptions.includeLuckyButton) {
                if (wasLuckyButton) {
                    PersonalBests.practiseSSTime.testNewTime(Text.literal("SS (Lucky Button) Took: "), startTime);
                } else {
                    PersonalBests.practiseSSUnluckyTime.testNewTime(Text.literal("SS Time Took: "), startTime);
                }
            } else {
                PersonalBests.practiseSSTime.testNewTime(Text.literal("SS Took: "), startTime);
            }

        }

        reset();
    }

    private static void reset() {
        started = false;
        showingPattern = false;
        skipped = false;
        buttons.clear();
        clicks = 0;
        ticksSinceLuckyButton = 0;
        wasLuckyButton = false;
        direction = null;
        totalTicks = 0;
    }

    private static int getColor(int index) {
        if (index == 1 && ticksSinceLuckyButton > 0) return ExtraOptions.luckyButtonColor;
        if (index == currentIndex) return Constants.GREEN_COLOR;
        if (index == currentIndex + 1) return Constants.ORANGE_COLOR;
        return Constants.RED_COLOR;
    }

    private static void setStage(int stage) {
        if (stage == 2 && ExtraOptions.includeLuckyButton && skipped) {
            double rng = Math.random();
            if (rng > ExtraOptions.luckyButtonRng) {
                wasLuckyButton = false;
                ticksSinceLuckyButton = UNLUCKY_DURATION;
            } else {
                wasLuckyButton = true;
            }
        }

        endIndex = stage;
        currentIndex = 0;
        totalTicks = getDelay() * endIndex;
        if (ExtraOptions.realisticDelay) {
            totalTicks += LAST_SHOWN_DURATION;
            if (stage != 2) {
                totalTicks += START_WAIT_DURATION;
            }
        }
        ticksLeft = totalTicks;
        showingPattern = true;
    }

    public static int getDelay() {
        return ExtraOptions.realisticDelay ? 8 : 10;
    }

    private static void incStage() {
        setStage(endIndex + 1);
    }

    private static int getLastDisplayIndex() {
        if (ExtraOptions.realisticDelay) {
            return endIndex - Math.max((ticksLeft - (endIndex != 2 ? LAST_SHOWN_DURATION : 4)), 0) / getDelay();
        }
        return endIndex - ticksLeft / getDelay();
    }

    private static void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!started) return;

        if (showingPattern) {
            if (!ExtraOptions.realisticDelay || totalTicks - ticksLeft > START_WAIT_DURATION || endIndex != 2) {
                int lastIndex = getLastDisplayIndex();
                renderButtons(matrixStack, consumer, 0, lastIndex);
                renderBackground(matrixStack, consumer, lastIndex - 1);
            }
        } else {
            renderButtons(matrixStack, consumer, currentIndex, endIndex);
        }
    }

    private static void renderButtons(MatrixStack matrixStack, VertexConsumer consumer, int start, int end) {
        Box directionBox = getBox(direction);
        if (directionBox == null) return;

        for (int i = start; i < end; i++) {
            if (buttons.size() <= i) break;
            BlockPos pos = buttons.get(i);
            Box box = directionBox.offset(pos.getX(), pos.getY(), pos.getZ());
            float[] color = RenderUtils.toFloats(getColor(i));
            RenderUtils.renderFilled(matrixStack, consumer, box, color);
        }
    }

    private static Box getBox(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH_BOX;
            case SOUTH -> SOUTH_BOX;
            case WEST -> WEST_BOX;
            case EAST -> EAST_BOX;
            default -> null;
        };
    }

    private static void renderBackground(MatrixStack matrixStack, VertexConsumer consumer, int backgroundIndex) {
        if (buttons.size() > backgroundIndex && backgroundIndex >= 0) {
            Box box = Box.of(buttons.get(backgroundIndex).toCenterPos(), 1, 1, 1);
            int dx, dz;
            switch (direction) {
                case SOUTH -> {
                    dx = 0;
                    dz = -1;
                }
                case NORTH -> {
                    dx = 0;
                    dz = 1;
                }
                case EAST -> {
                    dx = -1;
                    dz = 0;
                }
                case WEST -> {
                    dx = 1;
                    dz = 0;
                }
                default -> {
                    return;
                }
            }
            RenderUtils.renderFilled(matrixStack, consumer, box.offset(dx, 0, dz), new float[]{0, 0.5f, 1, 1});
        }
    }
}
