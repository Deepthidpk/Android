package com.example.sqllite;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private TextInputLayout tilName;
    private TextInputEditText etName;
    private TextView tvHeader;
    private ListView lvNames;

    private final List<String> shownNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // targetSdk 35+ draws edge-to-edge, so keep content clear of system bars.
        View root = findViewById(R.id.root);
        final int pad = root.getPaddingLeft();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(pad + bars.left, pad + bars.top, pad + bars.right, pad + bars.bottom);
            return windowInsets;
        });

        dbHelper = new DatabaseHelper(this);

        tilName = findViewById(R.id.tilName);
        etName = findViewById(R.id.etName);
        tvHeader = findViewById(R.id.tvHeader);
        lvNames = findViewById(R.id.lvNames);
        MaterialButton btnAdd = findViewById(R.id.btnAdd);
        MaterialButton btnDisplay = findViewById(R.id.btnDisplay);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shownNames);
        lvNames.setAdapter(adapter);

        // Clear the error as soon as the user starts fixing the input.
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                tilName.setError(null);
            }
        });

        btnAdd.setOnClickListener(v -> addName());
        btnDisplay.setOnClickListener(v -> displayAllNames());
    }

    private void addName() {
        String raw = etName.getText() == null ? "" : etName.getText().toString();
        NameValidator.Result result = NameValidator.validate(raw);

        if (!result.isValid()) {
            showError(messageFor(result.error));
            return;
        }

        if (dbHelper.nameExists(result.name)) {
            showError(getString(R.string.err_duplicate, result.name));
            return;
        }

        long id = dbHelper.addName(result.name);
        if (id == -1) {
            showError(getString(R.string.msg_add_failed));
            return;
        }

        tilName.setError(null);
        etName.setText("");
        hideKeyboard();
        Toast.makeText(this, getString(R.string.msg_added, result.name), Toast.LENGTH_SHORT).show();
    }

    private void displayAllNames() {
        List<String> names = dbHelper.getAllNames();

        shownNames.clear();
        shownNames.addAll(names);
        adapter.notifyDataSetChanged();
        hideKeyboard();

        if (names.isEmpty()) {
            tvHeader.setText(R.string.empty_list);
            Toast.makeText(this, R.string.msg_nothing_to_show, Toast.LENGTH_SHORT).show();
        } else {
            tvHeader.setText(getString(R.string.header_count, names.size()));
            Toast.makeText(this, getString(R.string.msg_loaded, names.size()), Toast.LENGTH_SHORT).show();
        }
    }

    private String messageFor(@NonNull NameValidator.Error error) {
        switch (error) {
            case EMPTY:
                return getString(R.string.err_empty);
            case TOO_SHORT:
                return getString(R.string.err_too_short, NameValidator.MIN_LENGTH);
            case TOO_LONG:
                return getString(R.string.err_too_long, NameValidator.MAX_LENGTH);
            case INVALID_CHARS:
            default:
                return getString(R.string.err_invalid_chars);
        }
    }

    private void showError(String message) {
        tilName.setError(message);
        etName.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = getCurrentFocus();
        if (imm != null && focus != null) {
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
