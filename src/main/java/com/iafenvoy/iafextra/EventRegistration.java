package com.iafenvoy.iafextra;

import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.iafenvoy.iafextra.event.AttackEntityEvent;
import com.iafenvoy.iafextra.event.EventBus;
import com.iafenvoy.iafextra.event.ProjectileImpactEvent;

public class EventRegistration {
    public static void register(){
        EventBus.register(ProjectileImpactEvent.class, ServerEvents::onArrowCollide);
        EventBus.register(AttackEntityEvent.class,ServerEvents::onPlayerAttackMob);
    }
}
