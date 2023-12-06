package com.pigeoff.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;
import com.pigeoff.menu.views.ChipCategories;

import java.util.Arrays;
import java.util.List;

public class ProductEditFragment extends BottomSheetDialogFragment {

    private static final String BUNDLE_ITEM = "bundleitem";
    private static final String BUNDLE_SECTION = "bundlesection";

    ProductEntity product;
    int section;
    OnEditListener listener;

    TextInputEditText editLabel;
    ChipCategories editSection;
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
            this.section = bundle.getInt(BUNDLE_SECTION, Constants.SECTION_GROCERIES);
        } else {
            this.section = Constants.SECTION_GROCERIES;
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
        editSection = view.findViewById(R.id.chip_group_filter);
        buttonSubmit = view.findViewById(R.id.button_submit);

        editLabel.setText(product.label);
        editSection.check(section);

        editLabel.requestFocus();

        buttonSubmit.setOnClickListener(v -> {
            if (String.valueOf(editLabel.getText()).isEmpty()) return;

            product.section = editSection.getSelectedSection();
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
