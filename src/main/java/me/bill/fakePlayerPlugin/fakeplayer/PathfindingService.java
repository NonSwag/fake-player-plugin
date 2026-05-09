package me.bill.fakePlayerPlugin.fakeplayer;

import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PathfindingService {

  public enum Owner {
    MOVE,
    MINE,
    PLACE,
    USE,
    ATTACK,
    FOLLOW,
    SLEEP,
    SYSTEM
  }

  public record NavigationRequest(
      @NotNull Owner owner,
      @NotNull Supplier<@Nullable Location> destinationSupplier,
      double arrivalDistance,
      double recalcDistance,
      int maxNullPathRecalculations,
      @Nullable Runnable onArrive,
      @Nullable Runnable onCancel,
      @Nullable Runnable onPathFailure,
      @Nullable Location lockOnArrival,
      @Nullable BotPathfinder.PathOptions overrideOpts) {

    public NavigationRequest(
        @NotNull Owner owner,
        @NotNull Supplier<@Nullable Location> destinationSupplier,
        double arrivalDistance,
        double recalcDistance,
        int maxNullPathRecalculations,
        @Nullable Runnable onArrive,
        @Nullable Runnable onCancel,
        @Nullable Runnable onPathFailure,
        @Nullable Location lockOnArrival) {
      this(
          owner,
          destinationSupplier,
          arrivalDistance,
          recalcDistance,
          maxNullPathRecalculations,
          onArrive,
          onCancel,
          onPathFailure,
          lockOnArrival,
          null);
    }

    public NavigationRequest(
        @NotNull Owner owner,
        @NotNull Supplier<@Nullable Location> destinationSupplier,
        double arrivalDistance,
        double recalcDistance,
        int maxNullPathRecalculations,
        @Nullable Runnable onArrive,
        @Nullable Runnable onCancel,
        @Nullable Runnable onPathFailure) {
      this(
          owner,
          destinationSupplier,
          arrivalDistance,
          recalcDistance,
          maxNullPathRecalculations,
          onArrive,
          onCancel,
          onPathFailure,
          null,
          null);
    }

    public NavigationRequest {
      if (owner == null) throw new IllegalArgumentException("owner");
      if (destinationSupplier == null) throw new IllegalArgumentException("destinationSupplier");
      if (arrivalDistance <= 0) throw new IllegalArgumentException("arrivalDistance must be > 0");
      if (recalcDistance < 0) throw new IllegalArgumentException("recalcDistance must be >= 0");
      if (maxNullPathRecalculations <= 0) maxNullPathRecalculations = Integer.MAX_VALUE;
    }
  }

  public interface Controller {
    boolean isNavigating(@NotNull UUID botUuid);

    boolean isNavigating(@NotNull UUID botUuid, @NotNull Owner owner);

    @Nullable Owner getOwner(@NotNull UUID botUuid);

    void cancel(@NotNull UUID botUuid);

    void cancelAll();

    void cancelAll(@NotNull Owner owner);

    void navigate(@NotNull FakePlayer fp, @NotNull NavigationRequest request);
  }

  private volatile Controller controller;

  public void setController(@Nullable Controller controller) {
    if (this.controller != null) this.controller.cancelAll();
    this.controller = controller;
  }

  public boolean hasController() {
    return controller != null;
  }

  public boolean isNavigating(@NotNull UUID botUuid) {
    Controller c = controller;
    return c != null && c.isNavigating(botUuid);
  }

  public boolean isNavigating(@NotNull UUID botUuid, @NotNull Owner owner) {
    Controller c = controller;
    return c != null && c.isNavigating(botUuid, owner);
  }

  public @Nullable Owner getOwner(@NotNull UUID botUuid) {
    Controller c = controller;
    return c != null ? c.getOwner(botUuid) : null;
  }

  public void cancel(@NotNull UUID botUuid) {
    Controller c = controller;
    if (c != null) c.cancel(botUuid);
  }

  public void cancelAll() {
    Controller c = controller;
    if (c != null) c.cancelAll();
  }

  public void cancelAll(@NotNull Owner owner) {
    Controller c = controller;
    if (c != null) c.cancelAll(owner);
  }

  public void navigate(@NotNull FakePlayer fp, @NotNull NavigationRequest request) {
    Controller c = controller;
    if (c != null) {
      c.navigate(fp, request);
      return;
    }
    if (request.onPathFailure() != null) request.onPathFailure().run();
  }

  public static BotPathfinder.PathOptions resolvePathOptions(@NotNull FakePlayer fp) {
    return resolvePathOptions(fp, null);
  }

  public static BotPathfinder.PathOptions resolvePathOptions(
      @NotNull FakePlayer fp, @Nullable BotPathfinder.PathOptions overrideOpts) {
    if (overrideOpts != null) return overrideOpts;
    return new BotPathfinder.PathOptions(
        fp.isNavParkour(),
        fp.isNavBreakBlocks(),
        fp.isNavPlaceBlocks(),
        fp.isNavAvoidWater(),
        fp.isNavAvoidLava());
  }

  public static double xzDist(@NotNull Location a, @NotNull Location b) {
    return xzDistRaw(a.getX(), a.getZ(), b.getX(), b.getZ());
  }

  public static double xzDistRaw(double ax, double az, double bx, double bz) {
    double dx = ax - bx;
    double dz = az - bz;
    return Math.sqrt(dx * dx + dz * dz);
  }

  public static void tickSwimAi(Player bot, boolean navJump, boolean isNavigating) {
    try {
      boolean inFluid = bot.isInWater() || bot.isInLava();
      if (inFluid) {
        NmsPlayerSpawner.setJumping(bot, true);
        if (!isNavigating) bot.setSprinting(false);
      } else {
        NmsPlayerSpawner.setJumping(bot, isNavigating && navJump);
      }
    } catch (NullPointerException e) {
      String msg = e.getMessage();
      if (msg != null && msg.contains("getCurrentWorldData()")) {
        NmsPlayerSpawner.setJumping(bot, bot.isInWater() || bot.isInLava());
        return;
      }
      throw e;
    }
  }
}
