/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import java.util.Arrays;

/**
 *
 * @author Thinh
 */

public final class StringHandlingSupporter {
    private StringHandlingSupporter() {}
    
    public static String combineStrings(String[] strings, String delim) {
        if(strings == null || delim == null)
            throw new IllegalArgumentException();
        
        return (strings.length == 0) ? null : Arrays.stream(strings).skip(1).reduce(strings[0], (s1, s2) -> s1 + delim + " " + s2);
    }
    
    public static String[] separateString(String string, String delim) {
        if(string == null || delim == null)
            throw new IllegalArgumentException();
        
        return Arrays.stream(string.split(delim)).map(String::trim).toArray(String[]::new);
    }
}
