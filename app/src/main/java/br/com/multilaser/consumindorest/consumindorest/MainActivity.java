package br.com.multilaser.consumindorest.consumindorest;

import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText fieldCep;
    TextView descriptionCep;
    Button searchCep;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fieldCep = (EditText) findViewById(R.id.fieldCep);
        descriptionCep = (TextView) findViewById(R.id.descriptionCep);
        searchCep = (Button) findViewById(R.id.searchCep);

        fieldCep.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle = false;

                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    handle = true;
                    searchingCep();
                }

                return handle;
            }
        });

        searchCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchingCep();
            }
        });
    }

    private void searchingCep () {
        String getTextFieldCep = fieldCep.getText().toString();

        if(isValidCep(getTextFieldCep)){

            Call<CEP> call = new RetrofitConfig().getCEPService().buscarCEP(getTextFieldCep);
            call.enqueue(new Callback<CEP>() {
                @Override
                public void onResponse(Call<CEP> call, Response<CEP> response) {
                    CEP cepResponse = response.body();
                    descriptionCep.setText(cepResponse.toString());
                    toast = Toast.makeText(getApplicationContext(), "Consulta realizada com sucesso!", Toast.LENGTH_LONG);

                    if(cepResponse.getErro()){
                        toast = Toast.makeText(getApplicationContext(), "CEP não encontrado", Toast.LENGTH_SHORT);
                        descriptionCep.setText("Oh! Meus Deus! Não encontramos esse CEP.");
                    }

                    toast.show();
                }

                @Override
                public void onFailure(Call<CEP> call, Throwable t) {
                    Log.e("CEPService ", "Erro ao buscar o CEP:" + t.getMessage());
                    toast = Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        } else {

            fieldCep.setError("CEP Invalído");
            descriptionCep.setText("CEP Invalido. Digite o CEP corretamente");
            toast = Toast.makeText(getApplicationContext(), "CEP Invalido", Toast.LENGTH_LONG);
            toast.show();

        }
    }

    private Boolean isValidCep(String cep) {
        String CEP_PATTERN = "[0-9]{8}";
        Pattern pattern = Pattern.compile(CEP_PATTERN);
        Matcher matcher = pattern.matcher(cep);

        return matcher.matches();
    }

}
