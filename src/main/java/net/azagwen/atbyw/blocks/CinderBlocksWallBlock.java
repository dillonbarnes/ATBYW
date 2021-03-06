package net.azagwen.atbyw.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.WallShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Iterator;
import java.util.Map;

public class CinderBlocksWallBlock extends WallBlock {
    private static final VoxelShape POST_SHAPE;
    private static final VoxelShape POST_SLAB_SHAPE;
    private static final VoxelShape SLAB_NORTH_SHAPE;
    private static final VoxelShape SLAB_SOUTH_SHAPE;
    private static final VoxelShape SLAB_WEST_SHAPE;
    private static final VoxelShape SLAB_EAST_SHAPE;
    private static final VoxelShape LOW_NORTH_SHAPE;
    private static final VoxelShape LOW_SOUTH_SHAPE;
    private static final VoxelShape LOW_WEST_SHAPE;
    private static final VoxelShape LOW_EAST_SHAPE;
    private static final VoxelShape TALL_NORTH_SHAPE;
    private static final VoxelShape TALL_SOUTH_SHAPE;
    private static final VoxelShape TALL_WEST_SHAPE;
    private static final VoxelShape TALL_EAST_SHAPE;
    private static final VoxelShape POST_COLLISION_SHAPE;
    private static final VoxelShape LOW_NORTH_COLLISION_SHAPE;
    private static final VoxelShape LOW_SOUTH_COLLISION_SHAPE;
    private static final VoxelShape LOW_WEST_COLLISION_SHAPE;
    private static final VoxelShape LOW_EAST_COLLISION_SHAPE;
    private static final VoxelShape TALL_NORTH_COLLISION_SHAPE;
    private static final VoxelShape TALL_SOUTH_COLLISION_SHAPE;
    private static final VoxelShape TALL_WEST_COLLISION_SHAPE;
    private static final VoxelShape TALL_EAST_COLLISION_SHAPE;
    private final Map<BlockState, VoxelShape> collisionShapeMap;
    private final Map<BlockState, VoxelShape> lowPostShapeMap;
    private final Map<BlockState, VoxelShape> tallPostShapeMap;

    public CinderBlocksWallBlock(Settings settings) {
        super(settings);
        this.lowPostShapeMap = this.getShapeMap(
                POST_SHAPE,
                new VoxelShape[] {LOW_NORTH_SHAPE, TALL_NORTH_SHAPE},
                new VoxelShape[] {LOW_EAST_SHAPE,  TALL_EAST_SHAPE },
                new VoxelShape[] {LOW_SOUTH_SHAPE, TALL_SOUTH_SHAPE},
                new VoxelShape[] {LOW_WEST_SHAPE,  TALL_WEST_SHAPE }
        );
        this.tallPostShapeMap = this.getShapeMap(
                VoxelShapes.union(POST_SHAPE, POST_SLAB_SHAPE),
                new VoxelShape[] {LOW_NORTH_SHAPE, TALL_NORTH_SHAPE},
                new VoxelShape[] {LOW_EAST_SHAPE,  TALL_EAST_SHAPE },
                new VoxelShape[] {LOW_SOUTH_SHAPE, TALL_SOUTH_SHAPE},
                new VoxelShape[] {LOW_WEST_SHAPE,  TALL_WEST_SHAPE }
        );
        this.collisionShapeMap = this.getShapeMap(
                POST_COLLISION_SHAPE,
                new VoxelShape[] {LOW_NORTH_COLLISION_SHAPE, TALL_NORTH_COLLISION_SHAPE},
                new VoxelShape[] {LOW_EAST_COLLISION_SHAPE,  TALL_EAST_COLLISION_SHAPE },
                new VoxelShape[] {LOW_SOUTH_COLLISION_SHAPE, TALL_SOUTH_COLLISION_SHAPE},
                new VoxelShape[] {LOW_WEST_COLLISION_SHAPE,  TALL_WEST_COLLISION_SHAPE }
        );
    }

    private static VoxelShape isWallTall(VoxelShape postShape, WallShape wallShape, VoxelShape lowSideShape, VoxelShape tallSideShape) {
        if (wallShape == WallShape.TALL) {
            return VoxelShapes.union(postShape, tallSideShape);
        } else {
            return wallShape == WallShape.LOW ? VoxelShapes.union(postShape, lowSideShape) : postShape;
        }
    }

