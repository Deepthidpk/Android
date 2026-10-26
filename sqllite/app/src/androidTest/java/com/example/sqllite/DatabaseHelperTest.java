package com.example.sqllite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

/** Runs on an emulator/device against a throw-away database file. */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private static final String TEST_DB = "names_test.db";

    private Context context;
    private DatabaseHelper db;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(TEST_DB);
        db = new DatabaseHelper(context, TEST_DB);
    }

    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(TEST_DB);
    }

    @Test
    public void emptyDatabaseReturnsEmptyList() {
        assertTrue(db.getAllNames().isEmpty());
    }

    @Test
    public void addedNamesAreReturnedInInsertionOrder() {
        assertTrue(db.addName("Athira") > 0);
        assertTrue(db.addName("Anu") > 0);
        assertTrue(db.addName("Rahul") > 0);
        assertEquals(Arrays.asList("Athira", "Anu", "Rahul"), db.getAllNames());
    }

    @Test
    public void duplicateIsRejectedCaseInsensitively() {
        assertTrue(db.addName("Anna") > 0);
        assertTrue(db.nameExists("anna"));
        assertEquals(-1, db.addName("ANNA"));
        assertEquals(1, db.getAllNames().size());
    }

    @Test
    public void nameExistsIsFalseForUnknownName() {
        assertFalse(db.nameExists("Nobody"));
    }

    @Test
    public void sqlInjectionTextIsStoredAsPlainData() {
        String nasty = "x'); DROP TABLE names;--";
        assertTrue(db.addName(nasty) > 0);
        assertEquals(1, db.getAllNames().size());
        assertEquals(nasty, db.getAllNames().get(0));
    }
}
