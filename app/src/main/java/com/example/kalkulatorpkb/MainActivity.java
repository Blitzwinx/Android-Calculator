package com.example.kalkulatorpkb;

import androidx.appcompat.app.AppCompatActivity;
import org.mariuszgromada.math.mxparser.*;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        License.iConfirmNonCommercialUse("YourName");

        display = findViewById(R.id.textView);
        if (display != null) {
            display.setShowSoftInputOnFocus(false);
        }
    }

    /** number formatting */
    private String formatExpression(String input) {
        if (input.isEmpty()) return input;

        StringBuilder result = new StringBuilder();
        StringBuilder currentNumber = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else {
                if (currentNumber.length() > 0) {
                    result.append(addThousandSeparators(currentNumber.toString()));
                    currentNumber.setLength(0);
                }
                result.append(c);
            }
        }

        if (currentNumber.length() > 0) {
            result.append(addThousandSeparators(currentNumber.toString()));
        }

        return result.toString();
    }

    private String addThousandSeparators(String number) {
        if (number.isEmpty()) return number;

        String[] parts = number.split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? parts[1] : "";

        StringBuilder formatted = new StringBuilder();
        int digitCount = 0;

        for (int i = integerPart.length() - 1; i >= 0; i--) {
            if (digitCount == 3) {
                formatted.insert(0, ',');
                digitCount = 0;
            }
            formatted.insert(0, integerPart.charAt(i));
            digitCount++;
        }

        if (!decimalPart.isEmpty()) {
            formatted.append('.').append(decimalPart);
        } else if (number.endsWith(".")) {
            formatted.append('.');
        }

        return formatted.toString();
    }

    // remove comma for calculations process
    private String removeCommas(String input) {
        return input.replace(",", "");
    }

    // add back comma for display
    private String formatResult(double result) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        if (result == (long) result) {
            DecimalFormat df = new DecimalFormat("#,###", symbols);
            return df.format((long) result);
        } else {
            DecimalFormat df = new DecimalFormat("#,##0.##########", symbols);
            return df.format(result);
        }
    }

    /** cursor & text positioning */

    private String insertAt(String text, String insertion, int position) {
        return text.substring(0, position) + insertion + text.substring(position);
    }

    private int getActualCursorPosition(String text, int displayPosition) {
        int commasBeforeCursor = 0;
        for (int i = 0; i < displayPosition && i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                commasBeforeCursor++;
            }
        }
        return displayPosition - commasBeforeCursor;
    }

    private int getDisplayCursorPosition(String formattedText, int actualPosition) {
        int commasBeforeCursor = 0;
        for (int i = 0, j = 0; i < formattedText.length() && j < actualPosition; i++) {
            if (formattedText.charAt(i) == ',') {
                commasBeforeCursor++;
            } else {
                j++;
            }
        }
        return actualPosition + commasBeforeCursor;
    }

    private void insertCharacter(String character) {
        if (display == null) return;

        String currentText = display.getText().toString();
        String plainText = removeCommas(currentText);

        int cursorPos = display.getSelectionStart();
        int actualPos = getActualCursorPosition(currentText, cursorPos);

        String newText = insertAt(plainText, character, actualPos);
        String formattedText = formatExpression(newText);

        display.setText(formattedText);

        int newCursorPos = getDisplayCursorPosition(formattedText, actualPos + 1);
        display.setSelection(Math.min(newCursorPos, formattedText.length()));
    }

    /** calculations */

    public void btnEquals(View v) {
        String expression = display.getText().toString();

        expression = removeCommas(expression);
        expression = expression.replaceAll("÷", "/");
        expression = expression.replaceAll("×", "*");

        // Calculate using mxparser library
        Expression exp = new Expression(expression);
        double result = exp.calculate();

        String formattedResult = formatResult(result);
        display.setText(formattedResult);
        display.setSelection(formattedResult.length());
    }

    public void btnClear(View v) {
        display.setText("");
    }

    // delete number (before cursor)
    public void btnBackspace(View v) {
        String currentText = display.getText().toString();
        if (currentText.isEmpty()) return;

        String plainText = removeCommas(currentText);
        int cursorPos = display.getSelectionEnd();

        if (cursorPos == 0) return;

        int actualPos = getActualCursorPosition(currentText, cursorPos);

        if (actualPos > 0) {
            String newText = plainText.substring(0, actualPos - 1) +
                    plainText.substring(actualPos);
            String formattedText = formatExpression(newText);

            display.setText(formattedText);

            int newCursorPos = getDisplayCursorPosition(formattedText, actualPos - 1);
            display.setSelection(Math.min(newCursorPos, formattedText.length()));
        }
    }

    public void btnZero(View v) { insertCharacter("0"); }
    public void btnOne(View v) { insertCharacter("1"); }
    public void btnTwo(View v) { insertCharacter("2"); }
    public void btnThree(View v) { insertCharacter("3"); }
    public void btnFour(View v) { insertCharacter("4"); }
    public void btnFive(View v) { insertCharacter("5"); }
    public void btnSix(View v) { insertCharacter("6"); }
    public void btnSeven(View v) { insertCharacter("7"); }
    public void btnEight(View v) { insertCharacter("8"); }
    public void btnNine(View v) { insertCharacter("9"); }
    
    public void btnPlus(View v) { insertCharacter("+"); }
    public void btnMinus(View v) { insertCharacter("-"); }
    public void btnMultiply(View v) { insertCharacter("×"); }
    public void btnDivide(View v) { insertCharacter("÷"); }
    public void btnDecimal(View v) { insertCharacter("."); }
    public void btnLeftParentheses(View v) { insertCharacter("("); }
    public void btnRightParentheses(View v) { insertCharacter(")"); }
}