    private Map<BlockState, VoxelShape> getShapeMap(VoxelShape post, VoxelShape[] north, VoxelShape[] east, VoxelShape[] south, VoxelShape[] west) {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (Boolean isUp : UP.getValues()) {
            for (WallShape eastShape : EAST_SHAPE.getValues()) {
                for (WallShape northShape : NORTH_SHAPE.getValues()) {
                    for (WallShape westShape : WEST_SHAPE.getValues()) {
                        for (WallShape southShape : SOUTH_SHAPE.getValues()) {
                            VoxelShape SHAPE_NO_POST = VoxelShapes.empty();
                            SHAPE_NO_POST = isWallTall(SHAPE_NO_POST, eastShape, east[0], east[1]);
                            SHAPE_NO_POST = isWallTall(SHAPE_NO_POST, westShape, west[0], west[1]);
                            SHAPE_NO_POST = isWallTall(SHAPE_NO_POST, northShape, north[0], north[1]);
                            SHAPE_NO_POST = isWallTall(SHAPE_NO_POST, southShape, south[0], south[1]);
                            if (isUp) {
                                SHAPE_NO_POST = VoxelShapes.union(SHAPE_NO_POST, post);
                            }

                            BlockState blockState = this.getDefaultState().with(UP, isUp).with(EAST_SHAPE, eastShape).with(WEST_SHAPE, westShape).with(NORTH_SHAPE, northShape).with(SOUTH_SHAPE, southShape);
                            builder.put(blockState.with(WATERLOGGED, false), SHAPE_NO_POST);
                            builder.put(blockState.with(WATERLOGGED, true), SHAPE_NO_POST);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean isCovered = !world.getBlockState(pos.up()).isAir();

        return isCovered ? lowPostShapeMap.get(state) : tallPostShapeMap.get(state);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collisionShapeMap.get(state);
    }

    static {
        Direction.Axis x =  Direction.Axis.X;
        Direction.Axis y =  Direction.Axis.Y;
        Direction.Axis z =  Direction.Axis.Z;

        double[] postValues =      {4.0D, 0.0D , 12.0D, 16.0D};
        double[] slabValuesN =     {4.0D, 12.0D, 0.0D , 12.0D, 14.0D, 8.0D };
        double[] slabValuesP =     {4.0D, 12.0D, 8.0D , 12.0D, 14.0D, 16.0D};
        double[] lowSideValuesN =  {5.0D, 0.0D , 0.0D , 11.0D, 12.0D, 8.0D };
        double[] lowSideValuesP =  {5.0D, 0.0D , 8.0D , 11.0D, 12.0D, 16.0D};
        double[] tallSideValuesN = {5.0D, 0.0D , 0.0D , 11.0D, 16.0D, 8.0D };
        double[] tallSideValuesP = {5.0D, 0.0D , 8.0D , 11.0D, 16.0D, 16.0D};

        POST_SHAPE =       Block.createCuboidShape(postValues[0], postValues[1], postValues[0], postValues[2], postValues[3], postValues[2]);
        POST_SLAB_SHAPE =  Block.createCuboidShape(3.0D, 16.0D, 3.0D , 13.0D, 18.0D, 13.0D);

        SLAB_NORTH_SHAPE = Block.createCuboidShape(slabValuesN[0], slabValuesN[1], slabValuesN[2], slabValuesN[3], slabValuesN[4], slabValuesN[5]);
        SLAB_SOUTH_SHAPE = Block.createCuboidShape(slabValuesP[0], slabValuesP[1], slabValuesP[2], slabValuesP[3], slabValuesP[4], slabValuesP[5]);
        SLAB_WEST_SHAPE =  Block.createCuboidShape(slabValuesN[2], slabValuesN[1], slabValuesN[0], slabValuesN[5], slabValuesN[4], slabValuesN[3]);
        SLAB_EAST_SHAPE =  Block.createCuboidShape(slabValuesP[2], slabValuesP[1], slabValuesP[0], slabValuesP[5], slabValuesP[4], slabValuesP[3]);
        LOW_NORTH_SHAPE = VoxelShapes.union(SLAB_NORTH_SHAPE, Block.createCuboidShape(lowSideValuesN[0], lowSideValuesN[1], lowSideValuesN[2], lowSideValuesN[3], lowSideValuesN[4], lowSideValuesN[5]));
        LOW_SOUTH_SHAPE = VoxelShapes.union(SLAB_SOUTH_SHAPE, Block.createCuboidShape(lowSideValuesP[0], lowSideValuesP[1], lowSideValuesP[2], lowSideValuesP[3], lowSideValuesP[4], lowSideValuesP[5]));
        LOW_WEST_SHAPE =  VoxelShapes.union(SLAB_WEST_SHAPE , Block.createCuboidShape(lowSideValuesN[2], lowSideValuesN[1], lowSideValuesN[0], lowSideValuesN[5], lowSideValuesN[4], lowSideValuesN[3]));
        LOW_EAST_SHAPE =  VoxelShapes.union(SLAB_EAST_SHAPE , Block.createCuboidShape(lowSideValuesP[2], lowSideValuesP[1], lowSideValuesP[0], lowSideValuesP[5], lowSideValuesP[4], lowSideValuesP[3]));
        TALL_NORTH_SHAPE = Block.createCuboidShape(tallSideValuesN[0], tallSideValuesN[1], tallSideValuesN[2], tallSideValuesN[3], tallSideValuesN[4], tallSideValuesN[5]);
        TALL_SOUTH_SHAPE = Block.createCuboidShape(tallSideValuesP[0], tallSideValuesP[1], tallSideValuesP[2], tallSideValuesP[3], tallSideValuesP[4], tallSideValuesP[5]);
        TALL_WEST_SHAPE =  Block.createCuboidShape(tallSideValuesN[2], tallSideValuesN[1], tallSideValuesN[0], tallSideValuesN[5], tallSideValuesN[4], tallSideValuesN[3]);
        TALL_EAST_SHAPE =  Block.createCuboidShape(tallSideValuesP[2], tallSideValuesP[1], tallSideValuesP[0], tallSideValuesP[5], tallSideValuesP[4], tallSideValuesP[3]);

        POST_COLLISION_SHAPE =       Block.createCuboidShape(postValues[0], postValues[1], postValues[0], postValues[2], 24.0D, postValues[2]);
        LOW_NORTH_COLLISION_SHAPE =  Block.createCuboidShape(lowSideValuesN[0] , lowSideValuesN[1] , lowSideValuesN[2] , lowSideValuesN[3] , 24.0D, lowSideValuesN[5] );
        LOW_SOUTH_COLLISION_SHAPE =  Block.createCuboidShape(lowSideValuesP[0] , lowSideValuesP[1] , lowSideValuesP[2] , lowSideValuesP[3] , 24.0D, lowSideValuesP[5] );
        LOW_WEST_COLLISION_SHAPE =   Block.createCuboidShape(lowSideValuesN[2] , lowSideValuesN[1] , lowSideValuesN[0] , lowSideValuesN[5] , 24.0D, lowSideValuesN[3] );
        LOW_EAST_COLLISION_SHAPE =   Block.createCuboidShape(lowSideValuesP[2] , lowSideValuesP[1] , lowSideValuesP[0] , lowSideValuesP[5] , 24.0D, lowSideValuesP[3] );
        TALL_NORTH_COLLISION_SHAPE = Block.createCuboidShape(tallSideValuesN[0], tallSideValuesN[1], tallSideValuesN[2], tallSideValuesN[3], 24.0D, tallSideValuesN[5]);
        TALL_SOUTH_COLLISION_SHAPE = Block.createCuboidShape(tallSideValuesP[0], tallSideValuesP[1], tallSideValuesP[2], tallSideValuesP[3], 24.0D, tallSideValuesP[5]);
        TALL_WEST_COLLISION_SHAPE =  Block.createCuboidShape(tallSideValuesN[2], tallSideValuesN[1], tallSideValuesN[0], tallSideValuesN[5], 24.0D, tallSideValuesN[3]);
        TALL_EAST_COLLISION_SHAPE =  Block.createCuboidShape(tallSideValuesP[2], tallSideValuesP[1], tallSideValuesP[0], tallSideValuesP[5], 24.0D, tallSideValuesP[3]);

    }
}
