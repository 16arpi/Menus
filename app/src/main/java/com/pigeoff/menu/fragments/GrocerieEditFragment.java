package com.pigeoff.menu.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.GroceryEntity;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.models.ProductViewModel;
import com.pigeoff.menu.util.Constants;
import com.pigeoff.menu.util.Util;
import com.pigeoff.menu.views.ChipCategories;

import java.util.List;
import java.util.stream.Collectors;

public class GrocerieEditFragment extends BottomSheetDialogFragment {

    private int section = Constants.SECTION_DIVERS;
    private AutoCompleteTextView label;
    private TextInputEditText quantity;
    private OnGrocerieChoose listener;
    private ProductViewModel productViewModel;
    private ChipCategories chipGroup;
    private HorizontalScrollView chipScroll;

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
        quantity = view.findViewById(R.id.edit_quantity);
        MaterialButton submit = view.findViewById(R.id.button_submit);
        chipGroup = view.findViewById(R.id.chip_group_filter);
        chipScroll = view.findViewById(R.id.chip_scroll);


        productViewModel.getItems().observe(getViewLifecycleOwner(), items -> {

            List<String> labels = items.stream().map(it -> it.label).collect(Collectors.toList());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    labels
            );

            label.setAdapter(adapter);

            label.setOnItemClickListener((parent, v, position, id) -> {
                TextView t = v.findViewById(android.R.id.text1);
                String ts = String.valueOf(t.getText());

                for (ProductEntity p : items) if (p.label.equals(ts)) {
                    chipGroup.check(p.section);
                }
            });

        });

        label.requestFocus();

        chipGroup.check(section);
        chipGroup.post(() -> {
            int chipId = chipGroup.getCheckedChipId();
            Chip chip = chipGroup.findViewById(chipId);
            chipScroll.scrollTo(chip.getLeft() - chip.getPaddingLeft(), chip.getTop());
        });

        submit.setOnClickListener(v -> {
            String productLabel = String.valueOf(label.getText());

            GroceryEntity item = new GroceryEntity();
            item.quantity = String.valueOf(quantity.getText());

            int finalSection = chipGroup.getSelectedSection();

            if (listener != null) listener.onGrocerieChoose(finalSection, productLabel, item);

            dismiss();
        });

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
