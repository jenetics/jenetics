package io.jenetics.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
final class SerialProxy implements Externalizable {

	@Serial
	private static final long serialVersionUID = 1;

	static final byte EVOLUTION_DURATIONS = 1;
	static final byte EVOLUTION_INIT = 2;
	static final byte EVOLUTION_PARAMS = 3;
	static final byte EVOLUTION_RESULT = 4;
	static final byte EVOLUTION_START = 5;

	/**
	 * The type being serialized.
	 */
	private byte _type;

	/**
	 * The object being serialized.
	 */
	private Object _object;

	/**
	 * Constructor for deserialization.
	 */
	public SerialProxy() {
	}

	/**
	 * Creates an instance for serialization.
	 *
	 * @param type  the type
	 * @param object  the object
	 */
	SerialProxy(final byte type, final Object object) {
		_type = type;
		_object = object;
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeByte(_type);
		switch (_type) {
			case EVOLUTION_DURATIONS -> ((EvolutionDurations)_object).write(out);
			case EVOLUTION_INIT -> ((EvolutionInit<?>)_object).write(out);
			case EVOLUTION_PARAMS -> ((EvolutionParams<?, ?>)_object).write(out);
			case EVOLUTION_RESULT -> ((EvolutionResult<?, ?>)_object).write(out);
			case EVOLUTION_START -> ((EvolutionStart<?, ?>)_object).write(out);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		_type = in.readByte();
		_object = switch (_type) {
			case EVOLUTION_DURATIONS -> EvolutionDurations.read(in);
			case EVOLUTION_INIT -> EvolutionInit.read(in);
			case EVOLUTION_PARAMS -> EvolutionParams.read(in);
			case EVOLUTION_RESULT -> EvolutionResult.read(in);
			case EVOLUTION_START -> EvolutionStart.read(in);
			default -> throw new StreamCorruptedException("Unknown serialized type.");
		};
	}

	@Serial
	private Object readResolve() {
		return _object;
	}

}
