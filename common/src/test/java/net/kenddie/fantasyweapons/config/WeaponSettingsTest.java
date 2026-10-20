package net.kenddie.fantasyweapons.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeaponSettingsTest {
    @Test
    public void acceptsAttributeBounds() {
        new WeaponSettings(WeaponSettings.MIN_DAMAGE, WeaponSettings.MIN_SPEED);
        new WeaponSettings(WeaponSettings.MAX_DAMAGE, WeaponSettings.MAX_SPEED);
    }

    @Test
    public void rejectsNonFiniteAttributes() {
        for (double value : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(value, -2.4));
            assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(9.0, value));
        }
    }

    @Test
    public void rejectsOutOfRangeAttributes() {
        assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(-1.0, -2.4));
        assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(2049.0, -2.4));
        assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(9.0, -4.1));
        assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(9.0, 1025.0));
    }
}
