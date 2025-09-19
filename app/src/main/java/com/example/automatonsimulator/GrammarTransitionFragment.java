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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    //lista de estados e transições para simular a gramática com algo parecido aos automatos
    private List<Estado> estados = new ArrayList<>();
    private List<Transicao> transicoes = new ArrayList<>();
    private List<Estado> atuais = new ArrayList<>();

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
        View item;
        EditText etTransicao;
        TextView tvVariavel;
        List<String> tokens = new ArrayList<>();
        String transicoes;
        boolean erro = false;

        //criação dos estados
        for(int i=0; i<linearLayout.getChildCount(); i++) //faço primeiro os meus estados
        {
            item = linearLayout.getChildAt(i);
            tvVariavel = item.findViewById(R.id.tvVariavel);
            String info = tvVariavel.getText().toString();
            info = String.valueOf(info.charAt(0)); //pegar apenas o character
            Estado estado = new Estado();
            estado.setFim(0); //não é fim
            estado.setInicio(0); //não é início
            estado.setNum(info);
            estados.add(estado);
        }

        //definir o estado inicial
        String fim = String.valueOf(GrammarFragment.inicial);
        //buscar pelo respectivo estado com o mesmo texto
        for(Estado e : estados)
        {
            if(fim.equals(e.getNum()))
                e.setInicio(1);
        }

        for (int i=0; i<linearLayout.getChildCount() && !erro; i++) //percorrer todas as entradas
        {
            String estadoAtual;
            List<Character> alfabetoAux = new ArrayList<>();

            item = linearLayout.getChildAt(i);
            etTransicao = item.findViewById(R.id.etTransicao); //input que o usuário pode digitar
            tvVariavel = item.findViewById(R.id.tvVariavel); //valor da entrada digitada na tela anterior

            //limpar a lista e receber a informação digitada
            transicoes = etTransicao.getText().toString();
            for (int j=0; j<tokens.size(); j++) //limpar a lista de tokens
                tokens.remove(0);
            if(!transicoes.isEmpty())
            {
                transicoes = transicoes.replaceAll(" ", ""); //tiro todos os espaços
                tokens = new ArrayList<>(Arrays.asList(transicoes.split("\\|"))); //separo em tokens pelo pipe
                if(verificarTokens(tokens))
                {
                    //se verdade então eu posso continuar o meu código
                    //fazer as ligações de cada estado
                    alfabetoAux.clear();
                    estadoAtual = String.valueOf(tvVariavel.getText().toString().charAt(0)); //pego apenas o character da primeira posição
                    for(String token : tokens)
                    {
                        if(!token.isEmpty()) //se não estiver vazia faço alguma coisa
                        {
                            if(GrammarFragment.terminaisList.contains(token.charAt(0))) //se oq vai gerar esta no meu alfabeto
                            {
                                //terminais aqui é a mesma coisa que o alfabeto
                                //se entrei aqui é porque a transição é válida
                                if(token.length() == 1)
                                {
                                    /**
                                     * Tenho que adicionar em uma lista para depois comparar se todos
                                     *  os characteres que chegarão aqui existem no meu alfabeto
                                     *  pois se isso for verdade, então eu tenho um estado final
                                     * */
                                    if(!alfabetoAux.contains(token.charAt(0)))
                                    {
                                        alfabetoAux.add(token.charAt(0)); //adiciono para testar no final
                                    }
                                    //fazerTransicaoVolta(token, estadoAtual);
                                }
                                else //uma transicao de um estado para outro
                                {
                                    //preciso verificar se esse estado que vou tentar criar a ligação existe no meu conjunto V
                                    boolean achei = false;
                                    for(Estado estado : estados)
                                    {
                                        if(estado.getNum().equals(String.valueOf(token.charAt(1))))
                                            achei = true;
                                    }
                                    if(achei)
                                    {
                                        fazerTransicaoOutro(token, estadoAtual);
                                    }
                                    else
                                    {
                                        //deu pau
                                        erro = true;
                                        Toast.makeText(getActivity(), String.format("VARIABLE not exists: " +
                                                token.charAt(1)), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else
                            {
                                //deu pau
                                erro = true;
                                Toast.makeText(getActivity(), String.format("LETTER NOT CONTAINS. At: " +
                                        token.charAt(0)), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if(contemTodoAlfabeto(alfabetoAux))
                    {
                        //então esse meu estado é um estado final
                        Estado e = null;
                        for(Estado estado : estados)
                        {
                            if(estado.getNum().equals(estadoAtual))
                                e = estado;
                        }
                        if(e != null)
                        {
                            //achei o respectivo estado
                            e.setFim(1);
                        }
                    }
                }
                else
                {
                    //deu pau
                    erro = true;
                    Toast.makeText(getActivity(), String.format("INVALID TOKEN at:" +
                           tvVariavel.getText().toString()), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                //deu pau
                erro = true;
                Toast.makeText(getActivity(), String.format("" +
                        "EMPTY: "+tvVariavel.getText().toString()), Toast.LENGTH_SHORT).show();
            }

            Log.d(tvVariavel.getText().toString(), "transicao");
            Log.d(etTransicao.getText().toString(), "transicao");
        }

        //chamar a função para testar agora com os meus estados e transições feitos
        if(testaRapidoPalavraAFND(etEntrada.getText().toString()) && !erro)
        {
            //deu certo
            Toast.makeText(getActivity(), "ACCEPTED word!", Toast.LENGTH_SHORT).show();
        }
        else if(!erro) //ou seja, o erro foi a entrada
        {
            //deu pau
            Toast.makeText(getActivity(), "NOT ACCEPTED word!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean contemTodoAlfabeto(List<Character> alfabeto)
    {
        alfabeto.sort(null);
        GrammarFragment.terminaisList.sort(null);
        for(int i=0; i<alfabeto.size() && i<GrammarFragment.terminaisList.size(); i++)
        {
            if(!alfabeto.get(i).equals(GrammarFragment.terminaisList.get(i)))
                return false;
        }
        return true;
    }

    private boolean verificarTokens(List<String> tokens) {
        for (String token : tokens) {
            if (!token.isEmpty()) {
                if (token.length() == 1) {
                    char c = token.charAt(0);
                    if (!Character.isLowerCase(c)) {
                        return false; // único char deve ser minúsculo
                    }
                } else if (token.length() == 2) {
                    char c1 = token.charAt(0);
                    char c2 = token.charAt(1);
                    if (!Character.isLowerCase(c1) || !Character.isUpperCase(c2)) {
                        return false; // regra não cumprida
                    }
                } else {
                    return false; // tamanho >= 3 é inválido
                }
            }
        }
        return true; // todos os tokens passaram
    }

    private void fazerTransicaoVolta(String token, String estadoAtual)
    {
        Transicao transicao = new Transicao();
        Estado inicio = null, destino = null;
        List<Character> characteres = new ArrayList<>();

        //aqui eu tenho certeza de que o token tem tamanho 1
        //procurar o meu estado na lista de estados
        for(Estado estado : estados)
        {
            if(estado.getNum().equals(estadoAtual))
                inicio = destino = estado;
        }

        //aqui eu preciso verificar se essa transição já existe
        transicao.setOrigem(inicio);
        transicao.setDestino(destino);
        characteres.add(token.charAt(0));
        transicao.setCharacteres(characteres);

        Transicao teste = existeTransicao(transicao);
        if(teste == null) //a transicao ainda não existe
        {
            transicoes.add(transicao);
        }
        else //a transicao já existe, apenas adicionar nela os characteres
        {
            if(!teste.getCharacteres().contains(token.charAt(0)))
            {
                teste.getCharacteres().add(token.charAt(0));
            }
        }
    }

    private void fazerTransicaoOutro(String token, String estadoAtual)
    {
        Transicao transicao = new Transicao();
        Estado inicio = null, destino = null;
        List<Character> characteres = new ArrayList<>();

        //aqui eu tenho certeza de que o token tem tamanho 2

        //procurar o meu estado inicial na lista de estados
        for(Estado estado : estados)
        {
            if(estado.getNum().equals(estadoAtual))
                inicio = estado;
        }

        //procurar o meu estado final na lista de estados
        for(Estado estado : estados)
        {
            if(estado.getNum().equals(String.valueOf(token.charAt(1))))
                destino = estado;
        }

        //aqui eu preciso verificar se essa transição já existe
        //aqui eu preciso verificar se essa transição já existe
        transicao.setOrigem(inicio);
        transicao.setDestino(destino);
        characteres.add(token.charAt(0));
        transicao.setCharacteres(characteres);

        Transicao teste = existeTransicao(transicao);
        if(teste == null) //a transicao ainda não existe
        {
            transicoes.add(transicao);
        }
        else //a transicao já existe, apenas adicionar nela os characteres
        {
            if(!teste.getCharacteres().contains(token.charAt(0)))
            {
                teste.getCharacteres().add(token.charAt(0));
            }
        }
    }

    private Transicao existeTransicao(Transicao transicao)
    {
        for(Transicao t : transicoes)
        {
            if(t.getOrigem()==transicao.getOrigem() && t.getDestino() == transicao.getDestino())
            {
                return t; //encontrei a transição em si que estava pesquisando
            }
        }
        return null; //não encontrei nenhuma transição
    }

    private boolean testaRapidoPalavraAFND(String palavra)
    {
        int i;

        //testa o caso da palavra vazia
        if(palavra.length() == 0 && testaVazia())
            return true;

        atuais.clear();
        atuais.add(getEstadoInicial());
        if (getEstadoInicial() == null) {
            /**
             * Se verdade então não é possível o teste, pois não foi especificado
             *  o estado inicial
             * Exibir de alguma forma isso para o usuário
             * */

            Toast.makeText(getActivity(), "There Is No Place To START", Toast.LENGTH_SHORT).show();
            //exibir que não foi possível realizar o teste porque não existe INICIAL
            return false;
        } else if (!existeEstadoFinal()) {
            /**
             * Quando não existe estado final também não consigo testar
             * */

            Toast.makeText(getActivity(), "There Is No FINAL State", Toast.LENGTH_SHORT).show();
            //exibir que não foi possível realizar o teste porque não existe FINAL
            return false;
        } else if (transicoes.size() == 0) {
            //aqui não existe nenhuma transição para testar

            Toast.makeText(getActivity(), "It Has No TRANSITION", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            /**
             * Se entrou no else eu consigo realizar o teste
             * */
            //vou ir testando os estados e transições
            i = 0;
            while (i < palavra.length()) {
                atuais = proximosEstados(palavra.charAt(i));
                if (atuais.isEmpty()) //não existe nenhuma transição para ir
                    return false;

                i++; //continuo o meu teste
            }
            if (existeUmEstadoFinal()) //se verdade então meu último estado é válido
                return true;
            return false;
        }
    }

    private boolean testaVazia()
    {
        Estado inicio = getEstadoInicial();
        return inicio != null && inicio.getInicio()==1 && inicio.getFim()==1;
    }

    private Estado getEstadoInicial()
    {
        for (Estado estado: estados)
        {
            if(estado.getInicio() == 1)
                return estado;
        }
        return null;
    }

    private boolean existeUmEstadoFinal()
    {
        for(Estado pos : atuais)
        {
            if(pos.getFim() == 1)
                return true; //pelo menos um existe
        }
        return false;
    }

    private List<Estado> proximosEstados(char simbolo)
    {
        List<Estado> proximos = new ArrayList<>();
        for (Estado teste : atuais)
        {
            List<Estado> proximosTeste = proximosEstadosAFND(teste, simbolo);
            //proximos.addAll(proximosTeste); //mesma coisa que o for abaixo
            for(Estado retorno : proximosTeste) //adicionar os proximos de estado teste
            {
                if(!proximos.contains(retorno))
                    proximos.add(retorno);
            }
        }
        return proximos; //tratar depois, pois posso ter uma lista vazia, sem proximos estados
    }

    private List<Estado> proximosEstadosAFND(Estado teste, char simbolo)
    {
        List<Transicao> transicoes = getTransicoes(teste);
        List<Estado> proximos = new ArrayList<>();
        for(Transicao t : transicoes)
        {
            if((t.getCharacteres().contains(simbolo) || t.getCharacteres().contains('ε')) && !proximos.contains(t.getDestino()))
                proximos.add(t.getDestino());
        }
        return proximos;
    }

    private List<Transicao> getTransicoes(Estado atual)
    {
        List<Transicao> opcoes = new ArrayList<>();
        int pos = 0;
        for(Transicao t: transicoes)
        {
            if(t.getOrigem().equals(atual))
                opcoes.add(transicoes.get(pos));

            pos++;
        }
        return opcoes;
    }

    private boolean existeEstadoFinal()
    {
        for (Estado estado: estados)
        {
            if(estado.getFim() == 1)
                return true;
        }
        return false;
    }
}