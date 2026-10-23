package fr.nocsy.mcpets.data.flags.plotsquared;

import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

public class FlagsManager {

    @Getter
    private static boolean registered;

    public static void init(MCPets instance) {
        registered = true;
    }

    public static void registerFlags() {
        if(!registered) return;

        GlobalFlagContainer.getInstance().addFlag(AllowPetsFlag.ALLOW_PETS_TRUE);
        GlobalFlagContainer.getInstance().addFlag(PetDamageFlag.PET_DAMAGE_FALSE);
    }

}
