package com.example.sayedsalah.inventoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView productImgView = null;
    private TextView productNameTxtView = null, productQuantityTxtView = null, productPriceTxtView = null, supplierTxtView = null;
    private Button increaseBtn = null, decreaseBtn = null, orderBtn = null, deleteBtn = null;
    private Product product = null;
    private DatabaseHelper databaseHelper = null;
    private Intent mailIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("selectedProduct");

        databaseHelper = new DatabaseHelper(this);

        productImgView = (ImageView) findViewById(R.id.productImgView);
        productNameTxtView = (TextView) findViewById(R.id.productNameTxtView);
        productQuantityTxtView = (TextView) findViewById(R.id.productQuantityTxtView);
        productPriceTxtView = (TextView) findViewById(R.id.productPriceTxtView);
        supplierTxtView = (TextView) findViewById(R.id.supplierTxtView);


        productImgView.setImageBitmap(BitmapFactory.decodeFile(product.getProductImage()));
        productNameTxtView.setText(product.getProductName());
        productQuantityTxtView.setText(String.valueOf(product.getProductQuantity()));
        productPriceTxtView.setText(String.valueOf(product.getProductPrice()));
        supplierTxtView.append(product.getSupplierMail());

        increaseBtn = (Button) findViewById(R.id.increaseBtn);
        increaseBtn.setOnClickListener(this);

        decreaseBtn = (Button) findViewById(R.id.decreaseBtn);
        decreaseBtn.setOnClickListener(this);

        orderBtn = (Button) findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(this);

        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.increaseBtn:
                product.setProductQuantity(product.getProductQuantity() + 1);
                productQuantityTxtView.setText(String.valueOf(product.getProductQuantity()));
                databaseHelper.updateProduct(product.getProductId(), product.getProductQuantity());
                break;
            case R.id.decreaseBtn:
                if (product.getProductQuantity() > 0) {
                    product.setProductQuantity(product.getProductQuantity() - 1);
                    productQuantityTxtView.setText(String.valueOf(product.getProductQuantity()));
                    databaseHelper.updateProduct(product.getProductId(), product.getProductQuantity());
                }
                break;
            case R.id.orderBtn:
                mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.setType("text/plain");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{product.getSupplierMail()});
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order Product");
                mailIntent.putExtra(Intent.EXTRA_TEXT, "Product Name: " + product.getProductName() + "\n" +
                        "Product Quantity: " + 50 + "\n");
                startActivity(Intent.createChooser(mailIntent, "Choose app to send Mail"));
                break;
            case R.id.deleteBtn:
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to delete ?\n\n"+ product.getProductName())
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseHelper.deleteProduct(product.getProductId());
                                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
    }

}
