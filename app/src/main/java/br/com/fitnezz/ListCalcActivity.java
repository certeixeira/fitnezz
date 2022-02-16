package br.com.fitnezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListCalcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calc);

        Bundle extras = getIntent().getExtras();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_list);

        if (extras != null) {
            String type = extras.getString("type");

            new Thread(() -> {
                List<Register> registers = SqlHelper.getInstance(this).getRegisterBy(type);

                runOnUiThread(() -> {
                    Log.d("Teste", registers.toString());
                    ListCalcAdapter adapter = new ListCalcAdapter(registers);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                });
            }).start();
        }
    }

    private class ListCalcAdapter extends RecyclerView.Adapter<ListCalcAdapter.ListCalViewHolder> {

        private List<Register> datas;
        private OnItemClickListener listener;

        public ListCalcAdapter(List<Register> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public ListCalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ListCalViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ListCalViewHolder holder, int position) {
            Register data = datas.get(position);
            holder.bind(data);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        //View da célula que está dentro da recycleview
        private class ListCalViewHolder extends RecyclerView.ViewHolder {

            public ListCalViewHolder(@NonNull View itemView) {
                super(itemView);

            }

            public void bind(Register data) {
                String formatted = "";
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"));
                    Date dateSaved = sdf.parse(data.createdDate);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss", new Locale("pt", "BR"));
                     formatted = dateFormat.format(dateSaved);

                } catch (ParseException e) {

                }

                ((TextView)itemView).setText(
                        getString(R.string.list_response, data.response, formatted)
                );
            }
        }
    }
}