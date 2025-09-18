package com.example.automatonsimulator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GrammarTransitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrammarTransitionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btTestar;
    private EditText etEntrada;
    private TextView tvDefinicao;
    private LinearLayout linearLayout;
    public GrammarTransitionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GrammarTransitionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GrammarTransitionFragment newInstance(String param1, String param2) {
        GrammarTransitionFragment fragment = new GrammarTransitionFragment();
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
        View view = inflater.inflate(R.layout.fragment_grammar_transition, container, false);
        btTestar = view.findViewById(R.id.btTestar2);
        etEntrada = view.findViewById(R.id.etEntrada2);
        linearLayout = view.findViewById(R.id.linearLayoutDinamico);
        tvDefinicao = view.findViewById(R.id.tvDefinicao);
        btTestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPalavra();
            }
        });
        carregarDefinicao();
        carregarVariaveis(view);
        return view;
    }
    private void carregarVariaveis(View view)
    {
        linearLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        int i = 0;
        TextView textView;
        EditText editText;
        View itemView;
        Character variavel;
        while (i < GrammarFragment.variaveisList.size())
        {
            variavel = GrammarFragment.variaveisList.get(i);
            itemView = inflater.inflate(R.layout.item_variavel, linearLayout, false);
            textView = itemView.findViewById(R.id.tvVariavel);
            editText = itemView.findViewById(R.id.etTransicao);

            textView.setText(variavel+" ->");
            linearLayout.addView(itemView);
            i++;
        }
    }
    private void carregarDefinicao()
    {
        int i = 0;
        String definicao = "G = ({ ";
        Character variavel, terminal;
        while (i < GrammarFragment.variaveisList.size())
        {
            variavel = GrammarFragment.variaveisList.get(i);
            if (i + 1 < GrammarFragment.variaveisList.size())
                definicao = definicao + variavel + ", ";
            else
                definicao = definicao + variavel + " ";
            i++;
        }
        i = 0;
        definicao = definicao + "}, ";
        definicao = definicao + "{ ";
        while (i < GrammarFragment.terminaisList.size())
        {
            terminal = GrammarFragment.terminaisList.get(i);
            if (i + 1 < GrammarFragment.terminaisList.size())
                definicao = definicao + terminal + ", ";
            else
                definicao = definicao + terminal + " ";
            i++;
        }
        definicao = definicao + "}, ";
        definicao = definicao + "P, ";
        definicao = definicao + GrammarFragment.inicial + ")";
        tvDefinicao.setText(definicao);
    }
    private void verificarPalavra()
    {
        int i = 0;
        View item;
        EditText etTransicao;
        TextView tvVariavel;
        while (i < linearLayout.getChildCount())
        {
            item = linearLayout.getChildAt(i);
            etTransicao = item.findViewById(R.id.etTransicao);
            tvVariavel = item.findViewById(R.id.tvVariavel);

            Log.d(tvVariavel.getText().toString(), "transicao");
            Log.d(etTransicao.getText().toString(), "transicao");

            i++;
        }
    }
}