package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ObjectFieldTest {

	@Test
	public void HaveAReferenceValue() {
		final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", Value.REFERENCE_VALUE);
		Assert.assertNotNull(ofield.value);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenReferenceValueIsNotObject() {
		new ObjectField("pkg/Owner", "Name", "I", Value.INT_VALUE);
	}

	@Test
	public void ObjectFieldToString() {
		final Value v = new Value(Type.getObjectType("java/lang/Object")) {
			@Override
			public String toString() {
				return "value";
			};
		};
		final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", v);
		Assert.assertEquals("value.pkg.Owner.Name", ofield.toString());
	}

	@Test
	public void EqualsButDifferentReferenceValueReturnFalse() {
		final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
		final Value v2 = new Value(Type.getObjectType("java/lang/String"));
		final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
		final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
		Assert.assertFalse(ofield1.equals(ofield2));
	}

	@Test
	public void EqualsReturnTrue() {
		final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
		final Value v2 = new Value(Type.getObjectType("java/lang/Object"));
		final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
		final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
		Assert.assertTrue(ofield1.equals(ofield2));
	}

	@Test
	public void DifferentReturnFalse() {
		final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
		final Value v2 = new Value(Type.getObjectType("java/lang/Object2"));
		final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
		final ObjectField ofield2 = new ObjectField("pkg/Owner2", "Name2", "[I", v2);
		Assert.assertFalse(ofield1.equals(ofield2));
	}

	@Test
	public void EqualsButDifferentReferenceValueReturnOtherHash() {
		final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
		final Value v2 = new Value(Type.getObjectType("java/lang/String"));
		final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
		final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
		Assert.assertNotEquals(ofield1.hashCode(), ofield2.hashCode());
	}

	@Test
	public void EqualsReturnSameHash() {
		final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
		final Value v2 = new Value(Type.getObjectType("java/lang/Object"));
		final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
		final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
		Assert.assertEquals(ofield1.hashCode(), ofield2.hashCode());
	}

}
