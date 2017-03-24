package de.lergin.sponge.laborus.listener;

import de.lergin.sponge.laborus.api.JobAction;
import de.lergin.sponge.laborus.job.Job;
import de.lergin.sponge.laborus.job.items.EntityJobItem;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.List;

/**
 * listener for entity kill jobEvents
 */
@ConfigSerializable
public class EntityKillListener extends JobAction<EntityJobItem> {
    public EntityKillListener() {}

    @Setting(value = "items")
    private List<EntityJobItem> jobItems;

    @Override
    public List<EntityJobItem> getJobItems() {
        return jobItems;
    }

    @Override
    public String getId() {
        return "ENTITY_KILL";
    }

    @Listener
    public void onEvent(DestructEntityEvent.Death event, @First Player player) throws Exception {
        super.onEvent(event, player,
                () -> true,
                () -> EntityJobItem.fromEntity(event.getTargetEntity()));
    }
}
