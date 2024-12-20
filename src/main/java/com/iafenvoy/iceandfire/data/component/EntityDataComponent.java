package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.IceAndFire;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class EntityDataComponent implements ComponentV3, AutoSyncedComponent, CommonTickingComponent {
    protected static final ComponentKey<EntityDataComponent> COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(IceAndFire.MOD_ID, "entity_data"), EntityDataComponent.class);

    public final FrozenData frozenData = new FrozenData();
    public final ChainData chainData = new ChainData();
    public final SirenData sirenData = new SirenData();
    public final ChickenData chickenData = new ChickenData();
    public final MiscData miscData = new MiscData();
    private final LivingEntity entity;

    public EntityDataComponent(LivingEntity entity) {
        this.entity = entity;
    }

    public static EntityDataComponent get(LivingEntity entity) {
        return COMPONENT.get(entity);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        this.frozenData.deserialize(tag);
        this.chainData.deserialize(tag);
        this.sirenData.deserialize(tag);
        this.chickenData.deserialize(tag);
        this.miscData.deserialize(tag);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        this.frozenData.serialize(tag);
        this.chainData.serialize(tag);
        this.sirenData.serialize(tag);
        this.chickenData.serialize(tag);
        this.miscData.serialize(tag);
    }

    @Override
    public void tick() {
        this.frozenData.tickFrozen(this.entity);
        this.chainData.tickChain(this.entity);
        this.sirenData.tickCharmed(this.entity);
        this.chickenData.tickChicken(this.entity);
        this.miscData.tickMisc(this.entity);
        boolean needUpdate = this.frozenData.doesClientNeedUpdate() || this.chainData.doesClientNeedUpdate() || this.sirenData.doesClientNeedUpdate() || this.miscData.doesClientNeedUpdate();
        if (needUpdate)
            COMPONENT.sync(this.entity);
    }
}
