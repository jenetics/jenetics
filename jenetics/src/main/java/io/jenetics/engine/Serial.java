package io.jenetics.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
final class Serial implements Externalizable {

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
	public Serial() {
	}

	/**
	 * Creates an instance for serialization.
	 *
	 * @param type  the type
	 * @param object  the object
	 */
	Serial(final byte type, final Object object) {
		_type = type;
		_object = object;
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeByte(_type);
		switch (_type) {
			case EVOLUTION_DURATIONS: ((EvolutionDurations)_object).write(out); break;
			case EVOLUTION_INIT: ((EvolutionInit)_object).write(out); break;
			case EVOLUTION_PARAMS: ((EvolutionParams)_object).write(out); break;
			case EVOLUTION_RESULT: ((EvolutionResult)_object).write(out); break;
			case EVOLUTION_START: ((EvolutionStart)_object).write(out); break;
			default:
				throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	@Override
	public void readExternal(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		_type = in.readByte();
		switch (_type) {
			case EVOLUTION_DURATIONS: _object = EvolutionDurations.read(in); break;
			case EVOLUTION_INIT: _object = EvolutionInit.read(in); break;
			case EVOLUTION_PARAMS: _object = EvolutionParams.read(in); break;
			case EVOLUTION_RESULT: _object = EvolutionResult.read(in); break;
			case EVOLUTION_START: _object = EvolutionStart.read(in); break;
			default:
				throw new StreamCorruptedException("Unknown serialized type.");
		}
	}

	private Object readResolve() {
		return _object;
	}

}
