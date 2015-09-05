package org.compass.core.accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.compass.core.CompassException;
import org.compass.core.util.reflection.ReflectionField;

public class ArrayValueGetter implements Getter {

	private static final long serialVersionUID = 3257848800692155955L;

	private final transient ReflectionField field;

	private final Class clazz;

	private final String name;

	ArrayValueGetter(ReflectionField field, Class clazz, String name) {
		this.field = field;
		this.clazz = clazz;
		this.name = name;
	}

	public Object get(Object target) throws CompassException {
		try {
			return field.get(target);
		} catch (Exception e) {
			throw new PropertyAccessException(e,
					"could not get a field value by reflection", false, clazz,
					name);
		}
	}

	public String getName() {
		return name;
	}

	public Class getReturnType() {
		return field.getType();
	}

	public Type getGenericReturnType() {
		return field.getGenericType();
	}

	public Field getField() {
		return this.field.getField();
	}

	public String toString() {
		return "DirectGetter(" + clazz.getName() + '.' + name + ')';
	}
}