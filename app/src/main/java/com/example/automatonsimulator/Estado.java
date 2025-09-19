package com.example.automatonsimulator;
public class Estado
{
    private float x, y;
    private String  num;
    private int fim; // 1 = Estado FINAL
    private int inicio; // 1 = Estado INICIAL

    public Estado(float x, float y, String num, int fim, int inicio) {
        this.x = x;
        this.y = y;
        this.num = num;
        this.fim = fim;
        this.inicio = inicio;
    }

    public Estado() {
        this(0,0,"",0,0);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getFim() {
        return fim;
    }

    public void setFim(int fim) {
        this.fim = fim;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }
}
