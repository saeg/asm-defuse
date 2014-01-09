package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.Set;

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

	@Test
	public void VariablesListIsUnmodifiable() {
		final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
		final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", v1);
		assertSuperClasss("UnmodifiableCollection", ofield.getVariables());
	}

	@Test
	public void VariableListContainsVariablesFromReferenceValue() {
		final Variable local = new Local(Type.INT_TYPE, 0);
		final Value ref = new Value(Type.getObjectType("pkg/Owner")) {
			@Override
			public Set<Variable> getVariables() {
				return Collections.singleton(local);
			}
		};
		final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", ref);
		Assert.assertTrue(ofield.getVariables().contains(local));
	}

	@Test
	public void VariableListContainsSelfWhenRootIsALocal() {
		final Variable local = new Local(Type.getObjectType("pkg/Ref"), 0);
		final ObjectField ofield = new ObjectField("pkg/Ref", "name", "I", local);
		Assert.assertTrue(ofield.getVariables().contains(ofield));
	}

	@Test
	public void VariableListContainsSelfWhenRootIsAStaticField() {
		final Variable sfield = new StaticField("Owner", "name", "Lpkg/Ref;");
		final ObjectField ofield = new ObjectField("pkg/Ref", "name", "I", sfield);
		Assert.assertTrue(ofield.getVariables().contains(ofield));
	}

	@Test
	public void VariableListTest() {
		final Variable local = new Local(Type.getObjectType("Ref1"), 0);
		final ObjectField ref1 = new ObjectField("Ref1", "name", "LRef2;", local);
		final ObjectField ref2 = new ObjectField("Ref2", "name", "I", ref1);
		final Set<Variable> variables = ref2.getVariables();
		Assert.assertTrue(variables.contains(local));
		Assert.assertTrue(variables.contains(ref1));
		Assert.assertTrue(variables.contains(ref2));
	}

	private static void assertSuperClasss(final String name, final Object o) {
		Class<?> clazz = o.getClass();
		while (!Object.class.equals(clazz)) {
			if (clazz.getSimpleName().equals(name))
				return;
			clazz = clazz.getSuperclass();
		}
		Assert.fail();
	}

}
