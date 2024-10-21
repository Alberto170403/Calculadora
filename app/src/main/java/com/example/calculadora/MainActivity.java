package com.example.calculadora;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText cuadroTexto;
    private String input = "";
    private boolean operadorPresionado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cuadroTexto = findViewById(R.id.cuadroTexto);

        configurarBotonNumerico(R.id.btn0, "0");
        configurarBotonNumerico(R.id.btn1, "1");
        configurarBotonNumerico(R.id.btn2, "2");
        configurarBotonNumerico(R.id.btn3, "3");
        configurarBotonNumerico(R.id.btn4, "4");
        configurarBotonNumerico(R.id.btn5, "5");
        configurarBotonNumerico(R.id.btn6, "6");
        configurarBotonNumerico(R.id.btn7, "7");
        configurarBotonNumerico(R.id.btn8, "8");
        configurarBotonNumerico(R.id.btn9, "9");

        configurarBotonOperador(R.id.btnSuma, "+");
        configurarBotonOperador(R.id.btnResta, "-");
        configurarBotonOperador(R.id.btnMultiplicacion, "*");
        configurarBotonOperador(R.id.btnDivision, "/");

        Button btnIgual = findViewById(R.id.btnIgual);
        btnIgual.setOnClickListener(view -> calcularResultado());

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(view -> limpiarTexto());
    }

    private void configurarBotonNumerico(int id, String valor) {
        Button button = findViewById(id);
        button.setOnClickListener(view -> agregarTexto(valor));
    }

    private void configurarBotonOperador(int id, String operador) {
        Button button = findViewById(id);
        button.setOnClickListener(view -> {
            if (!operadorPresionado && !input.isEmpty()) {
                agregarTexto(operador);
                operadorPresionado = true;
            }
        });
    }

    private void agregarTexto(String valor) {
        input += valor;
        cuadroTexto.setText(input);
        operadorPresionado = false;
    }

    private void limpiarTexto() {
        input = "";
        cuadroTexto.setText("");
        operadorPresionado = false;
    }

    private void calcularResultado() {
        try {
            double resultado = eval(input);
            cuadroTexto.setText(String.valueOf(resultado));
            input = String.valueOf(resultado);
        } catch (Exception e) {
            cuadroTexto.setText("Error");
            input = "";
        }
    }

    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }
}