package com.zzj.xiaomiwidgettest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public boolean checkStraightLine(int[][] coordinates) {
        for(int i = 2; i < coordinates.length; i++){
            if ((coordinates[i][1]-coordinates[0][1])*(coordinates[i-1][0]-coordinates[0][0]) != (coordinates[i][0]-coordinates[0][0])*(coordinates[i-1][1]-coordinates[0][1])){
                return false;
            }
        }
        return true;
    }
}