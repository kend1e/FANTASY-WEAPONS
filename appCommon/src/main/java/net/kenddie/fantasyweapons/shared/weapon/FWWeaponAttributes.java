package net.kenddie.fantasyweapons.shared.weapon;

/**
 * Represents the attributes of a weapon.
 * Similar to FAArmorAttributes in Fantasy Armor.
 */
public record FWWeaponAttributes(
        float damage,
        float speed,
        float reach,
        int durability
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float damage = 9.0f;
        private float speed = -2.4f;
        private float reach = 0.0f;
        private int durability = 0;

        public Builder damage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder reach(float reach) {
            this.reach = reach;
            return this;
        }

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public FWWeaponAttributes build() {
            return new FWWeaponAttributes(
                    damage,
                    speed,
                    reach,
                    durability
            );
        }
    }
}
