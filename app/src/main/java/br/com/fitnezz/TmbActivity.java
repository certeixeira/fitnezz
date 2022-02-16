package br.com.fitnezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TmbActivity extends AppCompatActivity {
    private EditText editHeight;
    private EditText editWeight;
    private EditText editAge;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmb_ativity);

        editAge = findViewById(R.id.edit_tmb_age);
        editHeight = findViewById(R.id.edit_tmb_height);
        editWeight = findViewById(R.id.edit_tmb_weight);
        spinner = findViewById(R.id.spinner_tmb_lifestyle);

        Button btnSend = findViewById(R.id.btn_tmb_send);
        btnSend.setOnClickListener(v -> {
            if (!validade()) {
                Toast.makeText(TmbActivity.this, "Todos os campos devem ser maiores do que zero", Toast.LENGTH_SHORT).show();
                return;
            }

            String sWeight = editWeight.getText().toString();

            int age = Integer.parseInt(editAge.getText().toString());
            int height = Integer.parseInt(editHeight.getText().toString());
            int weight = Integer.parseInt(sWeight);

            double result = calculateTmb(age, height, weight);
            double tmb = tmbResponse(result);
            Log.d("Teste", "resultado: " + tmb);

            AlertDialog dialog = new AlertDialog.Builder(TmbActivity.this)
                    .setTitle(getString(R.string.tmb_response, result))
                    .setMessage(getString(R.string.tmb_response, tmb))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) ->{
                    })
                    .setNegativeButton("Salvar", ((dialog1, which) -> {

                        new Thread(() ->{
                            long calcID = SqlHelper.getInstance(TmbActivity.this).addItem("tmb", result);
                            runOnUiThread(() -> {
                                if (calcID > 0) {
                                    Toast.makeText(TmbActivity.this, R.string.saves, Toast.LENGTH_SHORT).show();
                                    openListCalcActivity();
                                }
                            });
                        }).start();
                    }))
                    .create();
            dialog.show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editHeight.getWindowToken(),0);
            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editAge.getWindowToken(), 0);
        });
    }

    private double calculateTmb(int age, int height, int weight) {
        return 66 + (weight * 13.8) + (5 * height) - (6.8 * age);
    }

    private double tmbResponse(double tmb) {
        int index = spinner.getSelectedItemPosition();
        switch (index) {
            case 0: return tmb * 1.2;
            case 1: return tmb * 1.375;
            case 2: return tmb * 1.55;
            case 3: return tmb * 1.725;
            case 4: return tmb * 1.9;
            default: return 0;
        }
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

    //verifica se o valor é válido (não pode começar com zero)
    private boolean validade() {
        if (!editHeight.getText().toString().startsWith("0")
                && !editWeight.getText().toString().startsWith("0")
                && !editAge.getText().toString().startsWith("0")
                && !editHeight.getText().toString().isEmpty()
                && !editAge.getText().toString().isEmpty()
                && !editWeight.getText().toString().isEmpty()) {
            //se todas as condições acima forem preenchidas o retorno é true, o form é válido
            return true;
        }
        return false;
    }

    private void openListCalcActivity() {
        Intent intent = new Intent(TmbActivity.this, ListCalcActivity.class);
        intent.putExtra("type", "tmb");
        startActivity(intent);
    }

}