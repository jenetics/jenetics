package io.jenetics.incubator.bean;

import java.beans.PropertyDescriptor;

record Prop(
	PropertyDescriptor descriptor,
	String path,
	Object parent,
    Object value
)
	implements Property
{

	@Override
	public Class<?> type() {
		return descriptor.getPropertyType();
	}

	@Override
	public String name() {
		return descriptor.getName();
	}

	@Override
	public Object read() {
		return Beans.readValue(descriptor, parent);
	}

	@Override
	public boolean write(final Object value) {
		return Beans.writeValue(descriptor, parent, value);
	}

}
