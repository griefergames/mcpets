package fr.nocsy.mcpets.data.flags.plotsquared;


import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.jspecify.annotations.NonNull;

public class AllowPetsFlag extends BooleanFlag<AllowPetsFlag> {

    public static final AllowPetsFlag ALLOW_PETS_TRUE = new AllowPetsFlag(true);
    public static final AllowPetsFlag ALLOW_PETS_FALSE = new AllowPetsFlag(false);

    private AllowPetsFlag(boolean value) {
        super(value, TranslatableCaption.of("flags.flag_description_allow_pets"));
    }

    @Override
    protected AllowPetsFlag flagOf(@NonNull Boolean value) {
        return value ? ALLOW_PETS_TRUE : ALLOW_PETS_FALSE;
    }
}
