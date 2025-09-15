package com.example.automatonsimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TransicaoView extends View {
    public List<Transicao> transicoes = new ArrayList<>();
    private List<Integer> opostos;
    private static final float RAIO_ESTADO = 70f;

    public TransicaoView(Context context) {
        super(context);
    }

    public TransicaoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        opostos = new ArrayList<>(); // reset no começo

        //para desenhar a linha
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FA8072"));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);

        //para desenhar o triangulo ao final da linha
        Paint paintTriangulo = new Paint();
        paintTriangulo.setColor(Color.parseColor("#FA8072"));
        paintTriangulo.setStyle(Paint.Style.FILL);

        //para desenhar o texto
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int i = 0;
        while (i < transicoes.size()) {
            Transicao transicao = transicoes.get(i);

            //se não contém na lista, quer dizer que será uma aresta nova, seja oposta ou n
            if (!opostos.contains(i)) {
                int oposto = buscaOposto(i);

                if (oposto > -1) {
                    opostos.add(oposto);

                    // a->b (arco para cima)
                    desenharCurva(canvas, transicao, paint, paintTriangulo, textPaint, true);

                    // b->a (arco para baixo)
                    desenharCurva(canvas, transicoes.get(oposto), paint, paintTriangulo, textPaint, false);
                } else {
                    // apenas reta normal ou pra ele mesmo

                    if(transicao.getOrigem().equals(transicao.getDestino()))
                    {
                        //desenhar uma seta que vai e volta para ele mesmo
                        desenharSetaVoltando(canvas,transicao,paint,paintTriangulo, textPaint);
                    }
                    else
                    {
                        //apenas uma reta normal de um para outro
                        desenharLinhaReta(canvas, transicao, paint, paintTriangulo, textPaint);
                    }
                }
            }

            i++;
        }
    }

    /**
     * Desenhar uma seta em meia lua voltando para si mesmo, representando um estado
     *  onde a sua transição pode ocorrer para ele mesmo
     * */
    private void desenharSetaVoltando(Canvas canvas, Transicao t, Paint paint, Paint paintTriangulo, Paint textPaint) {
        float cx = t.getOrigem().getX(); // centro do estado
        float cy = t.getOrigem().getY();
        float r = RAIO_ESTADO; // já definido como 70 no seu código

        // Ponto inicial tangente no topo do círculo
        float startX = cx;
        float startY = cy - r;

        // Ponto final tangente (um pouco à esquerda do topo)
        float endX = cx - r * 0.7f;
        float endY = cy - r * 0.7f;

        // Ponto de controle para criar a barriga da curva
        float controlX = cx - r * 2f;
        float controlY = cy - r * 2f;

        // Criar path da curva
        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo(controlX, controlY, endX, endY);

        // Desenhar curva
        canvas.drawPath(path, paint);

        // Pegar posição/tangente no final da curva para alinhar a seta
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        float[] pos = new float[2];
        float[] tan = new float[2];
        pm.getPosTan(length, pos, tan);

        // Desenhar seta no final
        float arrowSize = 25f;
        float tipX = pos[0];
        float tipY = pos[1];

        // normalização tangente
        float norm = (float) Math.sqrt(tan[0] * tan[0] + tan[1] * tan[1]);
        float ux = tan[0] / norm;
        float uy = tan[1] / norm;

        // perpendicular
        float perpX = -uy;
        float perpY = ux;

        float baseX1 = tipX - ux * arrowSize + perpX * (arrowSize / 2);
        float baseY1 = tipY - uy * arrowSize + perpY * (arrowSize / 2);
        float baseX2 = tipX - ux * arrowSize - perpX * (arrowSize / 2);
        float baseY2 = tipY - uy * arrowSize - perpY * (arrowSize / 2);

        Path seta = new Path();
        seta.moveTo(tipX, tipY);
        seta.lineTo(baseX1, baseY1);
        seta.lineTo(baseX2, baseY2);
        seta.close();

        canvas.drawPath(seta, paintTriangulo);

        // Texto do rótulo
        canvas.drawText(t.stringCharacteres(), controlX, controlY - 10, textPaint);
    }

    /**
     * Desenha uma seta reta de a->b
     */
    private void desenharLinhaReta(Canvas canvas, Transicao t, Paint paint, Paint paintTriangulo, Paint textPaint) {
        float startX = t.getOrigem().getX();
        float startY = t.getOrigem().getY();
        float endX = t.getDestino().getX();
        float endY = t.getDestino().getY();

        // vetor direção
        double angle = Math.atan2(endY - startY, endX - startX);

        // recua o fim da linha para tangenciar o círculo destino
        startX += RAIO_ESTADO * Math.cos(angle);
        startY += RAIO_ESTADO * Math.sin(angle);
        endX -= RAIO_ESTADO * Math.cos(angle);
        endY -= RAIO_ESTADO * Math.sin(angle);

        // linha
        canvas.drawLine(startX, startY, endX, endY, paint);

        // seta
        desenharSeta(canvas, startX, startY, endX, endY, paintTriangulo);

        // rótulo no meio
        float midX = (startX + endX) / 2;
        float midY = (startY + endY) / 2 - 10;
        canvas.drawText(t.stringCharacteres(), midX, midY, textPaint);
    }

    /**
     * Desenha uma seta com curva, para diferenciar a ida e a volta de um
     *  estado para outro
     */
    private void desenharCurva(Canvas canvas, Transicao t, Paint paint, Paint paintTriangulo, Paint textPaint, boolean cima) {
        float startX = t.getOrigem().getX();
        float startY = t.getOrigem().getY();
        float endX = t.getDestino().getX();
        float endY = t.getDestino().getY();

        Path path = new Path();
        path.moveTo(startX, startY);

        // ponto de controle (quanto mais afastado, mais curva)
        float midX = (startX + endX) / 2;
        float midY = (startY + endY) / 2;
        float offset = 100; // controla a "altura" do arco

        if (cima) {
            path.quadTo(midX, midY - offset, endX, endY);
        } else {
            path.quadTo(midX, midY + offset, endX, endY);
        }

        // medir o caminho
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();

        // reduzir início e fim para tangenciar os círculos
        float[] pos = new float[2];
        Path recuado = new Path();
        pm.getSegment(RAIO_ESTADO, length - RAIO_ESTADO, recuado, true);

        // desenhar curva encurtada
        canvas.drawPath(recuado, paint);

        // seta tangente ao final
        desenharSetaCurva(canvas, recuado, paintTriangulo);

        // rótulo próximo ao meio da curva
        if (cima) {
            canvas.drawText(t.stringCharacteres(), midX, midY - offset - 10, textPaint);
        } else {
            canvas.drawText(t.stringCharacteres(), midX, midY + offset + 40, textPaint);
        }
    }

    /**
     * Desenha uma seta reta, representando unicamente a ida de a->b
     */
    private void desenharSeta(Canvas canvas, float startX, float startY, float endX, float endY, Paint paint) {
        double angle = Math.atan2(endY - startY, endX - startX);

        float arrowLength = 30;
        float arrowAngle = (float) Math.toRadians(25);

        float x1 = (float) (endX - arrowLength * Math.cos(angle - arrowAngle));
        float y1 = (float) (endY - arrowLength * Math.sin(angle - arrowAngle));

        float x2 = (float) (endX - arrowLength * Math.cos(angle + arrowAngle));
        float y2 = (float) (endY - arrowLength * Math.sin(angle + arrowAngle));

        Path path = new Path();
        path.moveTo(endX, endY);
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);
        path.close();

        canvas.drawPath(path, paint);
    }

    /**
     * Desenha um triangulo ao final de uma linha curva, para representar a seta
     *  quando existe a ida e a volta de um estado para outro
     */
    private void desenharSetaCurva(Canvas canvas, Path path, Paint paint) {
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();

        // pega ponto final e tangente
        float[] pos = new float[2];
        float[] tan = new float[2];
        pm.getPosTan(length, pos, tan);

        double angle = Math.atan2(tan[1], tan[0]);

        float arrowLength = 30;
        float arrowAngle = (float) Math.toRadians(25);

        float x1 = (float) (pos[0] - arrowLength * Math.cos(angle - arrowAngle));
        float y1 = (float) (pos[1] - arrowLength * Math.sin(angle - arrowAngle));

        float x2 = (float) (pos[0] - arrowLength * Math.cos(angle + arrowAngle));
        float y2 = (float) (pos[1] - arrowLength * Math.sin(angle + arrowAngle));

        Path seta = new Path();
        seta.moveTo(pos[0], pos[1]);
        seta.lineTo(x1, y1);
        seta.lineTo(x2, y2);
        seta.close();

        canvas.drawPath(seta, paint);
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

    private int buscaOposto(int i) {
        //ti sendo a transição (t) na posição 'i' -> ti
        Transicao ti = transicoes.get(i);
        for (int j = 0; j < transicoes.size(); j++) {
            if (i != j){
                Transicao teste = transicoes.get(j);
                if (teste.getOrigem().equals(ti.getDestino()) && teste.getDestino().equals(ti.getOrigem())) {
                    return j;
                }
            }
        }
        return -1;
    }
}
