package com.example.sayedsalah.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProductsAdapter.RecyclerViewClickListener {
    private DatabaseHelper databaseHelper = null;
    private ArrayList<Product> productArrayList = null;
    private RecyclerView recyclerView = null;
    private ProductsAdapter productsAdapter = null;
    private TextView emptyView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyView = (TextView) findViewById(R.id.emptyView);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        productArrayList = databaseHelper.readAllProducts();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        if (productArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            productsAdapter = new ProductsAdapter(MainActivity.this, productArrayList, MainActivity.this);
            recyclerView.setAdapter(productsAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.insertBtn:
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void recyclerViewItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Product product = productArrayList.get(position);
        intent.putExtra("selectedProduct", product);
        startActivity(intent);
    }

    @Override
    public void itemButtonClick(int position) {
        Product product = productArrayList.get(position);
        if (product.getProductQuantity() > 0) {
            product.setProductQuantity(product.getProductQuantity() - 1);
            databaseHelper.updateProduct(product.getProductId(), product.getProductQuantity());

            productArrayList.clear();
            productArrayList = databaseHelper.readAllProducts();
            if (productArrayList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);

                productsAdapter = null;
                productsAdapter = new ProductsAdapter(MainActivity.this, productArrayList, MainActivity.this);
                recyclerView.setAdapter(productsAdapter);
            }
        }
    }
}
