package com.iafenvoy.iafextra.event;

import net.minecraft.entity.LivingEntity;

public class LivingEvent extends Event {
    private LivingEntity entity;

    public LivingEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
