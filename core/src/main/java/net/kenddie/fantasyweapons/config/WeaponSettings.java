package net.kenddie.fantasyweapons.config;

public record WeaponSettings(double damage, double speed) {
    public static final WeaponSettings DEFAULT = new WeaponSettings(9.0, -2.4);
    public static final double MIN_DAMAGE = 0.0;
    public static final double MAX_DAMAGE = 2048.0;
    public static final double MIN_SPEED = -4.0;
    public static final double MAX_SPEED = 1024.0;

    public WeaponSettings {
        if (!validDamage(damage) || !validSpeed(speed)) {
            throw new IllegalArgumentException("Invalid weapon attributes: " + damage + ", " + speed);
        }
    }

    public static boolean validDamage(double value) {
        return Double.isFinite(value) && value >= MIN_DAMAGE && value <= MAX_DAMAGE;
    }

    public static boolean validSpeed(double value) {
        return Double.isFinite(value) && value >= MIN_SPEED && value <= MAX_SPEED;
    }
}
