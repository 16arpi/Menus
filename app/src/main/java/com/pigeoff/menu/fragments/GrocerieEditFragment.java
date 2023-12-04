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
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.ProductViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GrocerieEditFragment extends BottomSheetDialogFragment {

    private int section = Constants.SECTION_DIVERS;
    private AutoCompleteTextView label;
    private TextInputEditText value;
    private AutoCompleteTextView unit;
    private OnGrocerieChoose listener;
    private ProductViewModel productViewModel;
    private ChipGroup chipGroup;

    public GrocerieEditFragment() {

    }

    public static GrocerieEditFragment newInstance(int section) {
        GrocerieEditFragment fragment = new GrocerieEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_SECTION, section);
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
        if (bundle != null) section = bundle.getInt(Constants.BUNDLE_SECTION, Constants.SECTION_DIVERS);
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
        chipGroup = view.findViewById(R.id.chip_group_filter);

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

        selectChip(chipGroup, section);

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

            int finalSection = getSelection(chipGroup);

            System.out.println("FINAL SELECTION" + String.valueOf(finalSection));
            if (listener != null) listener.onGrocerieChoose(finalSection, productLabel, item);

            dismiss();
        });

    }

    private void selectChip(ChipGroup group, int position) {
        int[] ids = {
                R.id.chip_filter_groceries,
                R.id.chip_filter_fruits,
                R.id.chip_filter_meat,
                R.id.chip_filter_fresh,
                R.id.chip_filter_drinks,
                R.id.chip_filter_divers
        };
        group.check(ids[position]);
    }

    private int getSelection(ChipGroup group) {
        int[] ids = {
                R.id.chip_filter_groceries,
                R.id.chip_filter_fruits,
                R.id.chip_filter_meat,
                R.id.chip_filter_fresh,
                R.id.chip_filter_drinks,
                R.id.chip_filter_divers
        };

        int selection = group.getCheckedChipId();
        for (int i = 0; i < ids.length; ++i) if (ids[i] == selection) return i;
        return 0;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Util.hideKeyboard(requireActivity());
        super.onDismiss(dialog);
    }

    public interface OnGrocerieChoose {
        void onGrocerieChoose(int section, String productLabel, GroceryEntity item);
    }
}
