/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 *
 * @author Thinh
 */

public final class ButtonGroupHandlingSupporter {
    private ButtonGroupHandlingSupporter() {}
    
    public static AbstractButton getSelectedButton(ButtonGroup buttonGroup) {
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while(buttons.hasMoreElements()) {
            AbstractButton currentButton = buttons.nextElement();
            if(currentButton.isSelected()) {
               return currentButton;
            }
        }
        
        return null;
    }
    
    public static String getSelectedButtonText(ButtonGroup buttonGroup) {
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while(buttons.hasMoreElements()) {
            AbstractButton currentButton = buttons.nextElement();
            if(currentButton.isSelected()) {
               return currentButton.getText();
            }
        }
        
        return "";
    }
    
    public static void setSelectedButton(ButtonGroup buttonGroup, String toCheck) {
        buttonGroup.clearSelection();
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while(buttons.hasMoreElements()) {
            AbstractButton currentButton = buttons.nextElement();
            if(currentButton.getText().equals(toCheck)) {
               currentButton.setSelected(true);
               break;
            }
        }
    }
}
