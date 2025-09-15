package com.example.automatonsimulator;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutomatoFinitoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutomatoFinitoFragment extends Fragment {
    private float X, Y, offSetX = 0, offSetY =0;
    private int flagNew = 1, flagMove = 0, flagEdit = 0, flagDel = 0, flagLig = 0, flagLigIni = 0;
    private Button btNew, btMove, btEdit, btDel, btLig, btStepNext, btStepRun, btStepStop, btAFD;
    private AutomatonView automatonView;
    private TransicaoView transicaoView;
    private List<Estado> estadoList = new ArrayList<>();
    private List<Transicao> transicaoList = new ArrayList<>();
    private LinkedList<Integer> excluidoList = new LinkedList<>();
    private MenuItem it_final, it_inicial;
    public int cont = 0, index = -1;
    public static TextView tvEntrada;
    private Estado EstadoIniLig, EstadoFimLig;

    //variaveis estáticas para o controle correto dos estados na depuração e testes
    public static Estado atual, anterior = null;
    public static List<Estado> atuais = new ArrayList<>();
    public static List<Estado> anteriores = new ArrayList<>();
    public static int indiceAtual; //para percorrer a palavra de entrada
    public static int flagStep = 0;
    public static int flagAFD = 1; //deixar padrão sempre tratar como DETERMINÍSTICO

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AutomatoFinitoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutomatoFinito.
     */

    // TODO: Rename and change types and number of parameters
    public static AutomatoFinitoFragment newInstance(String param1, String param2) {
        AutomatoFinitoFragment fragment = new AutomatoFinitoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // diz ao sistema que esse fragmento tem menu
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_direita, menu); // inflando o menu aqui
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.it_newEnter) //nova entrada de palavra
        {
            novaEntrada();
        }
        else if(item.getItemId() == R.id.it_step) //modo de depuração
        {
            //AFD
            atual = getEstadoInicial();

            //AFND
            atuais.clear();
            atuais.add(atual);

            if(atual != null && existeEstadoFinal())
            {
                flagStep = 1;
                indiceAtual = 0;
                automatonView.atualizar();
                ativar(); //ativa o modo de depuração
                Toast.makeText(getActivity(), "Modo de DEPURAÇÃO", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.it_quickRun) //teste rápido
        {
            if(flagAFD == 1) //testa o DESTERMINÍSTICO
            {
                if (testaRapidoPalavraAFD())
                    Toast.makeText(getActivity(), "Palavra ACEITA!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Palavra REJEITADA!", Toast.LENGTH_SHORT).show();
            }
            else //testa o NÃO DETERMINÍSTICO
            {
                if(testaRapidoPalavraAFND())
                    Toast.makeText(getActivity(), "Palavra ACEITA!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Palavra REJEITADA!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.it_multipleRun)
        {
            Toast.makeText(getActivity(), "multiple run", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_automato_finito, container, false);
        btNew = view.findViewById(R.id.btNew);
        btDel = view.findViewById(R.id.btDel);
        btEdit = view.findViewById(R.id.btEdit);
        btMove = view.findViewById(R.id.btMove);
        btLig = view.findViewById(R.id.btLig);
        btAFD = view.findViewById(R.id.btAFD);
        btStepNext = view.findViewById(R.id.btStepNext);
        btStepRun = view.findViewById(R.id.btStepRun);
        btStepStop = view.findViewById(R.id.btStepStop);
        automatonView = view.findViewById(R.id.automatoView);
        transicaoView = view.findViewById(R.id.transicaoView);
        tvEntrada = view.findViewById(R.id.tvEntrada);

        //animação para bt new começa ativado
        updateButtonElevation(btNew);
        //os botões clicados, cada um com sua função
        btNew.setOnClickListener(v-> {
            flagNew = 1;
            flagDel = 0;
            flagEdit = 0;
            flagMove = 0;
            flagLig = 0;
            updateButtonElevation(btNew);
        });
        btMove.setOnClickListener(v -> {
            flagNew = 0;
            flagDel = 0;
            flagEdit = 0;
            flagMove = 1;
            flagLig = 0;
            updateButtonElevation(btMove);
        });
        btDel.setOnClickListener(v -> {
            flagNew = 0;
            flagDel = 1;
            flagEdit = 0;
            flagMove = 0;
            flagLig = 0;
            updateButtonElevation(btDel);
        });
        btEdit.setOnClickListener(v -> {
            flagNew = 0;
            flagDel = 0;
            flagEdit = 1;
            flagMove = 0;
            flagLig = 0;
            updateButtonElevation(btEdit);
        });
        btLig.setOnClickListener(v -> {
            flagNew = 0;
            flagDel = 0;
            flagEdit = 0;
            flagMove = 0;
            flagLig = 1;
            updateButtonElevation(btLig);
        });

        //botão de seleção de AFD () ou AFND
        btAFD.setOnClickListener(v->{
            if(flagAFD == 1) //deterministico ativo
            {
                flagAFD = 0; //ativando o não deterministico
                btAFD.setBackgroundColor(Color.parseColor("#2F4F4F"));
                btAFD.setTextColor(Color.parseColor("#FFFFFF"));
                btAFD.setText("AFND");
                Toast.makeText(getActivity(), "Modo NÃO Determinístico", Toast.LENGTH_SHORT).show();
            }
            else //não deterministico
            {
                //não aceitar transições com palavra vazia (tratar)
                //não aceitar transições para vários estados com o mesmo símbolo (tratar)
                flagAFD = 1; //ativando o deterministico
                btAFD.setBackgroundColor(Color.parseColor("#40E0D0"));
                btAFD.setTextColor(Color.parseColor("#000000"));
                btAFD.setText("AFD");
                transicaoList.clear(); //limpar a lista de transições
                transicaoView.transicoes.clear(); //limpa transições da view
                transicaoView.atualizarLig(); //atualizo a view para melhor interface
                Toast.makeText(getActivity(), "Modo Determinístico", Toast.LENGTH_SHORT).show();
            }
        });

        //botões para a depuração
        btStepNext.setVisibility(View.INVISIBLE); //começa invisível por default
        btStepNext.setOnClickListener(v->{
            if(flagAFD == 1) //DETERMINÍSTICOS
            {
                anterior = atual;
                if (tvEntrada.getText().toString().length() > 0)
                {
                    atual = proximoEstado(atual, tvEntrada.getText().toString().charAt(indiceAtual));
                }
                else
                if (getEstadoInicial() != null && getEstadoInicial().getFim() == 1 && getEstadoInicial().getInicio() == 1)
                {
                    atual = getEstadoInicial();
                }
                else
                {
                    atual = null;
                }
                if(atual == null)
                {
                    //deu pau
                    btStepNext.setVisibility(View.INVISIBLE);
                    btStepRun.setVisibility(View.INVISIBLE);
                    indiceAtual = 0;

                }
                else
                {
                    indiceAtual++;
                    if(indiceAtual == tvEntrada.getText().toString().length())
                    {
                        //cheguei ao final
                        if(atual.getFim() == 1)
                        {
                            btStepNext.setVisibility(View.INVISIBLE);
                            btStepRun.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            btStepNext.setVisibility(View.INVISIBLE);
                            btStepRun.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
            else //AFND
            {
                anteriores = atuais;
                if (tvEntrada.getText().toString().length() > 0)
                {
                    atuais = proximosEstados(tvEntrada.getText().toString().charAt(indiceAtual));
                }
                else if (getEstadoInicial() != null && getEstadoInicial().getFim() == 1 && getEstadoInicial().getInicio() == 1)
                {
                    atuais.clear();
                    atuais.add(getEstadoInicial());
                }
                else
                {
                    atuais.clear();
                }
                if(atuais.isEmpty())
                {
                    //deu pau
                    btStepNext.setVisibility(View.INVISIBLE);
                    btStepRun.setVisibility(View.INVISIBLE);
                    indiceAtual = 0;
                }
                else
                {
                    indiceAtual++;
                    if(indiceAtual == tvEntrada.getText().toString().length())
                    {
                        //cheguei ao final
                        if(existeUmEstadoFinal())
                        {
                            btStepNext.setVisibility(View.INVISIBLE);
                            btStepRun.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            btStepNext.setVisibility(View.INVISIBLE);
                            btStepRun.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            automatonView.atualizar();
        });

        btStepRun.setVisibility(View.INVISIBLE); //começa invisível por default
        btStepRun.setOnClickListener(v->{
            if(flagAFD == 1) //AFD
            {
                int flag = 0;
                while (indiceAtual < tvEntrada.getText().toString().length() && flag != 1)
                {
                    anterior = atual;
                    atual = proximoEstado(atual, tvEntrada.getText().toString().charAt(indiceAtual));
                    if (atual == null)
                    {
                        flag = 1;
                        indiceAtual = 0;
                    }
                    else
                        indiceAtual++;
                }
            }
            else //AFND
            {
                int flag = 0;
                while (indiceAtual < tvEntrada.getText().toString().length() && flag != 1)
                {
                    anteriores = atuais;
                    atuais = proximosEstados(tvEntrada.getText().toString().charAt(indiceAtual));
                    if (atuais.isEmpty())
                    {
                        flag = 1;
                        indiceAtual = 0;
                    }
                    else
                        indiceAtual++;
                }
            }
            btStepNext.setVisibility(View.INVISIBLE);
            btStepRun.setVisibility(View.INVISIBLE);
            automatonView.atualizar();
        });

        btStepStop.setVisibility(View.INVISIBLE);
        btStepStop.setOnClickListener(v->{
            //esse botão possui a cor android:color/system_error_800
            voltar(); //saio do modo de depuração
            flagStep = 0;
            automatonView.atualizar();
        });

        //a cada clique na tela, verificar o botão ativo e a sua respectiva ação
        automatonView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) // para obter as coordenadas do toque
                {
                    X = event.getX();
                    Y = event.getY();
                    if (flagNew == 1) // adicionar estado
                    {
                        Estado estado;
                        if (estadoList.size() > 0)
                        {

                            if (excluidoList.isEmpty())
                            {
                                estado = new Estado(X, Y, "q"+cont, 0,0);
                                cont++;
                            }
                            else
                            {
                                int aux = excluidoList.getFirst();
                                estado = new Estado(X, Y, "q"+aux, 0, 0);
                                excluidoList.removeFirst();
                            }
                        }
                        else
                        {
                            if (excluidoList.isEmpty())
                            {
                                estado = new Estado(X, Y, "q"+cont, 0, 1);
                                cont++;
                            }
                            else
                            {
                                int aux = excluidoList.getFirst();
                                estado = new Estado(X, Y, "q"+aux, 0, 1);
                                excluidoList.removeFirst();
                            }
                        }

                        estadoList.add(estado);
                        automatonView.add(estado);

                    }
                    else if(flagEdit == 1) // editar transicoes
                    {
                        int i = 0;
                        float auxX, auxY, distancia;
                        Estado estado;
                        while(i < estadoList.size())
                        {
                            estado = estadoList.get(i);
                            auxX = X - estado.getX();
                            auxY = Y - estado.getY();
                            distancia = (float) Math.sqrt(Math.pow(auxX, 2) + Math.pow(auxY, 2));
                            if (distancia <= 70) // achei meu estado
                            {

                                View anchor = new View(getContext());
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1,1);
                                params.leftMargin = (int) estado.getX();
                                params.topMargin = (int) estado.getY();
                                ((ViewGroup) automatonView.getParent()).addView(anchor, params);
                                PopupMenu popupMenu = new PopupMenu(getContext(), anchor);
                                popupMenu.getMenuInflater().inflate(R.menu.pop_pup_menu, popupMenu.getMenu());
                                it_final = popupMenu.getMenu().findItem(R.id.it_final);
                                it_inicial = popupMenu.getMenu().findItem(R.id.it_initial);
                                if (estado.getFim() == 1) // se meu estado, ja for final, então removo
                                {
                                    it_final.setTitle("Remove Final");
                                }
                                if(estado.getInicio() == 1) // se meu estado, ja for inicial, então removo
                                {
                                    it_inicial.setTitle("Remove Initial");
                                }
                                Estado finalEstado = estado;
                                int finalI = i;
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item)
                                    {
                                        if(item.getItemId()==R.id.it_final)
                                        {
                                            if (finalEstado.getFim() == 1) // ou seja, vou remover o estado final.
                                            {
                                                finalEstado.setFim(0);
                                                estadoList.set(finalI, finalEstado);
                                                automatonView.atualizarEstado(finalEstado, finalI);
                                            }
                                            else
                                            {
                                                finalEstado.setFim(1);
                                                estadoList.set(finalI, finalEstado);
                                                automatonView.atualizarEstado(finalEstado, finalI);
                                            }

                                        }
                                        if(item.getItemId()==R.id.it_initial)
                                        {
                                            if (finalEstado.getInicio() == 1) // ou seja, vou remover o estado inicial.
                                            {
                                                finalEstado.setInicio(0);
                                                estadoList.set(finalI, finalEstado);
                                                automatonView.atualizarEstado(finalEstado, finalI);
                                            }
                                            else // verificar se há outro com estado inicial
                                            {
                                                Estado aux;
                                                int i = 0;
                                                while(i < estadoList.size() && estadoList.get(i).getInicio() != 1)
                                                    i++;
                                                if (i < estadoList.size()) // achou
                                                {
                                                    aux = estadoList.get(i);
                                                    aux.setInicio(0);
                                                    estadoList.set(i, aux);
                                                    automatonView.atualizarEstado(aux, i);
                                                }
                                                finalEstado.setInicio(1);
                                                estadoList.set(finalI, finalEstado);
                                                automatonView.atualizarEstado(finalEstado, finalI);
                                            }
                                        }
                                        return true;
                                    }
                                });
                                popupMenu.setOnDismissListener(menu -> {
                                    ((ViewGroup) anchor.getParent()).removeView(anchor);
                                });
                                popupMenu.show();
                            }
                            i++;
                        }
                    }
                    else if(flagDel == 1) // deletar estados
                    {
                        // preciso encontrar X = Estado.getX e Y = Estado.getY // X é coluna e Y é linha
                        int flag = 0;
                        int i = 0;
                        float auxX, auxY, distancia;
                        Estado estado;
                        while(i < estadoList.size() && flag != 1)
                        {
                            estado = estadoList.get(i);
                            auxX = X - estado.getX();
                            auxY = Y - estado.getY();
                            distancia = (float) Math.sqrt(Math.pow(auxX, 2) + Math.pow(auxY, 2));
                            if (distancia <= 70)
                            {
                                for(int j=0; j< transicaoList.size(); j++) //remover todas as transições com esse automato
                                {
                                    if(transicaoList.get(j).getDestino() == estado || transicaoList.get(j).getOrigem() == estado)
                                    {
                                        Transicao transicao = transicaoList.get(j);
                                        transicaoList.remove(transicao);
                                        transicaoView.remover(transicao);
                                        j--;
                                    }
                                }
                                estadoList.remove(estado);
                                automatonView.remover(estado);
                                flag = 1;
                                excluidoList.addFirst(Integer.parseInt(estado.getNum().substring(1)));
                                excluidoList.sort(null);
                            }
                            i++;
                        }

                    }
                    else if(flagMove == 1)
                    {
                        Estado estado;
                        float auxX, auxY, distancia;
                        int i = 0;
                        int flag = 0;
                        while(i < estadoList.size() && flag != 1)
                        {
                            estado = estadoList.get(i);
                            auxX = X - estado.getX();
                            auxY = Y - estado.getY();
                            distancia = (float) Math.sqrt(Math.pow(auxX, 2) + Math.pow(auxY, 2));
                            if (distancia <= 70)
                            {
                                flag = 1;
                                index = i;
                                offSetX = auxX;
                                offSetY = auxY;
                            }
                            i++;
                        }
                    }
                    else if(flagLig == 1)
                    {
                        Estado estado = null;
                        float auxX, auxY, distancia;
                        int i = 0;
                        int flag = 0;
                        while(i < estadoList.size() && flag != 1)
                        {
                            estado = estadoList.get(i);
                            auxX = X - estado.getX();
                            auxY = Y - estado.getY();
                            distancia = (float) Math.sqrt(Math.pow(auxX, 2) + Math.pow(auxY, 2));
                            if (distancia <= 70)
                            {
                                flag = 1;
                            }
                            i++;
                        }
                        if(flag == 0)
                            estado = null;

                        //já busquei o automato que cliquei
                        if(flagLigIni == 0) //estou pegando o primeiro
                        {
                            flagLigIni = 1;

                            if(estado == null) //nao peguei nada
                            {
                                flagLigIni = 0;
                            }
                            else //peguei alguma coisa
                            {
                                //setar o botão para outra cor
                                EstadoIniLig = estado;
                            }
                        }
                        else //estou pegando o segundo
                        {
                            flagLigIni = 0;

                            //deixar o outro estado inicial com uma cor normal

                            if(estado == null)
                            {
                                EstadoIniLig = null;
                                EstadoFimLig = null;
                            }
                            else
                            {
                                // cria cópias finais para capturar no lambda
                                final Estado estadoSelecionado = estado;
                                //final Transicao transicao = new Transicao();

                                //aparecer o campo para colocar os characteres
                                EditText input = new EditText(v.getContext());

                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Enter Input")
                                        .setView(input)
                                        .setPositiveButton("Ok", (dialog, which) -> {
                                            //PEGAR UMA STRING COM O ALFABETO DIGITADO
                                            String coletado = input.getText().toString();
                                            int flagExiste = 0;
                                            Transicao transicao = procuraTransicao(estadoSelecionado);

                                            if(flagAFD == 1 && coletado.isEmpty()) //não posso permitir
                                            {
                                                Toast.makeText(getActivity(), "Transição Não Permitida", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(flagAFD == 1 && !transicaoValida(EstadoIniLig, coletado))
                                            {
                                                Toast.makeText(getActivity(), "Transição Não Permitida", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                if (transicao == null) //transição ainda não existia
                                                {
                                                    transicao = new Transicao();
                                                }
                                                else //transição já existia
                                                    flagExiste = 1;

                                                // aqui você já tem a string
                                                //DESSA STRING, PEGAR CADA CARACTER E MANDAR PARA A LISTA DE CHARACTERES DA TRANSIÇÃO
                                                List<Character> lista = characteresObtidos(coletado); //obter todos os caracteres diferentes da minha string
                                                if(lista.isEmpty())
                                                    lista.add('ε');
                                                List<Character> listaTransicao = transicao.getCharacteres();
                                                if(listaTransicao == null)
                                                    transicao.setCharacteres(lista);
                                                else
                                                {
                                                    //adiciono os characteres qua ainda não existem
                                                    //se já existir essa aresta, apenas adicionar mais elementos na lista de characteres
                                                    for(int iterator=0; iterator<lista.size(); iterator++)
                                                    {
                                                        if(!listaTransicao.contains(lista.get(iterator)))
                                                            listaTransicao.add(lista.get(iterator));
                                                    }
                                                    transicao.setCharacteres(listaTransicao); //devolvo a lista atualizada
                                                }

                                                //fazer a ligação entre os estados EstadoIniLig e EstadoFimLig
                                                EstadoFimLig = estadoSelecionado;
                                                if(flagExiste == 0) //a transição não existia
                                                {
                                                    transicao.setOrigem(EstadoIniLig); //= new Transicao(EstadoIniLig, EstadoFimLig, null);
                                                    transicao.setDestino(EstadoFimLig);
                                                    transicaoList.add(transicao);
                                                    transicaoView.addLista(transicao);
                                                }
                                                else
                                                    transicaoView.atualizarLig();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        }
                    }

                }
                else
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    if (index != -1 && flagMove == 1) // estado selecionado
                    {
                        Estado estado = estadoList.get(index);
                        estado.setX(event.getX() - offSetX);
                        estado.setY(event.getY() - offSetY);
                        automatonView.atualizarEstado(estado, index);
                        transicaoView.atualizarLig();
                    }

                }
                else
                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                    index = -1;

                return true;
            }
        });
        return view;
    }

    private void voltar()
    {
        atual = null;
        indiceAtual = 0;
        btStepNext.setVisibility(View.INVISIBLE);
        btStepStop.setVisibility(View.INVISIBLE);
        btStepRun.setVisibility(View.INVISIBLE);

        btMove.setVisibility(View.VISIBLE);
        btNew.setVisibility(View.VISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        btDel.setVisibility(View.VISIBLE);
        btLig.setVisibility(View.VISIBLE);
        btAFD.setVisibility(View.VISIBLE);
    }

    private void ativar()
    {
        btStepNext.setVisibility(View.VISIBLE);
        btStepStop.setVisibility(View.VISIBLE);
        btStepRun.setVisibility(View.VISIBLE);

        btMove.setVisibility(View.INVISIBLE);
        btNew.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.INVISIBLE);
        btDel.setVisibility(View.INVISIBLE);
        btLig.setVisibility(View.INVISIBLE);
        btAFD.setVisibility(View.INVISIBLE);
    }

    public List<Character> characteresObtidos(String stringLida)
    {
        Character character;
        List<Character> lista = new ArrayList<>();

        for (int i=0; i<stringLida.length(); i++)
        {
            character = stringLida.charAt(i);
            if(!lista.contains(character) && verificaSimbolo(character)){
                lista.add(character);
            }
        }
        return lista;
    }

    private boolean verificaSimbolo(Character simbolo) {
        if (simbolo == null) return false;
        char c = simbolo.charValue();
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }

    public Transicao procuraTransicao(Estado estado)
    {
        int i;
        for(i=0; i<transicaoList.size() && (EstadoIniLig != transicaoList.get(i).getOrigem() || transicaoList.get(i).getDestino() != estado); i++);
        if(i < transicaoList.size())
            return transicaoList.get(i);
        return null;
    }

    private void novaEntrada()
    {
        EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
        .setTitle("Enter Input")
        .setView(input)
        .setPositiveButton("Ok", (dialog, which) -> {
            int flag = 0;
            int tam = input.getText().toString().length();
            for(int i=0; i<tam && flag!=1; i++)
            {
                if(!verificaSimbolo(input.getText().toString().charAt(i)))
                    flag = 1;
            }
            if(flag == 1) //deu errado
            {
                Toast.makeText(getActivity(), "Entrada NÃO PERMITIDA!!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                tvEntrada.setText(input.getText().toString());
                tvEntrada.setTextColor(Color.parseColor("#FFFFFF")); //definindo a cor do texto para branco
            }
        })
        .setNegativeButton("Cancel", null)
        .show();
    }

    private void updateButtonElevation(Button activeButton)
    {
        Button[] allButtons = {btNew, btMove, btEdit, btDel, btLig};

        float activeElevation = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                15, // botão ativo mais alto
                getResources().getDisplayMetrics()
        );

        float inactiveElevation = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,// botão inativo mais baixo
                getResources().getDisplayMetrics()
        );

        int activeColor = Color.parseColor("#9f90ea");  // cor do botão ativo -> roxo mais claro
        int inactiveColor = Color.parseColor("#2f2c79"); // cor padrão dos botões

        for (Button button : allButtons) {
            if (button == activeButton) {
                // Anima elevação e escala
                button.animate()
                        .translationZ(activeElevation)
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .start();

                // Muda cor do botão ativo
                if (button instanceof com.google.android.material.button.MaterialButton) {
                    ((com.google.android.material.button.MaterialButton) button)
                            .setBackgroundTintList(ColorStateList.valueOf(activeColor));
                }
            } else {
                button.animate()
                        .translationZ(inactiveElevation)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();

                // Volta para cor normal
                if (button instanceof com.google.android.material.button.MaterialButton) {
                    ((com.google.android.material.button.MaterialButton) button)
                            .setBackgroundTintList(ColorStateList.valueOf(inactiveColor));
                }
            }
        }
    }

    // FUNÇÕES PARA TESTAR AUTOMATOS RAPIDAMENTE (QUICK RUN)
    //testa apenas AUTOMATOS FINITOS DETERMINÍSTICOS
    private boolean testaRapidoPalavraAFD()
    {
        int i;
        String entrada = tvEntrada.getText().toString();
        Estado atual = getEstadoInicial();

        //testa o caso da palavra vazia
        if(entrada.length() == 0 && testaVazia())
            return true;

        if (atual == null) {
            /**
             * Se verdade então não é possível o teste, pois não foi especificado
             *  o estado inicial
             * Exibir de alguma forma isso para o usuário
             * */

            //exibir que não foi possível realizar o teste porque não existe INICIAL
            Toast.makeText(getActivity(), "Não existe onde INICIAR", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!existeEstadoFinal()) {
            /**
             * Quando não existe estado final também não consigo testar
             * */

            Toast.makeText(getActivity(), "Não existe nenhum estado FINAL", Toast.LENGTH_SHORT).show();
            //exibir que não foi possível realizar o teste porque não existe FINAL
            return false;
        } else if (transicaoList.size() == 0) {
            //aqui não existe nenhuma transição para testar

            Toast.makeText(getActivity(), "Não possui nenhuma TRANSIÇÃO", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            /**
             * Se entrou no else eu consigo realizar o teste
             * */
            //vou ir testando os estados e transições
            i = 0;
            while (i < entrada.length()) {
                atual = proximoEstado(atual, entrada.charAt(i));
                if (atual == null)
                    return false;

                i++; //continuo o meu teste
            }
            if (atual.getFim() == 1) //se verdade então meu último estado é válido
                return true;
            return false;
        }
    }

    private boolean testaRapidoPalavraAFND()
    {
        int i;
        String entrada = tvEntrada.getText().toString();

        //testa o caso da palavra vazia
        if(entrada.length() == 0 && testaVazia())
            return true;

        atuais.clear();
        atuais.add(getEstadoInicial());
        if (getEstadoInicial() == null) {
            /**
             * Se verdade então não é possível o teste, pois não foi especificado
             *  o estado inicial
             * Exibir de alguma forma isso para o usuário
             * */

            Toast.makeText(getActivity(), "Não existe onde INICIAR", Toast.LENGTH_SHORT).show();
            //exibir que não foi possível realizar o teste porque não existe INICIAL
            return false;
        } else if (!existeEstadoFinal()) {
            /**
             * Quando não existe estado final também não consigo testar
             * */

            Toast.makeText(getActivity(), "Não existe nenhum estado FINAL", Toast.LENGTH_SHORT).show();
            //exibir que não foi possível realizar o teste porque não existe FINAL
            return false;
        } else if (transicaoList.size() == 0) {
            //aqui não existe nenhuma transição para testar

            Toast.makeText(getActivity(), "Não possui nenhuma TRANSIÇÃO", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            /**
             * Se entrou no else eu consigo realizar o teste
             * */
            //vou ir testando os estados e transições
            i = 0;
            while (i < entrada.length()) {
                atuais = proximosEstados(entrada.charAt(i));
                if (atuais.isEmpty()) //não existe nenhuma transição para ir
                    return false;

                i++; //continuo o meu teste
            }
            if (existeUmEstadoFinal()) //se verdade então meu último estado é válido
                return true;
            return false;
        }
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

    public static boolean existeUmEstadoFinal()
    {
        for(Estado pos : atuais)
        {
            if(pos.getFim() == 1)
                return true; //pelo menos um existe
        }
        return false;
    }

    //função para pegar o estado inicial da minha lista de estados
    private Estado getEstadoInicial()
    {
        for (Estado estado: estadoList)
        {
            if(estado.getInicio() == 1)
                return estado;
        }
        return null;
    }

    //função para verificar se existe pelo menos um estado final
    private boolean existeEstadoFinal()
    {
        for (Estado estado: estadoList)
        {
            if(estado.getFim() == 1)
                return true;
        }
        return false;
    }

    private Estado proximoEstado(Estado atual, char simboloLido)
    {
        List<Transicao> opcoes = getTransicoes(atual);
        for(Transicao t : opcoes)
        {
            if(t.getCharacteres().contains(simboloLido))
                return t.getDestino();
        }
        return null;
    }

    private List<Transicao> getTransicoes(Estado atual)
    {
        List<Transicao> opcoes = new ArrayList<>();
        int pos = 0;
        for(Transicao t: transicaoList)
        {
            if(t.getOrigem().equals(atual))
                opcoes.add(transicaoList.get(pos));

            pos++;
        }
        return opcoes;
    }

    private boolean testaVazia()
    {
        Estado inicio = getEstadoInicial();
        return inicio != null && inicio.getInicio()==1 && inicio.getFim()==1;
    }

    private boolean transicaoValida(Estado estado, String coleta)
    {
        List<Transicao> transicoes = getTransicoes(estado);
        List<Character> simbolos = characteresObtidos(coleta);
        if(simbolos.isEmpty() && flagAFD == 1)
            return false;
        for(Transicao t : transicoes)
        {
            for(Character c : simbolos)
            {
                if(t.getCharacteres().contains(c))
                    return false; //já existe a transição para esse símbolo
            }
        }
        return true;
    }
}