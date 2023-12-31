package com.pigeoff.menu.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.pigeoff.menu.BuildConfig;
import com.pigeoff.menu.R;
import com.pigeoff.menu.database.ProductEntity;
import com.pigeoff.menu.database.RecipeEntity;
import com.pigeoff.menu.models.RecipesViewModel;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class ImportExport<T> {
    private final T context;
    private final RecipesViewModel model;
    private ActivityResultLauncher<String[]> launcherCallback;

    public ImportExport (T context, RecipesViewModel model) {
        this.context = context;
        this.model = model;

        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            this.launcherCallback = activity.registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
                if (result != null) {
                    try {
                        String doc = readTextFromUri(activity, result);
                        Export object = deserialize(doc);
                        addProductsAndRecipes(object);

                    } catch (IOException e) {
                        riseError(activity);
                    }
                }
            });
        } else if (context instanceof Fragment) {
            Fragment fragment = (Fragment) context;
            this.launcherCallback = fragment.registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
                if (result != null) {
                    try {
                        String doc = readTextFromUri(fragment.requireContext(), result);
                        Export object = deserialize(doc);
                        addProductsAndRecipes(object);

                    } catch (IOException e) {
                        riseError(fragment.requireContext());
                    }
                }
            });
        }
    }

    public void export(List<ProductEntity> products, List<RecipeEntity> recipes) {
        List<RecipeEntity> absolutePictureRecipes = turnImagePathToDataURI(recipes);
        Export export = new Export(BuildConfig.VERSION_CODE, products, absolutePictureRecipes);
        Gson gson = new Gson();
        String string = gson.toJson(export);

        Context localContext = getContext(this.context);

        try {
            File outputDir = localContext.getFilesDir();
            File outputFile = File.createTempFile("export", ".json", outputDir);
            Uri uri = FileProvider.getUriForFile(localContext, "com.pigeoff.menu.fileprovider", outputFile);

            FileOutputStream inputStream = new FileOutputStream(outputFile);
            inputStream.write(string.getBytes(Charset.defaultCharset()));
            inputStream.close();

            if (uri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setDataAndType(uri, localContext.getContentResolver().getType(uri));
                localContext.startActivity(Intent.createChooser(shareIntent, null));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void open() {
        launcherCallback.launch(new String[]{"application/json"});
    }

    private List<RecipeEntity> turnImagePathToDataURI(List<RecipeEntity> source) {
        for (RecipeEntity r : source) {
            if (r.picturePath == null || r.picturePath.isEmpty()) continue;
            try {
                byte[] content = FileUtils.readFileToByteArray(new File(r.picturePath));
                r.picturePath = Base64.encodeToString(content, Base64.DEFAULT);

            } catch (Exception e) {

            }
        }
        return source;
    }

    private Context getContext(T parent) {
        if (parent instanceof AppCompatActivity) {
            return (AppCompatActivity) parent;
        } else if (parent instanceof Fragment) {
            return ((Fragment) parent).getContext();
        } else {
            return null;
        }
    }

    private String readTextFromUri(Context context, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private void riseError(Context context) {
        Toast.makeText(context, R.string.import_failed, Toast.LENGTH_SHORT).show();
    }

    private Export deserialize(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Export.class);
    }

    private void addProductsAndRecipes(Export export) {
        model.importRecipes(export.products, export.recipes, () -> {
            if (this.context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) this.context;
                activity.runOnUiThread(new FailureToast(activity));
            } else if (this.context instanceof Fragment) {
                Fragment fragment = (Fragment) this.context;
                fragment.requireActivity().runOnUiThread(new FailureToast(fragment.requireContext()));
            }
        });
    }

    private static class FailureToast implements Runnable {
        Context context;

        public FailureToast(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            Toast.makeText(context, R.string.import_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
