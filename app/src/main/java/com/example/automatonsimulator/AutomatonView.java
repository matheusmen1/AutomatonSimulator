package com.example.automatonsimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AutomatonView extends View
{
    private List<Estado> estadoList = new ArrayList<>();

    public AutomatonView(Context context) {
        super(context);
    }
    public AutomatonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        //para desenhar o circulo
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#2f2c79"));
        paint.setStyle(Paint.Style.FILL);

        //para desenhar a borda de estado final
        Paint paintBorda = new Paint();
        paintBorda.setColor(Color.WHITE);
        paintBorda.setStyle(Paint.Style.STROKE);
        paintBorda.setStrokeWidth(5);

        //para desenhar o texto
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        //para desenhar o triangulo de inicio
        Paint paintTriangulo = new Paint();
        paintTriangulo.setColor(Color.parseColor("#2f2c79"));
        paintTriangulo.setStyle(Paint.Style.FILL);

        int i = 0;
        while (i < estadoList.size())
        {
            Estado estado = estadoList.get(i);
            canvas.drawCircle(estado.getX(), estado.getY(), 70, paint);
            canvas.drawText(estado.getNum(), estado.getX(), estado.getY() + 15, textPaint);

            if(estado.getFim() == 1 && estado.getInicio() == 1)
            {
                float size = 50;
                Path path = new Path();
                path.moveTo(estado.getX() - 70, estado.getY());
                path.lineTo(estado.getX() - 70 - size, estado.getY() - size);
                path.lineTo(estado.getX() - 70 - size, estado.getY() + size);
                path.close();
                canvas.drawPath(path, paintTriangulo);
                canvas.drawCircle(estado.getX(), estado.getY(), 70, paintBorda);
            }
            else
            if (estado.getInicio() == 1)
            {
                float size = 50;
                Path path = new Path();
                path.moveTo(estado.getX() - 70, estado.getY());
                path.lineTo(estado.getX() - 70 - size, estado.getY() - size);
                path.lineTo(estado.getX() - 70 - size, estado.getY() + size);
                path.close();
                canvas.drawPath(path, paintTriangulo);
            }
            else
            if (estado.getFim() == 1)
            {
                canvas.drawCircle(estado.getX(), estado.getY(), 70, paintBorda);
            }


            i++;
        }

    }
    public void add(Estado estado)
    {
        estadoList.add(estado);
        invalidate();
    }
    public void remover(Estado estado)
    {
        estadoList.remove(estado);
        invalidate();
    }
    public void atualizarEstado(Estado estado, int i)
    {
        estadoList.set(i, estado);
        invalidate();
    }
}
