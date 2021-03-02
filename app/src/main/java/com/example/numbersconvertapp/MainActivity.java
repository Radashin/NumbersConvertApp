package com.example.numbersconvertapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String[] data = {"Десятичное число -> прямой, обратный, дополнительный коды", "Прямой код -> десятичное число",
            "Обратный код -> десятичное число", "Дополнительный код -> десятичное число",
            "Вещественное число -> двоичный код", "Представление вещественных чисел в формате IEEE754",
            "Обратный перевод из двоичного формата IEEE754 в десятичный"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        EditText input = findViewById(R.id.editTextNumber);
        input.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                calculateResult();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                calculateResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spinner.setAdapter(adapter);


    }

    @SuppressLint("SetTextI18n")
    private ArrayList<String> calculateBinaryCode(int number) {
        TextView stepsText = findViewById(R.id.stepsTextView);
        ArrayList<String> binaryCode = new ArrayList<>();
        while (number != 0) {
            binaryCode.add(0, String.valueOf(Math.abs(number % 2)));
            stepsText.setText(stepsText.getText() + String.valueOf(number) + " % " + 2 + " = " + number % 2 + "\n");
            number /= 2;
        }
        return binaryCode;
    }

    @SuppressLint("SetTextI18n")
    private String calculateBinaryCode(double number) {
        float a = 2011.56f;
        int left = (int) a;
        boolean divideOneMoreTime = left >= 2;
        String bin = "";
        while (divideOneMoreTime) {
            bin = left % 2 + bin;
            left /= 2;
            if (left < 2) {
                divideOneMoreTime = false;
            }
        }
        bin += ".";
        float right = (float) a - (int) a;
        for (int i = 0; i < 20; i++) {
            right = right * 2 - (int) right * 2;
            bin = bin + (int) right;
            if (right == 1.0) {
                break;
            }
        }
        return bin;
    }

    private ArrayList<String> calculateDirectCode(int number, ArrayList<String> binaryCode) {
        ArrayList<String> directCode = new ArrayList<>();
        if (number > 0) {
            directCode.add(0, "0");
        } else if (number < 0) {
            directCode.add(0, "1");
        }
        directCode.add(",");
        directCode.addAll(binaryCode);

        return directCode;
    }

    private ArrayList<String> calculateReverseCode(int number, ArrayList<String> binaryCode) {
        ArrayList<String> reverseCode = new ArrayList<>();
        if (number > 0) {
            reverseCode.add(0, "0");
            reverseCode.add(",");
            reverseCode.addAll(binaryCode);
        } else if (number < 0) {
            reverseCode.add(0, "1");
            reverseCode.add(",");
            for (String bit : binaryCode) {
                if (bit.equals("0")) {
                    reverseCode.add("1");
                } else if (bit.equals("1")) {
                    reverseCode.add("0");
                }
            }
        }
        return reverseCode;
    }

    private ArrayList<String> calculateAdditionalCode(int number, ArrayList<String> binaryCode) {
        ArrayList<String> additionalCode = new ArrayList<>();
        if (number > 0) {
            additionalCode.add(0, "0");
            additionalCode.add(",");
            additionalCode.addAll(binaryCode);
        } else if (number < 0) {
            ArrayList<String> reverseCode = new ArrayList<>();
            for (String bit : binaryCode) {
                if (bit.equals("0")) {
                    reverseCode.add("1");
                } else if (bit.equals("1")) {
                    reverseCode.add("0");
                }
            }
            int index = reverseCode.size();
            while (index > 0) {
                if (reverseCode.get(index - 1).equals("1")) {
                    reverseCode.set(index - 1, "0");
                } else if (reverseCode.get(index - 1).equals("0")) {
                    reverseCode.set(index - 1, "1");
                    break;
                }
                index--;
            }
            additionalCode.add(0, "1");
            additionalCode.add(",");
            additionalCode.addAll(reverseCode);
        }
        return additionalCode;
    }

    private String convertCodeArrayToString(ArrayList<String> codeArray) {
        StringBuilder text = new StringBuilder();
        for (String s : codeArray) {
            text.append(s);
        }
        return text.toString();
    }

    private String convertDirectCodeToNormal(String directCode) {
        char[] directCodeList = directCode.toCharArray();
        StringBuilder normalNumber = new StringBuilder();
        for (int i = 2; i < directCodeList.length; i++) {
            normalNumber.append(directCodeList[i]);
        }
        return String.valueOf(Integer.parseInt(normalNumber.toString(), 2));
    }

    private String convertReverseCodeToNormal(String reverseCode) {
        char[] reverseCodeList = reverseCode.toCharArray();
        StringBuilder normalNumber = new StringBuilder();
        if (reverseCodeList[0] == '1') {
            for (int i = 2; i < reverseCodeList.length; i++) {
                if (reverseCodeList[i] == '0') {
                    normalNumber.append('1');
                } else {
                    normalNumber.append('0');
                }
            }
            return String.valueOf(Integer.parseInt(normalNumber.toString(), 2) * -1);
        } else {
            for (int i = 2; i < reverseCodeList.length; i++) {
                normalNumber.append(reverseCodeList[i]);
            }
            return String.valueOf(Integer.parseInt(normalNumber.toString(), 2));
        }

    }

    private String convertAdditionalCodeToNormal(String additionalCode) {
        if(additionalCode.length()<3){
            throw new NumberFormatException();
        }
        char[] additionalCodeList = additionalCode.toCharArray();
        StringBuilder normalNumber = new StringBuilder();
        if (additionalCodeList[0] == '1') {
            for (int i = additionalCodeList.length - 1; i > 0; i--) {
                if (additionalCodeList[i] == '0') {
                    additionalCodeList[i] = '1';
                    for (int j = i - 1; j > 0; j--) {
                        if (additionalCodeList[j] == '1') {
                            additionalCodeList[j] = '0';
                            break;
                        }else {
                            Log.i("debug1", "srab");
                            additionalCodeList[j] = '1';
                        }
                    }
                    break;
                }
            }

            Log.i("debug1", Arrays.toString(additionalCodeList));

            for (int i = 2; i < additionalCodeList.length; i++) {
                if (additionalCodeList[i] == '0') {
                    normalNumber.append('1');
                } else {
                    normalNumber.append('0');
                }
            }
            return String.valueOf(Integer.parseInt(normalNumber.toString(), 2) * -1);
        } else {
            for (int i = 2; i < additionalCodeList.length; i++) {
                normalNumber.append(additionalCodeList[i]);
            }
            return String.valueOf(Integer.parseInt(normalNumber.toString(), 2));
        }

    }

    @SuppressLint("SetTextI18n")
    private void calculateResult() {
        Spinner spinner = findViewById(R.id.spinner);
        TextView stepsText = findViewById(R.id.stepsTextView);
        TextView outputText = findViewById(R.id.resultTextView);
        EditText input = findViewById(R.id.editTextNumber);
        stepsText.setText("");
        outputText.setText("");
//        if (String.valueOf(input.getText()).length() == 0) {
//            stepsText.setText("");
//            outputText.setText("");
//            return;
//        }
        switch (spinner.getSelectedItem().toString()) {
            case "Десятичное число -> прямой, обратный, дополнительный коды": {
                try {
                    int number = Integer.parseInt(String.valueOf(input.getText()));
                    ArrayList<String> binaryCode = calculateBinaryCode(number);
                    ArrayList<String> directCode = calculateDirectCode(number, binaryCode);
                    ArrayList<String> reverseCode = calculateReverseCode(number, binaryCode);
                    ArrayList<String> additionalCode = calculateAdditionalCode(number, binaryCode);
                    String text = String.format("Двоичное представление: (%s)\nПрямой код: (%s)\nОбратный код: (%s)\nДополнительный код: (%s)", convertCodeArrayToString(binaryCode), convertCodeArrayToString(directCode),
                            convertCodeArrayToString(reverseCode), convertCodeArrayToString(additionalCode));
                    outputText.setText(text);
                } catch (NumberFormatException e) {
                    stepsText.setText("Ошибка ввода");
                    outputText.setText("Ошибка ввода");
                }
                break;
            }
            case "Прямой код -> десятичное число": {
                try {
                    String directCode = String.valueOf(input.getText());
                    String number = convertDirectCodeToNormal(directCode);
                    String text = String.format("Десятичное представление: (%s)", number);
                    outputText.setText(text);
                } catch (NumberFormatException e) {
                    stepsText.setText("Ошибка ввода");
                    outputText.setText("Ошибка ввода");
                }
                break;
            }
            case "Обратный код -> десятичное число": {
                try {
                    String reverseCode = String.valueOf(input.getText());
                    String number = convertReverseCodeToNormal(reverseCode);
                    String text = String.format("Десятичное представление: (%s)", number);
                    outputText.setText(text);
                } catch (NumberFormatException e) {
                    stepsText.setText("Ошибка ввода");
                    outputText.setText("Ошибка ввода");
                }
                break;
            }
            case "Дополнительный код -> десятичное число": {
                try {
                    String additionalCode = String.valueOf(input.getText());
                    String number = convertAdditionalCodeToNormal(additionalCode);
                    String text = String.format("Десятичное представление: (%s)", number);
                    outputText.setText(text);
                } catch (NumberFormatException e) {
                    stepsText.setText("Ошибка ввода");
                    outputText.setText("Ошибка ввода");
                }
                break;
            }
            case "Вещественное число -> двоичный код":{
                try {
                    String binaryCode = calculateBinaryCode(Double.parseDouble(String.valueOf(input.getText())));
                    String text = String.format("Двоичное представление: (%s)", binaryCode);
                    outputText.setText(text);
                }catch (NumberFormatException e) {
                    stepsText.setText("Ошибка ввода");
                    outputText.setText("Ошибка ввода");
                }
                break;
            }
        }

    }

}