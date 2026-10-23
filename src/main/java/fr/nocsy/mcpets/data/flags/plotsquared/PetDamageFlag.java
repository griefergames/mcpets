package fr.nocsy.mcpets.data.flags.plotsquared;


import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.jspecify.annotations.NonNull;

public class PetDamageFlag extends BooleanFlag<PetDamageFlag> {

    public static final PetDamageFlag PET_DAMAGE_TRUE = new PetDamageFlag(true);
    public static final PetDamageFlag PET_DAMAGE_FALSE = new PetDamageFlag(false);

    private PetDamageFlag(boolean value) {
        super(value, TranslatableCaption.of("flags.flag_description_pet_damage"));
    }

    @Override
    protected PetDamageFlag flagOf(@NonNull Boolean value) {
        return value ? PET_DAMAGE_TRUE : PET_DAMAGE_FALSE;
    }

}
