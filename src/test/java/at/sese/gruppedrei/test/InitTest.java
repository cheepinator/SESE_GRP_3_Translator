package at.sese.gruppedrei.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class InitTest {

    @Test
    public void initTest(){
        assertEquals("first test true", 1, 1);
    }

    @Test
    public void failTest(){
        assertEquals("first test falses", 1, 2);
    }
}
