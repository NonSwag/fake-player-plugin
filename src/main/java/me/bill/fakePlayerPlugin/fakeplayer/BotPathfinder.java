package me.bill.fakePlayerPlugin.fakeplayer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.TrapDoor;

public final class BotPathfinder {

  private BotPathfinder() {}

  public record Pos(int x, int y, int z) {}

  public enum MoveType {
    WALK,
    ASCEND,
    DESCEND,
    PARKOUR,
    BREAK,
    PLACE,
    PILLAR,
    SWIM,
    OPEN,
    CLIMB
  }

  public record Move(int x, int y, int z, MoveType type) {
    public Pos toPos() {
      return new Pos(x, y, z);
    }
  }

  public record PathOptions(
      boolean parkour, boolean breakBlocks, boolean placeBlocks, boolean avoidWater, boolean avoidLava) {
    public PathOptions(boolean parkour, boolean breakBlocks, boolean placeBlocks) {
      this(parkour, breakBlocks, placeBlocks, false, false);
    }

    public static final PathOptions DEFAULT = new PathOptions(false, false, false, false, false);

    public boolean anyEnabled() {
      return parkour || breakBlocks || placeBlocks;
    }
  }

  public static boolean canPassThrough(World world, int x, int y, int z) {
    if (y < world.getMinHeight() || y > world.getMaxHeight()) return true;
    try {
      if (!world.isChunkLoaded(x >> 4, z >> 4)) return false;
      Block block = world.getBlockAt(x, y, z);
      Material mat = block.getType();

      if (mat.isAir() || mat == Material.WATER) return true;
      if (mat == Material.LAVA) return false;
      if (block.getBlockData() instanceof Fence) return false;
      if (mat.name().contains("WALL") && !mat.name().contains("WALL_")) return false;
      if (mat.name().contains("_WALL")
          || mat == Material.COBBLESTONE_WALL
          || mat == Material.MOSSY_COBBLESTONE_WALL) return false;
      if (block.getBlockData() instanceof Door door) return door.isOpen();
      if (block.getBlockData() instanceof Gate gate) return gate.isOpen();
      if (block.getBlockData() instanceof TrapDoor trapDoor) return trapDoor.isOpen();
      if (block.getBlockData() instanceof Slab slab) return slab.getType() == Slab.Type.BOTTOM;
      if (mat == Material.COBWEB) return false;
      if (isClimbable(mat)) return true;
      return block.isPassable();
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean canStandOn(World world, int x, int y, int z) {
    if (y < world.getMinHeight() || y > world.getMaxHeight()) return false;
    try {
      if (!world.isChunkLoaded(x >> 4, z >> 4)) return false;
      Block block = world.getBlockAt(x, y, z);
      Material mat = block.getType();

      if (mat.isAir()) return false;
      if (mat.isSolid() && mat.isOccluding()) return true;
      if (block.getBlockData() instanceof Slab) return true;
      if (mat.name().contains("STAIRS")) return true;
      if (block.getBlockData() instanceof Fence) return false;
      if (mat.name().contains("WALL")) return false;
      if (mat == Material.GLASS
          || mat.name().contains("STAINED_GLASS") && !mat.name().contains("PANE")) return true;
      if (mat == Material.CHEST
          || mat == Material.TRAPPED_CHEST
          || mat == Material.ENDER_CHEST
          || mat == Material.BARREL) return true;
      if (mat.name().contains("LEAVES")) return true;
      if (mat == Material.FARMLAND || mat == Material.DIRT_PATH || mat == Material.SOUL_SAND) return true;
      if (mat == Material.HONEY_BLOCK) return true;
      if (mat.name().contains("_BED")) return true;
      if (mat == Material.SCAFFOLDING) return true;
      if (isClimbable(mat)) return true;
      if (block.getBlockData() instanceof TrapDoor trapDoor) {
        return !trapDoor.isOpen() && trapDoor.getHalf() == org.bukkit.block.data.Bisected.Half.TOP;
      }
      if (mat == Material.WATER) return false;
      if (mat == Material.MAGMA_BLOCK) return true;
      return !block.isPassable();
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean walkable(World world, int x, int y, int z) {
    if (!inBounds(world, y) || !inBounds(world, y + 1)) return false;
    return canStandOn(world, x, y - 1, z)
        && canPassThrough(world, x, y, z)
        && canPassThrough(world, x, y + 1, z);
  }

  public static boolean passable(World world, int x, int y, int z) {
    return canPassThrough(world, x, y, z);
  }

  private static boolean isClimbable(Material mat) {
    return mat == Material.LADDER
        || mat == Material.VINE
        || mat == Material.SCAFFOLDING
        || mat.name().endsWith("_VINES")
        || mat.name().endsWith("_VINE");
  }

  private static boolean inBounds(World world, int y) {
    return y > world.getMinHeight() && y < world.getMaxHeight() - 1;
  }
}
