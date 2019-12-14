package bgu.spl.mics;
import org.json.simple.*;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;
    @BeforeEach
    public void setUp(){
        inventory = Inventory.getInstance();
    }

    @Test
    public void testLoad(){
        String[] gadgets = {"a", "b", "c"};
        inventory.load(gadgets);
        assertTrue(inventory.getItem("a"));
        assertTrue(inventory.getItem("b"));
        assertFalse(inventory.getItem("d"));
        assertTrue(inventory.getItem("c"));
        assertFalse(inventory.getItem("b"));
    }

    @Test
    public void testGetItem(){
        String[] gadgets = {"a", "b", "c"};
        inventory.load(gadgets);
        assertTrue(inventory.getItem("a"));
        assertFalse(inventory.getItem("a"));
        assertTrue(inventory.getItem("b"));
        assertFalse(inventory.getItem("d"));
        assertTrue(inventory.getItem("c"));
    }

    @Test
    public void testGetInstance(){
        Inventory i2 = Inventory.getInstance();
        assertTrue(i2 == inventory);
    }

    @Test
    public void testPrintToFile(){

    }
}
