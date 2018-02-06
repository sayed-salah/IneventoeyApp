package com.example.sayedsalah.inventoryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class InsertActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText nameEDtText = null, quantityEDtText = null, priceEDtText = null, supplierEDtText = null;
    private ImageView pickedImageView = null;
    private Button pickImageBtn = null, saveBtn = null;

    private Intent galleryIntent = null;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    private static final int RESULT_LOAD_IMAGE = 111;
    private String imageDecodableString;
    private boolean imagePicked = false;

    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEDtText = (EditText) findViewById(R.id.nameEDtText);
        quantityEDtText = (EditText) findViewById(R.id.quantityEDtText);
        priceEDtText = (EditText) findViewById(R.id.priceEDtText);
        supplierEDtText = (EditText) findViewById(R.id.supplierEDtText);

        pickedImageView = (ImageView) findViewById(R.id.pickedImageView);

        pickImageBtn = (Button) findViewById(R.id.pickImageBtn);
        pickImageBtn.setOnClickListener(this);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pickImageBtn:
                galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(ContextCompat.checkSelfPermission(InsertActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(InsertActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }else{
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }
                break;
            case R.id.saveBtn:
                if (checkDataValidation()) {
                    Product product = new Product();
                    product.setProductName(nameEDtText.getText().toString());
                    product.setProductQuantity(Integer.parseInt(quantityEDtText.getText().toString()));
                    product.setProductPrice(Integer.parseInt(priceEDtText.getText().toString()));
                    product.setProductImage(imageDecodableString);
                    product.setSupplierMail(supplierEDtText.getText().toString());
                    databaseHelper.insertProduct(product);
                    Toast.makeText(getBaseContext(), "Product Inserted Successfully.", Toast.LENGTH_SHORT).show();
                    nameEDtText.setText("");
                    quantityEDtText.setText("");
                    priceEDtText.setText("");
                    supplierEDtText.setText("");
                    pickedImageView.setImageBitmap(null);
                } else {
                    Toast.makeText(getBaseContext(), "One or more filed is invalid !!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean checkDataValidation() {
        String reg = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        try {
            if (!nameEDtText.getText().toString().isEmpty() &&
                    !quantityEDtText.getText().toString().isEmpty() &&
                    !priceEDtText.getText().toString().isEmpty() &&
                    !supplierEDtText.getText().toString().isEmpty() &&
                    imagePicked) {
                if (Pattern.matches(reg, supplierEDtText.getText().toString()) &&
                        Integer.parseInt(quantityEDtText.getText().toString()) != 0 &&
                        Integer.parseInt(priceEDtText.getText().toString()) != 0 ) {
                    return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK ) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageDecodableString = cursor.getString(columnIndex);
                cursor.close();
                pickedImageView.setImageBitmap(BitmapFactory.decodeFile(imageDecodableString));
                imagePicked = true;
            } else {
                imagePicked = false;
                Toast.makeText(this, "You haven't picked Image.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(getBaseContext(), "You can't pick image without permission.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}