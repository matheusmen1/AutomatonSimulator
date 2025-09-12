package com.example.automatonsimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TransicaoView extends View {
    private List<Transicao> transicoes = new ArrayList<>();

    public TransicaoView(Context context) {
        super(context);
    }

    public TransicaoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#2f2c79"));
        //paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);

        Paint paintTriangulo = new Paint();
        paintTriangulo.setColor(Color.parseColor("#2f2c79"));
        paintTriangulo.setStyle(Paint.Style.FILL);

        int i = 0;
        while (i < transicoes.size())
        {
            Transicao transicao = transicoes.get(i);
            canvas.drawLine(transicao.getOrigem().getX(), transicao.getOrigem().getY(),
                            transicao.getDestino().getX(), transicao.getDestino().getY(), paint);
            //canvas.drawText(estado.getNum(), estado.getX(), estado.getY() + 15, textPaint);

            i++;
        }

    }

    public void addLista(Transicao transicao){
        transicoes.add(transicao);
        invalidate();
    }

    public void remover(Transicao transicao){
        transicoes.remove(transicao);
        invalidate();
    }

    public void atualizarLig()
    {
        invalidate();
    }


}
