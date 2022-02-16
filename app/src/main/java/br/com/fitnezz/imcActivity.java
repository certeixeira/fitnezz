package br.com.fitnezz;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class imcActivity extends AppCompatActivity {

    private EditText editHeight;
    private EditText editWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        editHeight = findViewById(R.id.edit_imc_height);
        editWeight = findViewById(R.id.edit_imc_weight);

        Button btnSend = findViewById(R.id.btn_imc_send);

        btnSend.setOnClickListener(v -> {
            if (!validade()) {
                Toast.makeText(imcActivity.this, R.string.fields_messages, Toast.LENGTH_LONG).show();
                return;
            }
            String sHeight = editHeight.getText().toString();
            String sWeight = editWeight.getText().toString();

            int height = Integer.parseInt(sHeight);
            int weight = Integer.parseInt(sWeight);

            double result = calculateImc(height, weight);
            Log.d("Teste", "Result: " + result);

            int imcResponseId = imcResponse(result);

            AlertDialog dialog = new AlertDialog.Builder(imcActivity.this)
                    .setTitle(getString(R.string.imc_response, result))
                    .setMessage(imcResponseId)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    })
                    .setNegativeButton(R.string.saves, ((dialog1, which) -> {

                        //execucutado em uma Thread separada evitando travamento da interface
                        new Thread(() -> {
                            long calcId = SqlHelper.getInstance(imcActivity.this).addItem("imc", result);
                            runOnUiThread(() -> {
                                if (calcId > 0) {
                                    Toast.makeText(imcActivity.this, "R.string.saved", Toast.LENGTH_SHORT).show();
                                    openListCalcActivity();
                                }
                            });
                        }).start();

                    }))
                    .create();
            dialog.show();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editHeight.getWindowToken(), 0);
        });
    }

    //mostra menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //evento de click do menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_list) {
            openListCalcActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openListCalcActivity() {
        Intent intent = new Intent(imcActivity.this, ListCalcActivity.class);
        intent.putExtra("type", "imc");
        startActivity(intent);
    }

    //garante a consistencia da função, retorna apenas resource string
    @StringRes
    //Retorna o res string de acordo com o cálculo do imc
    private int imcResponse(double imc) {
        if (imc < 15) {
            return R.string.imc_severely_low_weight;
        } else if (imc < 16) {
            return R.string.imc_very_low_weight;
        } else if (imc < 18.5) {
            return R.string.imc_low_weight;
        } else if (imc < 25) {
            return R.string.normal;
        } else if (imc < 30) {
            return R.string.imc_high_weight;
        } else if (imc < 35) {
            return R.string.imc_so_high_weight;
        } else if (imc < 40) {
            return R.string.imc_severely_high_weight;
        } else {
            return R.string.imc_extreme_weight;
        }
    }

    //calcula imc
    private double calculateImc(int height, int weight) {
        //peso / (altura * altura)
        //é necessário usar o doouble para conseguir o resultado
        return weight / (((double) height / 100) * ((double) height / 100));

    }

    //verifica se o valor é válido (não pode começar com zero)
    private boolean validade() {
        if (!editHeight.getText().toString().startsWith("0")
                && !editWeight.getText().toString().startsWith("0")
                && !editHeight.getText().toString().isEmpty()
                && !editWeight.getText().toString().isEmpty()) {
            //se todas as condições acima forem preenchidas o retorno é true, o form é válido
            return true;
        }
        return false;
    }
}