package com.pigeoff.menu.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.ProductViewModel;
import com.pigeoff.menu.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GrocerieEditFragment extends BottomSheetDialogFragment {
    private AutoCompleteTextView label;
    private TextInputEditText value;
    private AutoCompleteTextView unit;
    private OnGrocerieChoose listener;
    private ProductViewModel productViewModel;

    public GrocerieEditFragment() {

    }

    public static GrocerieEditFragment newInstance() {
        return new GrocerieEditFragment();
    }

    public void addOnCallback(OnGrocerieChoose listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productViewModel = new ProductViewModel(requireActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_grocerie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        label = view.findViewById(R.id.edit_label);
        value = view.findViewById(R.id.edit_value);
        unit = view.findViewById(R.id.edit_unit);
        MaterialButton submit = view.findViewById(R.id.button_submit);

        List<String> units = Arrays.asList(Util.getUnitsLabel(requireContext()));

        productViewModel.getItems().observe(getViewLifecycleOwner(), items -> {

            List<String> labels = items.stream().map(it -> it.label).collect(Collectors.toList());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    labels
            );

            label.setAdapter(adapter);

            label.setOnItemClickListener((parent, v, pos, id) -> {
                System.out.println("SELECTION");
                TextView text = v.findViewById(android.R.id.text1);
                ProductEntity product = new ProductEntity();
                for (ProductEntity p : items) if (p.label.equals(text.getText().toString())) product = p;
                unit.setText(units.get(product.defaultUnit), false);
            });

        });

        label.requestFocus();

        submit.setOnClickListener(v -> {
            String productLabel = String.valueOf(label.getText());
            if (productLabel.isEmpty()) return;

            float itemValue;
            try {
                itemValue = Float.parseFloat(String.valueOf(value.getText()));
            } catch (Exception e) {
                itemValue = 0.0f;
            }

            GroceryEntity item = new GroceryEntity();
            item.value = itemValue;
            item.unit = units.indexOf(unit.getText().toString());


            if (listener != null) listener.onGrocerieChoose(productLabel, item);

            dismiss();
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Util.hideKeyboard(requireActivity());
        super.onDismiss(dialog);
    }

    public interface OnGrocerieChoose {
        void onGrocerieChoose(String productLabel, GroceryEntity item);
    }
}
