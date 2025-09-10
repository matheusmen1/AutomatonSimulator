package com.example.automatonsimulator;

import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Pattern regex;
    List<Character> alfabeto = new ArrayList<>();
    List<String> lista = new ArrayList<>(), listaOrdenada = new ArrayList<>();
    String expressao, entrada;
    boolean flag = false;
    EditText etExpressao, etEntrada;
    Button btTestar;
    TextView tvConjunto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etExpressao = findViewById(R.id.etExpressao);
        etEntrada = findViewById(R.id.etEntrada);
        btTestar = findViewById(R.id.btTestar);
        tvConjunto = findViewById(R.id.tvConjunto);
        btTestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testar();
            }
        });
    }
    private void achaPalavra()
    {
        List<String> fila = new ArrayList<>();

        if(testaPalavra("")){
            lista.add("ε");
        }
        fila.add("");
        while(!fila.isEmpty() && lista.size() < 10 && fila.get(0).length() <= 10)
        {
            String palavra = fila.remove(0);
            for(int i=0; i<alfabeto.size() && lista.size() < 10; i++)
            {
                char letra = alfabeto.get(i);
                String novaPalavra = palavra + letra;

                fila.add(novaPalavra);
                if(testaPalavra(novaPalavra))//pegar oque o antonucci fez
                {
                    lista.add(novaPalavra);
                }
            }
        }
//        if(lista.size() < 10  && tamanho <= 10)
//        {
//            for(int i=0; i<alfabeto.size() && lista.size()<10; i++)
//            {
//                char letra = alfabeto.get(i);
//                String teste = palavra + letra;
//
//                if(testaPalavra(teste))//pegar oque o antonucci fez
//                {
//                    lista.add(teste);
//                }
//                //achaPalavra(tamanho + 1, teste);
//            }
//            for(int i=0; i<alfabeto.size() && lista.size()<10; i++)
//            {
//                char letra = alfabeto.get(i);
//                String teste = palavra + letra;
//
//                achaPalavra(tamanho + 1, teste);
//            }
//        }
    }

    private void mostrarPalavrasNoTextView(List<String> lista)
    {
        String texto = "{" + String.join(", ", lista) + "}";
        tvConjunto.setText(texto);
        tvConjunto.setVisibility(View.VISIBLE);
    }

    boolean testaPalavra(String palavra)
    {
        Matcher matcher = regex.matcher(palavra);
        return matcher.matches(); //o matches me retorna se deu certo ou n
    }

    private Pattern gerarRegex()
    {
            try {
                String expressao = etExpressao.getText().toString();
                expressao = expressao.replaceAll(" ", "");
                expressao = expressao.replaceAll("\\+", "|");
                expressao = expressao.replaceAll("\\.", "");
                expressao = expressao.replaceAll("ε", "");
                int i = 0;
                char letra;
                //aqui eu coleto o meu alfabeto
                while(i < expressao.length())
                {
                    letra = expressao.charAt(i);
                    if (letra != '|' && letra != '(' && letra != ')' && letra != '[' && letra != ']' && letra != ',')
                    {
                        if(!alfabeto.contains(letra))
                        {
                            alfabeto.add(letra);
                        }
                    }
                    i++;
                }

                return Pattern.compile(expressao);
            }
            catch (Exception e)
            {
                return null;
            }

    }
    private List<String> ordenarLista(List<String> lista)
    {
        List<String> aux;
        aux = lista;
        String letraA, letraB;

        for(int i = 0; i < aux.size(); i++)
        {
            letraA = lista.get(i);
            for(int j = i + 1; j < aux.size(); j++)
            {
                letraB = aux.get(j);
                if (letraA.compareTo(letraB) > 0)
                {
                    aux.set(i, letraB);
                    aux.set(j, letraA);
                }
            }
        }
        return aux;
    }
    private void testar()
    {
        listaOrdenada.clear();
        lista.clear();
        entrada = etEntrada.getText().toString();
        regex = gerarRegex(); //retorna um pattern
        if (regex != null)
        {
            Matcher matcher = regex.matcher(entrada);
            //tvConjunto.setText(alfabeto.toString());
            //tvConjunto.setVisibility(View.VISIBLE);
            if(etExpressao.getText().toString().length() > 0){
                achaPalavra();
            }
            else
                lista.add("ε");
            //listaOrdenada = ordenarLista(lista);
            mostrarPalavrasNoTextView(lista);
            if (matcher.matches())
            {
                Toast.makeText(this, "Entrada ACEITA", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Entrada REJEITADA", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Expressão Regular INCORRETA", Toast.LENGTH_SHORT).show();

    }


}