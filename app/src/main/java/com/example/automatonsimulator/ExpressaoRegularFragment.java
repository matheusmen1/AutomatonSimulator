package com.example.automatonsimulator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpressaoRegularFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpressaoRegularFragment extends Fragment {

    Pattern regex;
    List<Character> alfabeto = new ArrayList<>();
    List<String> lista = new ArrayList<>(), listaOrdenada = new ArrayList<>();
    String expressao, entrada;
    private EditText etExpressao, etEntrada;
    private TextView tvConjunto;
    private Button btTestar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpressaoRegularFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpressaoRegularFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpressaoRegularFragment newInstance(String param1, String param2) {
        ExpressaoRegularFragment fragment = new ExpressaoRegularFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expressao_regular, container, false);
        etExpressao = view.findViewById(R.id.etExpressao);
        etEntrada = view.findViewById(R.id.etEntrada);
        btTestar = view.findViewById(R.id.btTestar);
        tvConjunto = view.findViewById(R.id.tvConjunto);
        btTestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testar(view);
            }
        });
        return view;
    }

    private void achaPalavra()
    {
        List<String> fila = new ArrayList<>();
        alfabeto.sort(null);

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
            expressao = etExpressao.getText().toString();
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

    private void testar(View view)
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
                Toast.makeText(view.getContext(), "Entrada ACEITA!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(view.getContext(), "Entrada REJEITADA!!", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(view.getContext(), "Expressão Regular INCORRETA!!", Toast.LENGTH_SHORT).show();
    }
}