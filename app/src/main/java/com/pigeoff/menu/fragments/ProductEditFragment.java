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
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.Arrays;
import java.util.List;

public class ProductEditFragment extends BottomSheetDialogFragment {

    private static final String BUNDLE_ITEM = "bundleitem";
    private static final String BUNDLE_SECTION = "bundlesection";

    ProductEntity product;
    int section;
    OnEditListener listener;

    TextInputEditText editLabel;
    AutoCompleteTextView editSection;
    AutoCompleteTextView editDefaultUnit;
    MaterialButton buttonSubmit;

    public ProductEditFragment() {

    }

    public static ProductEditFragment newInstance(ProductEntity item, int section) {
        ProductEditFragment fragment = new ProductEditFragment();
        Bundle bundle = new Bundle();
        if (item != null) bundle.putString(BUNDLE_ITEM, item.toSerialize());
        bundle.putInt(BUNDLE_SECTION, section);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String itemJson = bundle.getString(BUNDLE_ITEM, null);
            if (itemJson != null) this.product = ProductEntity.toObject(itemJson);
            this.section = bundle.getInt(BUNDLE_SECTION, Constants.TAB_GROCERIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editLabel = view.findViewById(R.id.edit_label);
        editSection = view.findViewById(R.id.edit_section);
        editDefaultUnit = view.findViewById(R.id.edit_product_unit);
        buttonSubmit = view.findViewById(R.id.button_submit);

        List<String> sectionsTypes = Arrays.asList(Util.getSectionsLabel(requireContext()));
        List<String> unitTypes = Arrays.asList(Util.getUnitsLabel(requireContext()));

        editLabel.setText(product.label);
        editSection.setText(sectionsTypes.get(product.section), false);
        editDefaultUnit.setText(unitTypes.get(product.defaultUnit), false);

        editLabel.requestFocus();

        editSection.setText(sectionsTypes.get(section), false);

        buttonSubmit.setOnClickListener(v -> {
            if (String.valueOf(editLabel.getText()).isEmpty()) return;

            product.defaultUnit = unitTypes.indexOf(editDefaultUnit.getText().toString());
            product.section = sectionsTypes.indexOf(editSection.getText().toString());
            product.label = String.valueOf(editLabel.getText());

            if (listener != null) listener.onSubmit(product);
            dismiss();
        });
    }

    public void setOnEditListener(OnEditListener listener) {
        this.listener = listener;
    }

    public interface OnEditListener {
        void onSubmit(ProductEntity product);
    }


}
