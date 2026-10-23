package net.griefergames.mcpets.active;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.events.PetDespawnEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Thin accessors so Kotlin can call Lombok-generated methods on upstream MCPets types
 * (Kotlin compiles before Java annotation processing in this module).
 */
public final class PetAccess {

    private PetAccess() {
    }

    public static String id(Pet pet) {
        return pet.getId();
    }

    public static String permission(Pet pet) {
        return pet.getPermission();
    }

    public static UUID owner(Pet pet) {
        return pet.getOwner();
    }

    public static void setOwner(Pet pet, UUID owner) {
        pet.setOwner(owner);
    }

    public static void setCheckPermission(Pet pet, boolean checkPermission) {
        pet.setCheckPermission(checkPermission);
    }

    public static Map<UUID, List<Pet>> activePets() {
        return Pet.getActivePets();
    }

    public static String skinPathId(PetSkin skin) {
        return skin.getPathId();
    }

    public static String skinPermission(PetSkin skin) {
        return skin.getPermission();
    }

    public static Pet despawnPet(PetDespawnEvent event) {
        return event.getPet();
    }

    public static PetDespawnReason despawnReason(PetDespawnEvent event) {
        return event.getReason();
    }
}
