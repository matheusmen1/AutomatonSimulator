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

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int i = 0;
        while (i < transicoes.size())
        {
            Transicao transicao = transicoes.get(i);
            float raio = 70f;

            // Posições centrais dos estados
            float xOrigem = transicao.getOrigem().getX();
            float yOrigem = transicao.getOrigem().getY();
            float xDestino = transicao.getDestino().getX();
            float yDestino = transicao.getDestino().getY();

            // Vetor entre origem e destino
            float dx = xDestino - xOrigem;
            float dy = yDestino - yOrigem;

            // Distância entre os centros
            float dist = (float) Math.sqrt(dx*dx + dy*dy);

            // Normalização
            float ux = dx / dist;
            float uy = dy / dist;

            // Ajuste para pegar exatamente na borda do círculo
            float x1 = xOrigem + ux * raio;
            float y1 = yOrigem + uy * raio;
            float x2 = xDestino - ux * raio;
            float y2 = yDestino - uy * raio;

            //desenhar a linha
            canvas.drawLine(x1, y1, x2, y2, paint);
            //desenhar o triangulo para simular a seta
            float arrowSize = 30f; // tamanho da seta

            // direção normalizada já temos: ux, uy
            // perpendicular (rotaciona 90 graus)
            float perpX = -uy;
            float perpY = ux;

            // pontos do triângulo
            float tipX = x2;
            float tipY = y2;

            float baseX1 = x2 - ux * arrowSize + perpX * (arrowSize / 2);
            float baseY1 = y2 - uy * arrowSize + perpY * (arrowSize / 2);

            float baseX2 = x2 - ux * arrowSize - perpX * (arrowSize / 2);
            float baseY2 = y2 - uy * arrowSize - perpY * (arrowSize / 2);

            // desenha o triângulo
            Path path = new Path();
            path.moveTo(tipX, tipY);
            path.lineTo(baseX1, baseY1);
            path.lineTo(baseX2, baseY2);
            path.close();

            canvas.drawPath(path, paint);

            //desenhar o texto para as opções de transição que possuo
            canvas.drawText(transicao.stringCharacteres(),(x1+x2)/2, (y1+y2)/2, textPaint);

            i++;
        }

    }

    public void addLista(Transicao transicao){
        transicoes.add(transicao);
        invalidate();
    }

    public void remover(Transicao transicao) {
        transicoes.remove(transicao);
        invalidate();
    }

    public void atualizarLig()
    {
        invalidate();
    }


}
