package de.lergin.laborus.api;

import de.lergin.laborus.Laborus;
import de.lergin.laborus.data.JobKeys;
import de.lergin.laborus.data.jobs.JobDataManipulatorBuilder;
import de.lergin.laborus.config.TranslationKeys;
import de.lergin.laborus.job.Job;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * a ability for a job that can be activated via the /jobs ability command.
 *
 * it needs to be registered with the JobService:
 * JobService jobService = Sponge.getServiceManager().getRegistration(JobService.class).get().getProvider();
 * jobService.registerJobAbility(Class.class, "nameForTheConfig");
 *
 * the only other method that needs to be implemented is activateAbility that should start the ability, the sending of
 * messages and the handling of the cooldown is done automatically
 *
 * The best way to implement the Serializable Interface is by using the @ConfigSerializable Annotation and the using of
 * the @Setting Annotation for additional settings. If this is used the settings for the cooldown and name are
 * automatically added.
 */
public abstract class JobAbility implements Serializable {
    @Setting(value = "cooldown")
    private int coolDown = 0;
    @Setting(value = "name")
    private String name = "";

    private Laborus plugin = Laborus.instance();

    public JobAbility() {}

    public abstract void activateAbility(Job job, Player player);

    public boolean startAbility(Job job, Player player){
        if (!canStartAbility(job, player)) {
            sendCoolDownNotEndedMessage(job, player);
            return false;
        }

        activateAbility(job, player);

        startCoolDown(job, player);
        sendStartMessage(job, player);

        plugin.config.base.loggingConfig.jobAbilities(job, "Started Ability ({})", this.getName());

        return true;
    }

    public void sendStartMessage(Job job, Player player) {
        player.sendMessage(
                plugin.translationHelper.get(
                        TranslationKeys.JOB_ABILITY_START,
                        player,
                        job.getId()
                ),
                this.textArgs(job, player)
        );
    }

    public void sendCoolDownNotEndedMessage(Job job, Player player) {
        player.sendMessage(
                plugin.translationHelper.get(
                        TranslationKeys.JOB_ABILITY_CANNOT_START_COOLDOWN,
                        player,
                        job.getId()
                ),
                this.textArgs(job, player)
        );
    }

    public boolean canStartAbility(Job job, Player player) {
        Map<String, Long> abilityUsed = player.get(JobKeys.JOB_ABILITY_USED).orElseGet(HashMap::new);

        boolean canStartAbility =
                (abilityUsed.getOrDefault(job.getId(), 0L) + coolDown) < Instant.now().getEpochSecond();

        if(!canStartAbility){
            plugin.config.base.loggingConfig.jobAbilities(job, "Cannot start (cooldown)");
        }

        return canStartAbility;
    }

    public void startCoolDown(Job job, Player player) {
        Map<String, Long> abilityUsed = player.get(JobKeys.JOB_ABILITY_USED).orElseGet(HashMap::new);
        Map<String, Long> tempMap = new HashMap<>();
        tempMap.putAll(abilityUsed);
        tempMap.put(job.getId(), Instant.now().getEpochSecond());

        if(!player.offer(JobKeys.JOB_ABILITY_USED, tempMap).isSuccessful()){
            player.offer(new JobDataManipulatorBuilder().abilityUsed(tempMap).create());

            plugin.config.base.loggingConfig.jobAbilities(job, "cooldown started");
        }else{
            plugin.config.base.loggingConfig
                    .jobAbilities(job, "cooldown couldn't be started (data offer not successful)");
        }
    }

    public int getCoolDown() {
        return coolDown;
    }

    public long getSecondsTillEndOfCoolDown(Job job, Player player) {
        Map<String, Long> abilityUsed = player.get(JobKeys.JOB_ABILITY_USED).orElseGet(HashMap::new);

        return abilityUsed.getOrDefault(job.getId(), (long) coolDown) + coolDown - Instant.now().getEpochSecond();
    }

    public String getName() {
        return name;
    }

    public Map<String, TextElement> textArgs(Job job, Player player){
        Map<String, TextElement> args = job.textArgs(player);

        args.put("ability.name", Text.of(this.getName()));
        args.put("ability.remaining_cooldown", Text.of(this.getSecondsTillEndOfCoolDown(job, player)));

        return args;
    }
}
