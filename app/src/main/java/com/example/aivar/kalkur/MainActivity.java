package com.example.aivar.kalkur;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private String op1 = "0";
    private String op2 = "0";
    private String sign = "";

    private TextView answer;
    private TextView topOperand;
    private TextView bottomOperand;
    private TextView signView;
    private TextView unary1;
    private TextView unary2;
    private TextView minus;
    private TextView answerView1;
    private TextView answerView2;

    private ArrayList<View> operand1Group = new ArrayList<>();
    private ArrayList<View> operand2Group = new ArrayList<>();

    private KalkReceiver receiver;
    private IntentFilter intentFilter;

    private enum OPERATION_STATE {
        INSERTING_OP1,
        INSERTING_SIGN,
        INSERTING_OP2,
        SHOWING_ANSWER,
        IDLE
    };

    private OPERATION_STATE currentlyInserting = OPERATION_STATE.INSERTING_OP1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView txt1 = (TextView)findViewById(R.id.op1);
        TextView txt2 = (TextView)findViewById(R.id.op2);
        TextView txt3 = (TextView)findViewById(R.id.sign);
        TextView txt4 = (TextView)findViewById(R.id.answer);
        TextView txt5 = (TextView)findViewById(R.id.unary1);
        TextView txt6 = (TextView)findViewById(R.id.unary2);
        TextView txt7 = (TextView)findViewById(R.id.answer1);
        TextView txt8 = (TextView)findViewById(R.id.answer2);
        Typeface tf = Typeface.createFromAsset(getAssets(), "BEBAS.ttf");
        txt1.setTypeface(tf);
        txt2.setTypeface(tf);
        txt3.setTypeface(tf);
        txt4.setTypeface(tf);
        txt5.setTypeface(tf);
        txt6.setTypeface(tf);
        txt7.setTypeface(tf);
        txt8.setTypeface(tf);

        answer = (TextView)  findViewById(R.id.answer);
        topOperand = (TextView) findViewById(R.id.op1);
        bottomOperand = (TextView) findViewById(R.id.op2);
        signView = (TextView) findViewById(R.id.sign);
        unary1 = (TextView) findViewById(R.id.unary1);
        unary2 = (TextView) findViewById(R.id.unary2);
        minus = (TextView) findViewById(R.id.minus);
        answerView1 = (TextView) findViewById(R.id.answer1);
        answerView2 = (TextView) findViewById(R.id.answer2);



        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            operand1Group = new ArrayList<View>(Arrays.asList(topOperand));
            operand2Group = new ArrayList<View>(Arrays.asList(bottomOperand));
        } else {
            operand1Group = new ArrayList<View>(Arrays.asList(unary1, topOperand));
            operand2Group = new ArrayList<View>(Arrays.asList(unary2, bottomOperand));
        }

        receiver = new KalkReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.aivar.calcAnswer");

    }

    public void onResume() {
        super.onResume();

        registerReceiver(receiver, intentFilter);
    }

    public void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    class KalkReceiver extends BroadcastReceiver {

        public KalkReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO update UI

            try {

                Bundle args = intent.getBundleExtra("answerBundle");
                String answer1 = getOperandText(args.getString("op1"), "");
                String answer2 = getOperandText(args.getString("op2"), "");
                String result = args.getString("answer");

                topOperand.setVisibility(View.VISIBLE);
                bottomOperand.setVisibility(View.VISIBLE);
                unary1.setVisibility(View.VISIBLE);
                unary2.setVisibility(View.VISIBLE);
                signView.setVisibility(View.VISIBLE);
                answerView2.setVisibility(View.VISIBLE);
                answerView1.setVisibility(View.VISIBLE);

                switch (currentlyInserting) {
                    case INSERTING_OP1:
                        answerView1.setText(answer1);
                        answer.setText(result);
                        answer.setVisibility(View.GONE);

                        bottomOperand.setVisibility(View.GONE);
                        unary2.setVisibility(View.GONE);
                        signView.setVisibility(View.GONE);
                        answerView2.setVisibility(View.GONE);
                        break;
                    case INSERTING_OP2:
                        answerView1.setText(answer1);
                        answerView2.setText(answer2);
                        answer.setText(result);
                        answer.setVisibility(View.GONE);
                        break;
                    case INSERTING_SIGN:
                        answerView1.setText(answer1);
                        answer.setText(result);
                        answer.setVisibility(View.GONE);

                        bottomOperand.setVisibility(View.GONE);
                        unary2.setVisibility(View.GONE);
                        answerView2.setVisibility(View.GONE);
                        break;
                    case SHOWING_ANSWER:
                        answerView1.setText(answer1);
                        answerView2.setText(answer2);
                        answer.setText(result);
                        answer.setVisibility(View.VISIBLE);

                        op1 = result;
                        topOperand.setText(op1);

                        topOperand.setVisibility(View.GONE);
                        bottomOperand.setVisibility(View.GONE);
                        unary1.setVisibility(View.GONE);
                        unary2.setVisibility(View.GONE);
                        signView.setVisibility(View.GONE);
                        answerView2.setVisibility(View.GONE);
                        answerView1.setVisibility(View.GONE);
                        break;
                }
            } catch (Exception e) {

            }
        }
    }

    public void numberClicked(View view) {

        Button btn = (Button) view;
        String clickedBtnText = btn.getText().toString();

        if (currentlyInserting.equals(OPERATION_STATE.INSERTING_SIGN)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP2;
        }

        if (currentlyInserting.equals(OPERATION_STATE.SHOWING_ANSWER)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP1;
        }

        switch(currentlyInserting) {
            case INSERTING_OP1:
                op1 = getOperandText(op1, clickedBtnText);
                topOperand.setText(op1);
                break;
            case INSERTING_OP2:
                bottomOperand.setVisibility(View.VISIBLE);
                op2 = getOperandText(op2, clickedBtnText);
                bottomOperand.setText(op2);
                break;
        }
        requestCalculation();
    }

    private String getOperandText(String currentOperand, String clickedBtnText) {
        if(currentOperand.equals("0") && clickedBtnText.equals("0")) {
            return currentOperand;
        } else if(currentOperand.equals("0") && !clickedBtnText.equals("0")) {
            return clickedBtnText;
        }

        if(operandBelowLength(currentOperand + clickedBtnText)) {
            return currentOperand + clickedBtnText;
        } else if(currentOperand.contains("E")){
            String[] arr = currentOperand.split("E");
            arr[0] = arr[0].substring(0, 10 - arr[1].length());
            return arr[0] + "E" + arr[1];
        } else {
            return currentOperand.substring(0, 10);
        }
    }

    public boolean operandBelowLength(String operand) {
        return operand.length() <= 10;
    }

    public void binaryClicked(View view) {

        if (currentlyInserting.equals(OPERATION_STATE.SHOWING_ANSWER)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP1;
        }

        if (currentlyInserting.equals(OPERATION_STATE.INSERTING_OP1) && op1.endsWith(".")) {
            op1 = op1.replace(".", "");
            topOperand.setText(op1);
        }

        if (currentlyInserting.equals(OPERATION_STATE.INSERTING_OP1) ||
                currentlyInserting.equals(OPERATION_STATE.INSERTING_SIGN)) {
            signView.setVisibility(View.VISIBLE);
            Button btn = (Button) view;
            String clickedBtnText = btn.getText().toString();

            sign = clickedBtnText;
            signView.setText(clickedBtnText);

            currentlyInserting = OPERATION_STATE.INSERTING_SIGN;

        }
        requestCalculation();
    }

    public void unaryClicked(View view) {

        if (currentlyInserting.equals(OPERATION_STATE.SHOWING_ANSWER)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP1;
        }

        Button btn = (Button) view;
        String clickedBtnText = btn.getText().toString();

        switch (currentlyInserting) {
            case INSERTING_OP1:
                unary1.setVisibility(View.VISIBLE);
                unary1.setText(clickedBtnText);
                break;
            case INSERTING_OP2:
                unary2.setVisibility(View.VISIBLE);
                unary2.setText(clickedBtnText);
                break;
        }
        requestCalculation();
    }

    public void minusClicked(View view) {

        if (currentlyInserting.equals(OPERATION_STATE.SHOWING_ANSWER)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP1;
        }

        switch (currentlyInserting) {
            case INSERTING_OP2:
                op2 = invertSign(bottomOperand);
                break;
            default:
                if (!signView.getText().equals("-")) {
                    Button minusBtn = new Button(this);
                    minusBtn.setText("-");
                    binaryClicked(minusBtn);
                } else {
                    op1 = invertSign(topOperand);
                }
        }
        requestCalculation();
    }

    public String invertSign(TextView operand) {

        String operandValue = "";
        int sign = 1;
        try {
            operandValue = operand.getText().toString();

            if (operandValue.isEmpty()) return operandValue;

            if (operandValue.contains("-")){
                operandValue = operandValue.replace("-", "");
            } else {
                operandValue = "-" + operandValue;
                sign = -1;
            }

            operand.setText(operandValue);

            Double.parseDouble(operandValue);

        } catch(Exception e){
            if (operandValue.contains("π")) operandValue = String.valueOf(Math.PI * sign);
            else if (operandValue.contains("e")) operandValue = String.valueOf(Math.E * sign);
        }

        return operandValue;
}

    public void commaClicked(View view) {
        switch (currentlyInserting) {
            case INSERTING_OP1:
                if (op1.length() < 9) {
                    op1 = op1.contains(".") ? op1 : op1.concat(".");
                    topOperand.setText(op1);
                }
                break;
            case INSERTING_OP2:
                if (op2.length() < 9) {
                    op2 = op2.contains(".") ? op2 : op2.concat(".");
                    bottomOperand.setText(op2);
                }
                break;
        }
    }

    public void constantClicked(View view) {

        if (currentlyInserting.equals(OPERATION_STATE.SHOWING_ANSWER)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP1;
        }

        if (currentlyInserting.equals(OPERATION_STATE.INSERTING_SIGN)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP2;
        }

        Button constantButton = (Button) view;
        String constant = constantButton.getText().toString();
        double constantValue = 0;

        if (constant.equals("π")) {
            constantValue = Math.PI;
        } else if (constant.equals("e")) {
            constantValue = Math.E;
        } else {
            return;
        }

        switch (currentlyInserting) {
            case INSERTING_OP2:
                int op2sign = op2.equals("") ? 1: (int) Math.signum(Double.parseDouble(op2));
                op2sign = op2sign == 0 ? 1 : op2sign;
                op2 = Double.toString(op2sign * constantValue);
                bottomOperand.setText(op2sign == -1 ? ("-" + constant) : constant);
                bottomOperand.setVisibility(View.VISIBLE);
                break;
            default:
                int op1sign = op1.equals("") ? 1 : (int) Math.signum(Double.parseDouble(op1));
                op1sign = op1sign == 0 ? 1 : op1sign;
                op1 = Double.toString(op1sign * constantValue);
                topOperand.setText(op1sign == -1 ? ("-" + constant) : constant);
        }
        requestCalculation();
    }

    public void clear(View view) {

        op1 = "0";
        op2 = "0";
        sign = "";

        topOperand.setVisibility(View.VISIBLE);

        currentlyInserting = OPERATION_STATE.INSERTING_OP1;

        unary1.setText("");
        unary2.setText("");
        topOperand.setText("0");
        bottomOperand.setText("");
        signView.setText("");
        answer.setText("");
        answerView1.setText("");
        answerView2.setText("");
    }

    public void deleteClicked(View view) {

        if (currentlyInserting.equals(OPERATION_STATE.SHOWING_ANSWER)) {
            currentlyInserting = OPERATION_STATE.INSERTING_OP1;
        }

        boolean didErase = false;

        switch (currentlyInserting) {
            case INSERTING_OP1:
                for (int i = (operand1Group.size()-1); i > -1; i--) {
                    TextView erasable = (TextView) operand1Group.get(i);
                    if (!didErase) didErase = eraseOne(erasable);
                    op1 = topOperand.getText().toString();
                    if (didErase && erasable.length() != 0) break;
                    if (didErase && i == 0 && erasable.getText().length() == 0) topOperand.setText("0");
                }
                break;
            case INSERTING_OP2:
                for (int i = (operand2Group.size()-1); i > -1; i--) {
                    TextView erasable = (TextView) operand2Group.get(i);
                    if (!didErase) didErase = eraseOne(erasable);
                    op2 = bottomOperand.getText().toString();
                    if (didErase && erasable.length() != 0) break;
                    if (didErase && i == 0 && erasable.getText().length() == 0) currentlyInserting = OPERATION_STATE.INSERTING_SIGN;
                }
                break;
            case INSERTING_SIGN:
                sign = "";
                signView.setText(sign);
                currentlyInserting = OPERATION_STATE.INSERTING_OP1;
                break;
        }
        requestCalculation();
    }

    private boolean eraseOne(TextView erasable) {

        String viewText = erasable.getText().toString();

        if (viewText.equals("sin") ||
                viewText.equals("cos") ||
                viewText.equals("tan")) {
            erasable.setText("");
            return true;
        }

        if (viewText.matches("\\-\\d$") || viewText.matches("\\-π$") || viewText.matches("\\-e$")) {
            erasable.setText("");
            return true;
        } else if (viewText.length() == 0) {
            return false;
        } else {
            erasable.setText(viewText.substring(0,viewText.length() - 1));
            return true;
        }
    }

    public void requestCalculation() {

        Intent calculationIntent = new Intent("com.example.aivar.kalkur.requestCalculation");

        calculationIntent.putExtra("unary1", unary1.getText());
        calculationIntent.putExtra("op1",op1);
        calculationIntent.putExtra("sign",sign);
        calculationIntent.putExtra("unary2", unary2.getText());
        calculationIntent.putExtra("op2",op2);

        calculationIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        getApplicationContext().sendBroadcast(calculationIntent);
    }

    public void equalsClicked(View view){

        if(!currentlyInserting.equals(OPERATION_STATE.INSERTING_SIGN)) {

            requestCalculation();

            clear(null);

            currentlyInserting = OPERATION_STATE.SHOWING_ANSWER;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("op1", op1);
        outState.putString("op2", op2);
        outState.putString("sign", sign);
        outState.putSerializable("currentlyInserting", currentlyInserting);
        outState.putString("unary1", unary1.getText().toString());
        outState.putString("unary2", unary2.getText().toString());
        outState.putString("answer", answer.getText().toString());
        outState.putString("answer1", answerView1.getText().toString());
        outState.putString("answer2", answerView2.getText().toString());

        outState.putInt("op1Visibility", topOperand.getVisibility());
        outState.putInt("op2Visibility", bottomOperand.getVisibility());
        outState.putInt("signVisibility", signView.getVisibility());
        outState.putInt("unary1Visibility", unary1.getVisibility());
        outState.putInt("unary2Visibility", unary2.getVisibility());
        outState.putInt("answerVisibility", answer.getVisibility());
        outState.putInt("answer1Visibility", answerView1.getVisibility());
        outState.putInt("answer2Visibility", answerView2.getVisibility());

        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("ResourceType")

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        op1 = savedInstanceState.getString("op1");
        op2 = savedInstanceState.getString("op2");
        sign = savedInstanceState.getString("sign");
        currentlyInserting = (OPERATION_STATE) savedInstanceState.getSerializable("currentlyInserting");
        unary1.setText(savedInstanceState.getString("unary1"));
        unary2.setText(savedInstanceState.getString("unary2"));
        answer.setText(savedInstanceState.getString("answer"));
        answerView1.setText(savedInstanceState.getString("answer1"));
        answerView2.setText(savedInstanceState.getString("answer2"));

        topOperand.setText(op1);
        bottomOperand.setText(op2);
        signView.setText(sign);

        topOperand.setVisibility(savedInstanceState.getInt("op1Visibility"));
        bottomOperand.setVisibility(savedInstanceState.getInt("op2Visibility"));
        signView.setVisibility(savedInstanceState.getInt("signVisibility"));
        unary1.setVisibility(savedInstanceState.getInt("unary1Visibility"));
        unary2.setVisibility(savedInstanceState.getInt("unary2Visibility"));
        answer.setVisibility(savedInstanceState.getInt("answerVisibility"));
        answerView1.setVisibility(savedInstanceState.getInt("answer1Visibility"));
        answerView2.setVisibility(savedInstanceState.getInt("answer2Visibility"));
    }
}
