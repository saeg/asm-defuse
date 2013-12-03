package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {

	private Field field;

	@Before
	public void setUp() {
		field = new Field("pkg/Owner", "Name", "[D");
	}

	@Test
	public void AttributesAreCorrect() {
		Assert.assertEquals("pkg/Owner", field.owner);
		Assert.assertEquals("Name", field.name);
		Assert.assertEquals("[D", field.desc);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenDescriptorIsInvalid() {
		new StaticField("Owner", "Name", "Desc");
	}

	@Test
	public void EqualsASelfReturnTrue() {
		Assert.assertTrue(field.equals(field));
	}

	@Test
	public void EqualsANullReturnsFalse() {
		Assert.assertFalse(field.equals(null));
	}

	@Test
	public void EqualsButDifferentClassReturnsFalse() {
		final Field other = new Field("pkg/Owner", "Name", "[D") {
			/* other class */
		};
		Assert.assertFalse(field.equals(other));
	}

	@Test
	public void EqualsButDifferentOwnerReturnFalse() {
		final Field other = new Field("pkg/Owner2", "Name", "[D");
		Assert.assertFalse(field.equals(other));
	}

	@Test
	public void EqualsButDifferentNameReturnFalse() {
		final Field other = new Field("pkg/Owner", "Name2", "[D");
		Assert.assertFalse(field.equals(other));
	}

	@Test
	public void EqualsButDifferentDescriptorReturnFalse() {
		final Field other = new Field("pkg/Owner", "Name", "I");
		Assert.assertFalse(field.equals(other));
	}

	@Test
	public void EqualsReturnTrue() {
		final Field other = new Field("pkg/Owner", "Name", "[D");
		Assert.assertTrue(field.equals(other));
	}

	@Test
	public void EqualsButDifferentOwnerReturnOtherHash() {
		final Field other = new Field("pkg/Owner2", "Name", "[D");
		Assert.assertNotEquals(field.hashCode(), other.hashCode());
	}

	@Test
	public void EqualsButDifferentNameReturnOtherHash() {
		final Field other = new Field("pkg/Owner", "Name2", "[D");
		Assert.assertNotEquals(field.hashCode(), other.hashCode());
	}

	@Test
	public void EqualsButDifferentDescriptorReturnOtherHash() {
		final Field other = new Field("pkg/Owner", "Name", "I");
		Assert.assertNotEquals(field.hashCode(), other.hashCode());
	}

	@Test
	public void EqualsReturnSameHash() {
		final Field other = new Field("pkg/Owner", "Name", "[D");
		Assert.assertEquals(field.hashCode(), other.hashCode());
	}

}
