package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Util;

import java.util.Arrays;
import java.util.List;

public class GrocerieEditFragment extends BottomSheetDialogFragment {

    public ProductEntity product;
    public TextInputEditText label;
    public TextInputEditText value;
    public AutoCompleteTextView unit;
    public MaterialButton submit;

    public OnGrocerieChoose listener;

    public GrocerieEditFragment(ProductEntity product, OnGrocerieChoose listener) {
        this.product = product;
        this.listener = listener;
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
        submit = view.findViewById(R.id.button_submit);

        List<String> units = Arrays.asList(Util.getUnitsLabel(requireContext()));

        label.setText(product.label);
        unit.setText(units.get(product.defaultUnit), false);

        value.requestFocus();

        submit.setOnClickListener(v -> {

            float itemValue;
            try {
                itemValue = Float.parseFloat(value.getText().toString());
            } catch (Exception e) {
                itemValue = 0.0f;
            }

            GroceryEntity item = new GroceryEntity();
            item.ingredientId = product.id;
            item.value = itemValue;
            item.unit = units.indexOf(unit.getText().toString());

            listener.onGrocerieChoose(item);

            dismiss();
        });

    }

    public interface OnGrocerieChoose {
        void onGrocerieChoose(GroceryEntity item);
    }
}
