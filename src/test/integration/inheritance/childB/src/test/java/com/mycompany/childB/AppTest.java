package com.mycompany.childB;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    @Test
    public void testAppSumMethod() {
        assertEquals(7, App.sum(5, 2));
    }
}
