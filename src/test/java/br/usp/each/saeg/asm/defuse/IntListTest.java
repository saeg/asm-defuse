package br.usp.each.saeg.asm.defuse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntListTest {

    private IntList list;

    @Before
    public void setUp() {
        list = new IntList();
    }

    @Test
    public void testOneElement() {
        list.add(0);
        Assert.assertArrayEquals(new int[] { 0 }, list.toArray());
        Assert.assertArrayEquals(new int[] { 0 }, list.toReverseArray());
    }

    @Test
    public void testMoreThanOneElement() {
        list.add(0);
        list.add(1);
        Assert.assertArrayEquals(new int[] { 0, 1 }, list.toArray());
        Assert.assertArrayEquals(new int[] { 1, 0 }, list.toReverseArray());
    }

    @Test
    public void testEmpty() {
        Assert.assertArrayEquals(new int[0], list.toArray());
        Assert.assertArrayEquals(new int[0], list.toReverseArray());
    }

    @Test
    public void testClear() {
        list.add(0);
        list.add(1);
        list.clear();
        Assert.assertArrayEquals(new int[0], list.toArray());
        Assert.assertArrayEquals(new int[0], list.toReverseArray());
    }

}
