package com.github.alexthe666.citadel.server.block;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class LecternBooks {

    public static Map<Identifier, BookData> BOOKS = new HashMap<>();

    public static void init() {
        BOOKS.put(Citadel.CITADEL_BOOK.getId(), new BookData(0X64A27B, 0XD6D6D6));
    }

    public static boolean isLecternBook(ItemStack stack) {
        return BOOKS.containsKey(Registries.ITEM.getId(stack.getItem()));
    }

    public static class BookData {
        int bindingColor;
        int pageColor;

        public BookData(int bindingColor, int pageColor) {
            this.bindingColor = bindingColor;
            this.pageColor = pageColor;
        }

        public int getBindingColor() {
            return this.bindingColor;
        }

        public int getPageColor() {
            return this.pageColor;
        }
    }
}
