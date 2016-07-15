package org.jenetics.example.tsp.gpx;

import static java.lang.String.format;

import java.io.Serializable;

/**
 * Represents the GPS speed value in m/s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Speed implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double MPS_TO_KMH_FACTOR = 3.6;

    private final double _value;

    /**
     * Create a new GPS {@code Speed} object in m/s.
     *
     * @param value the GPS speed value in m/s.
     */
    private Speed(final double value) {
        _value = value;
    }

    /**
     * Return the GPS speed value in m/s.
     *
     * @return the GPS speed value in m/s
     */
    public double getValue() {
        return _value;
    }

    /**
     * Return the GPS speed value in km/h.
     *
     * @return the GPS speed value in km/h
     */
    public double toKmH() {
        return _value*MPS_TO_KMH_FACTOR;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(_value);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Speed &&
            Double.compare(((Speed)obj)._value, _value) == 0;
    }

    @Override
    public String toString() {
        return format("%s m/s", _value);
    }


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

    /**
     * Create a new GPS {@code Speed} object in m/s.
     *
     * @param meterPerSecond the GPS speed value in m/s.
     */
    public static Speed of(final double meterPerSecond) {
        return new Speed(meterPerSecond);
    }

    /**
     * Create a new GPS {@code Speed} object in km/h.
     *
     * @param kilometerPerHour the GPS speed value in m/s.
     */
    public static Speed ofKmH(final double kilometerPerHour) {
        return new Speed(kilometerPerHour/MPS_TO_KMH_FACTOR);
    }

}
