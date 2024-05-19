package net.jukitsumc.jukmod.client.config.impl.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.LongSliderEntry;
import me.shedaniel.clothconfig2.impl.builders.LongSliderBuilder;
import net.jukitsumc.jukmod.client.config.impl.category.Category;
import net.jukitsumc.jukmod.config.option.LongSliderOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class LongSliderOptionImpl extends OptionImpl<Long, LongSliderEntry> implements LongSliderOption {
    public LongSliderOptionImpl(String id, Category category, Long defaultValue, long from, long to) {
        super(id, category, defaultValue);
        this.setConfigEntry(() -> {
            LongSliderBuilder builder = ConfigEntryBuilder.create()
                    .startLongSlider(Component.translatable(this.getTranslationKey()),
                            this.get(),
                            from,
                            to)
                    .setTextGetter(value -> {
                        if (value == 0) {
                            return MutableComponent.create(
                                    new TranslatableContents(this.getTranslationKey() + ".off", null, new Object[0]));
                        }
                        return MutableComponent.create(new PlainTextContents.LiteralContents(new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.US))
                                .format(value)));
                    }).setSaveConsumer(this::set)
                    .setDefaultValue(defaultValue);
            builder.setTooltip(this.getTooltip(this.getTranslationKey()));
            return builder.build();
        });
    }
}
