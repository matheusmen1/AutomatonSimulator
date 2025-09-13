package com.example.automatonsimulator;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
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
    private float X, Y, offSetX = 0, offSetY =0 ;
    private int flagNew = 1, flagMove = 0, flagEdit = 0, flagDel = 0, flagLig = 0, flagLigIni = 0;
    private Button btNew, btMove, btEdit, btDel, btLig;
    AutomatonView automatonView;
    TransicaoView transicaoView;
    List<Estado> estadoList = new ArrayList<>();
    List<Transicao> transicaoList = new ArrayList<>();
    LinkedList<Integer> excluidoList = new LinkedList<>();
    MenuItem it_final, it_inicial;
    public int cont = 0, index = -1;
    private Estado EstadoIniLig, EstadoFimLig;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_automato_finito, container, false);
        btNew = view.findViewById(R.id.btNew);
        btDel = view.findViewById(R.id.btDel);
        btEdit = view.findViewById(R.id.btEdit);
        btMove = view.findViewById(R.id.btMove);
        btLig = view.findViewById(R.id.btLig);
        automatonView = view.findViewById(R.id.automatoView);
        transicaoView = view.findViewById(R.id.transicaoView);

        //animação para bt new começa ativado
        updateButtonElevation(btNew);

        btNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Modo Adição ATIVADO", Toast.LENGTH_SHORT).show();
                flagNew = 1;
                flagDel = 0;
                flagEdit = 0;
                flagMove = 0;
                flagLig = 0;
                updateButtonElevation(btNew);
            }
        });
        btMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Modo Mover ATIVADO", Toast.LENGTH_SHORT).show();
                flagNew = 0;
                flagDel = 0;
                flagEdit = 0;
                flagMove = 1;
                flagLig = 0;
                updateButtonElevation(btMove);
            }
        });
        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Modo Deletar ATIVADO ", Toast.LENGTH_SHORT).show();
                flagNew = 0;
                flagDel = 1;
                flagEdit = 0;
                flagMove = 0;
                flagLig = 0;
                updateButtonElevation(btDel);
            }
        });
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Modo Edição ATIVADO ", Toast.LENGTH_SHORT).show();
                flagNew = 0;
                flagDel = 0;
                flagEdit = 1;
                flagMove = 0;
                flagLig = 0;
                updateButtonElevation(btEdit);
            }
        });
        btLig.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Modo Ligação ATIVADO ", Toast.LENGTH_SHORT).show();
                flagNew = 0;
                flagDel = 0;
                flagEdit = 0;
                flagMove = 0;
                flagLig = 1;
                updateButtonElevation(btLig);
            }
        });
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
                                ordenarLista();
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
                                            int flagExiste = 0;
                                            Transicao transicao = procuraTransicao(estadoSelecionado);
                                            if (transicao == null) //transição ainda não existia
                                            {
                                                transicao = new Transicao();
                                            }
                                            else //transição já existia
                                                flagExiste = 1;

                                            //PEGAR UMA STRING COM O ALFABETO DIGITADO
                                            String coletado = input.getText().toString();
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

                                            //Toast.makeText(v.getContext(), "Digitou: " + coletado, Toast.LENGTH_SHORT).show();
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
    public void ordenarLista()
    {
        int aux;
        for (int i = 0; i < excluidoList.size(); i++)
        {
            for (int j = i + 1; j < excluidoList.size(); j++)
            {
                if (excluidoList.get(i) > excluidoList.get(j))
                {
                   aux = excluidoList.get(i);
                   excluidoList.set(i, excluidoList.get(j));
                   excluidoList.set(j, aux);
                }
            }
        }

    }
    public List<Character> characteresObtidos(String stringLida){
        Character character;
        List<Character> lista = new ArrayList<>();

        for (int i=0; i<stringLida.length(); i++)
        {
            character = stringLida.charAt(i);
            if(!lista.contains(character)){
                lista.add(character);
            }
        }
        return lista;
    }

    public Transicao procuraTransicao(Estado estado)
    {
        int i;
        for(i=0; i<transicaoList.size() && (EstadoIniLig != transicaoList.get(i).getOrigem() || transicaoList.get(i).getDestino() != estado); i++);
        if(i < transicaoList.size())
            return transicaoList.get(i);
        return null;
    }

    private void updateButtonElevation(Button activeButton) {
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

        int activeColor = Color.parseColor("#9f90ea");  // cor do botão ativo (ex: laranja)
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
}