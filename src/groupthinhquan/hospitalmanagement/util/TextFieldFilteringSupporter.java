/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import groupthinhquan.hospitalmanagement.gui.MainFrame;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Thinh
 */

public final class TextFieldFilteringSupporter {
    public static final String STANDARD_INTEGER_REGEX = "^[+-]?\\d+$";
    public static final String COMMON_INTEGER_REGEX = "^-?\\d+$";
    public static final String POSITIVE_INTEGER_REGEX = "^\\d+$";
    public static final String STANDARD_FLOAT_REGEX = "^[+-]?\\d*\\.?\\d+([eE][-+]?[0-9]+)?$" + "|"
                                                    + "^[+-]?\\d+\\.?\\d*([eE][-+]?[0-9]+)?$";
    public static final String SHORT_FLOAT_REGEX = "^[+-]?\\d*\\.?\\d+$" + "|" + "^[+-]?\\d+\\.?\\d*$";
    public static final String COMMON_FLOAT_REGEX = "^-?\\d*\\.?\\d+$" + "|" + "^-?\\d+\\.?\\d*$";
    public static final String POSITIVE_FLOAT_REGEX = "^\\d*\\.?\\d+$" + "|" + "^\\d+\\.?\\d*$";
    
    private TextFieldFilteringSupporter() {}
    
    private static int periodCheck(String text) {
        // If there is one period only, it's fine
        // Otherwise, stop at the second period
        int periodCount = 0;
        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == '.') {
                periodCount++;
                if(periodCount >= 2) {
                    break;
                }
            }
        }
        
        return periodCount;
    }
    
    public static double round(double value, int places) {
        if (places < 0) 
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        
        return bd.doubleValue();
    }
    
    public static String round(String value, int places) {
        if(places < 0) 
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        
        return bd.stripTrailingZeros().toPlainString();
    }
    
    public static String reformat(String value) {
        BigDecimal bd = new BigDecimal(value);
        
        return bd.stripTrailingZeros().toPlainString();
    }
    
    public static void setPositiveIntegerFilter(JTextField textField, int maxChar, int maxValue) {
        if (maxChar < 0 || maxValue < 0) 
            throw new IllegalArgumentException();
        
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();
                
                if(text.isEmpty() || (text.length() <= maxChar && text.matches(POSITIVE_INTEGER_REGEX) 
                    && Integer.parseInt(text) <= maxValue)) {
                    super.insertString(fb, offset, string, attr);
                }
                else {
                    MainFrame.makeBeepSound();
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                    return;
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();

                if(text.isEmpty() || (text.length() <= maxChar && text.matches(POSITIVE_INTEGER_REGEX) 
                    && Integer.parseInt(text) <= maxValue)){
                    super.replace(fb, offset, length, string, attr);
                }
                else {
                    MainFrame.makeBeepSound();
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(reformat(currentText)); 
                }
            }
        });
    }
    
    public static void setPositiveIntegerFilter(JTextField textField, int maxChar) {
        if(maxChar < 0) 
            throw new IllegalArgumentException();
                
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();

                if(text.isEmpty() || (text.length() <= maxChar && text.matches(POSITIVE_INTEGER_REGEX))) {
                    super.insertString(fb, offset, string, attr);
                }
                else {
                    MainFrame.makeBeepSound();
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                    return;
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();

                if(text.isEmpty() || (text.length() <= maxChar && text.matches(POSITIVE_INTEGER_REGEX))) {
                    super.replace(fb, offset, length, string, attr);
                }
                else {
                    MainFrame.makeBeepSound();
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(reformat(currentText)); 
                }
            }
        });
    }
    
    public static void setPositiveIntegerFilter(JTextField textField) {
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();
                
                if(text.isEmpty() || text.matches(POSITIVE_INTEGER_REGEX)) {
                    super.insertString(fb, offset, string, attr);
                }
                else {
                    MainFrame.makeBeepSound();
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                    return;
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();
                
                if(text.isEmpty() || text.matches(POSITIVE_INTEGER_REGEX)) {
                    super.replace(fb, offset, length, string, attr);
                }
                else {
                    MainFrame.makeBeepSound();
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(reformat(currentText)); 
                }
            }
        });
    }

    public static void setPositiveFloatFilter(JTextField textField, double maxValue, int maximumFractionDigits) {
        if(maxValue < 0 || maximumFractionDigits < 0) 
            throw new IllegalArgumentException();
                
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();
                
                int periodCount = periodCheck(text);
                
                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.insertString(fb, offset, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.insertString(fb, offset, string, attr); 
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                    return;
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);

                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();

                int periodCount = periodCheck(text);

                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.replace(fb, offset, length, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.replace(fb, offset, length, string, attr);  
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(round(currentText, maximumFractionDigits)); 
                }
            }
        });
    }
    
    public static void setPositiveFloatFilter(JTextField textField, double maxValue) {
        if(maxValue < 0) 
            throw new IllegalArgumentException();
        
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();
                
                int periodCount = periodCheck(text);
                
                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.insertString(fb, offset, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.insertString(fb, offset, string, attr); 
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                    return;
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);

                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();

                int periodCount = periodCheck(text);

                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.replace(fb, offset, length, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || (text.matches(POSITIVE_FLOAT_REGEX) && Double.parseDouble(text) <= maxValue)) {
                        super.replace(fb, offset, length, string, attr);  
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(reformat(currentText)); 
                }
            }
        });
    }
    
    public static void setPositiveFloatFilter(JTextField textField, int maximumFractionDigits) {
        if(maximumFractionDigits < 0) 
            throw new IllegalArgumentException();
        
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();
                
                int periodCount = periodCheck(text);
                
                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.insertString(fb, offset, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.insertString(fb, offset, string, attr); 
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);

                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();

                int periodCount = periodCheck(text);

                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.replace(fb, offset, length, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.replace(fb, offset, length, string, attr);  
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(round(currentText, maximumFractionDigits)); 
                }
            }
        });
    }
    
    public static void setPositiveFloatFilter(JTextField textField) {
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                                    throws BadLocationException {
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);
                
                StringBuilder builder = new StringBuilder(text);
                text = builder.insert(offset, string).toString();
                
                int periodCount = periodCheck(text);
                
                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.insertString(fb, offset, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.insertString(fb, offset, string, attr); 
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                                    throws BadLocationException {
                if(string == null) {
                    super.replace(fb, offset, length, string, attr);
                }
                
                int textLen = fb.getDocument().getLength();
                String text = fb.getDocument().getText(0, textLen);

                StringBuilder builder = new StringBuilder(text);
                builder.delete(offset, offset + length);
                text = builder.insert(offset, string).toString();

                int periodCount = periodCheck(text);

                switch (periodCount) {
                case 0:
                    if(text.length() == 0 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.replace(fb, offset, length, string, attr);
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                case 1:
                    if(text.length() == 1 || text.matches(POSITIVE_FLOAT_REGEX)) {
                        super.replace(fb, offset, length, string, attr);  
                    }
                    else {
                        MainFrame.makeBeepSound();
                    }
                    break;
                default:
                    MainFrame.makeBeepSound();
                    break;
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter(){ 
            @Override
            public void focusLost(FocusEvent evt) {
                String currentText = textField.getText();
                if(!currentText.isEmpty()) {
                   textField.setText(reformat(currentText)); 
                }
            }
        });
    }
}
