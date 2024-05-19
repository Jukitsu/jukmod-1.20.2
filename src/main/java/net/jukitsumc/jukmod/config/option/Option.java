package net.jukitsumc.jukmod.config.option;

import net.jukitsumc.jukmod.client.config.impl.category.Category;

public interface Option<T> {
    String getId();

    String getTranslationKey();

    Category getCategory();

    T get();
    void set(T value);

    T getDefault();
}
