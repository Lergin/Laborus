package de.lergin.laborus.api;

import com.google.common.reflect.TypeToken;
import de.lergin.laborus.Laborus;

import java.util.HashMap;
import java.util.Map;

/**
 * The service to register JobBoni, JobActions and JobAbilities. Is provided by Sponges ServiceManager after the
 * GameConstruction:
 * Sponge.getServiceManager().getRegistration(JobService.class).get().getProvider();
 */
public class JobService {
    private Map<Object, TypeToken<? extends JobBonus>> jobBoni = new HashMap<>();
    private Map<Object, TypeToken<? extends JobAction>> jobAction = new HashMap<>();
    private Map<Object, TypeToken<? extends JobAbility>> jobAbilities = new HashMap<>();

    private Laborus plugin = Laborus.instance();

    /**
     * registers a new JobBonus
     * @param bonusClass the class of the bonus
     * @param configurationName the name used in the configuration for this bonus
     */
    public void registerJobBonus(Class<? extends JobBonus> bonusClass, String configurationName){
        registerJobBonus(bonusClass, configurationName, "");
    }

    /**
     * registers a new JobBonus
     * @param bonusClass the class of the bonus
     * @param configurationName the name used in the configuration for the bonus
     * @param configurationComment currently ignored
     */
    public void registerJobBonus(Class<? extends JobBonus> bonusClass, String configurationName, String configurationComment){
        jobBoni.put(configurationName, TypeToken.of(bonusClass));

        plugin.getLogger().info("Registered JobBonus {}", bonusClass.getCanonicalName());
    }

    /**
     * @return a map of all the configuration keys of the JobBoni and the TypeTokens assigned to them
     */
    public Map<Object, TypeToken<? extends JobBonus>> getJobBoni(){
        return jobBoni;
    }

    public void registerJobAction(Class<? extends JobAction> actionClass, String configurationName){
        registerJobAction(actionClass, configurationName, "");
    }

    public void registerJobAction(Class<? extends JobAction> actionClass, String configurationName, String configurationComment){
        jobAction.put(configurationName, TypeToken.of(actionClass));

        plugin.getLogger().info("Registered JobAction {}", actionClass.getCanonicalName());
    }

    public Map<Object, TypeToken<? extends JobAction>> getJobAction(){
        return jobAction;
    }

    public void registerJobAbility(Class<? extends JobAbility> abilityClass, String configurationName){
        registerJobAbility(abilityClass, configurationName, "");
    }

    public void registerJobAbility(Class<? extends JobAbility> abilityClass, String configurationName, String configurationComment){
        jobAbilities.put(configurationName, TypeToken.of(abilityClass));

        plugin.getLogger().info("Registered JobAbility {}", abilityClass.getCanonicalName());
    }

    public Map<Object, TypeToken<? extends JobAbility>> getJobAilities(){
        return jobAbilities;
    }
}
