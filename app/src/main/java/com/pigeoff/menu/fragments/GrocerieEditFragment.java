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

    public static final String BUNDLE_PRODUCT = "bundleproduct";

    public ProductEntity product;
    public TextInputEditText label;
    public TextInputEditText value;
    public AutoCompleteTextView unit;
    public MaterialButton submit;

    public OnGrocerieChoose listener;

    public GrocerieEditFragment() {

    }

    public static GrocerieEditFragment newInstance(ProductEntity product) {
        GrocerieEditFragment fragment = new GrocerieEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_PRODUCT, product.toSerialize());
        fragment.setArguments(bundle);
        return fragment;
    }

    public void addOnCallback(OnGrocerieChoose listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = bundle.getString(BUNDLE_PRODUCT, "");
            this.product = json.isEmpty() ? new ProductEntity() : ProductEntity.toObject(json);
        }
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
                itemValue = Float.parseFloat(String.valueOf(value.getText()));
            } catch (Exception e) {
                itemValue = 0.0f;
            }

            GroceryEntity item = new GroceryEntity();
            item.ingredientId = product.id;
            item.value = itemValue;
            item.unit = units.indexOf(unit.getText().toString());

            if (listener != null) listener.onGrocerieChoose(item);

            dismiss();
        });

    }

    public interface OnGrocerieChoose {
        void onGrocerieChoose(GroceryEntity item);
    }
}
