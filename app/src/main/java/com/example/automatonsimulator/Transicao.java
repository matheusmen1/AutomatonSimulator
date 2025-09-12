package com.example.automatonsimulator;

import java.util.ArrayList;
import java.util.List;

public class Transicao {
    private Estado origem;
    private Estado destino;
    private List<Character> characteres;

    //construtores
    public Transicao(Estado origem, Estado destino, List<Character> characteres) {
        this.origem = origem;
        this.destino = destino;
        this.characteres = characteres;
    }

    public Transicao() {
        this(null,null,null);
    }

    //gets e sets
    public Estado getOrigem() {
        return origem;
    }

    public void setOrigem(Estado origem) {
        this.origem = origem;
    }

    public Estado getDestino() {
        return destino;
    }

    public void setDestino(Estado destino) {
        this.destino = destino;
    }

    public List<Character> getCharacteres() {
        return characteres;
    }

    public void setCharacteres(List<Character> characteres) {
        this.characteres = characteres;
    }

    public void addChar(Character character) {
        characteres.add(character);
    }

    public void removerChar(Character character){
        int i;
        for(i=0; i<characteres.size() && character != characteres.get(i); i++);

        if(i<characteres.size())
            characteres.remove(i);
    }
}
