package net.azagwen.atbyw.blocks;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import static net.azagwen.atbyw.init.AtbywMain.*;

public class AtbywTags {
    // Block Tags
    public static final Tag<Block> BOOKSHELVES = registerBlockTag("bookshelves");

    // Item Tags
    public static final Tag<Item> BOOKSHELVES_I = registerItemTag("bookshelves");
    public static final Tag<Item> STICKS = registerItemTag("sticks");

    private AtbywTags() {
    }

    private static Tag<Block> registerBlockTag(String id) {
        return TagRegistry.block(newID(id));
    }
    private static Tag<Item> registerItemTag(String id) {
        return TagRegistry.item(newID(id));
    }
}
