package com.example.automatonsimulator; // Certifique-se que o pacote está correto

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;

public class MultipleRunDialogFragment extends DialogFragment {

    public interface MultipleRunListener {
        void onTestRequested(List<String> inputs, List<EditText> inputFields);
    }

    private MultipleRunListener listener;
    private LinearLayout entriesContainer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // ⭐ CORREÇÃO 1: Usar getTargetFragment() que é o par correto de setTargetFragment()
            listener = (MultipleRunListener) getTargetFragment();
        } catch (ClassCastException e) {
            // A mensagem de erro agora fica mais clara
            throw new ClassCastException(getTargetFragment().toString() + " must implement MultipleRunListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_multiple_run, null);
        entriesContainer = view.findViewById(R.id.entries_container);

        Button btnAdd = new Button(getContext());
        btnAdd.setText("+");
        btnAdd.setOnClickListener(v -> addNewInputField());

        Button btnTest = view.findViewById(R.id.btnTest);
        btnTest.setOnClickListener(v -> runTests());

        // Adiciona o primeiro campo de entrada
        addNewInputField();

        entriesContainer.addView(btnAdd);



        return new AlertDialog.Builder(getActivity())
                .setTitle("Multiple Run")
                .setView(view)
                .setPositiveButton("Close", null)
                .create();
    }

    /**
     * ⭐ NOVO MÉTODO: onStart()
     * Este método é chamado DEPOIS do onCreateDialog e garante que a janela (Window) já existe.
     * É o lugar certo para configurar o teclado.
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            // Ajusta a janela para redimensionar quando o teclado aparecer
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void addNewInputField() {
        EditText input = new EditText(getContext());
        input.setHint("Input");

        int buttonIndex = entriesContainer.getChildCount() - 2;
        entriesContainer.addView(input, buttonIndex);

        // ⭐ CORREÇÃO 2: A lógica do teclado agora é mais segura e explícita
        // Usamos post() para garantir que a view foi adicionada antes de pedir o foco.
        input.post(() -> {
            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void runTests() {
        // (Este método não precisou de alterações)
        List<String> inputs = new ArrayList<>();
        List<EditText> inputFields = new ArrayList<>();

        for (int i = 0; i < entriesContainer.getChildCount(); i++) {
            View child = entriesContainer.getChildAt(i);
            if (child instanceof EditText) {
                inputs.add(((EditText) child).getText().toString());
                inputFields.add((EditText) child);
            }
        }
        listener.onTestRequested(inputs, inputFields);
    }
}