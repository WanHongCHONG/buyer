package com.harshit.androideatit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.harshit.androideatit.Common.Common;
import com.harshit.androideatit.Common.Config;
import com.harshit.androideatit.Database.Database;
import com.harshit.androideatit.Model.MyResponse;
import com.harshit.androideatit.Model.Notification;
import com.harshit.androideatit.Model.Order;
import com.harshit.androideatit.Model.Request;
import com.harshit.androideatit.Model.Sender;
import com.harshit.androideatit.Model.Token;
import com.harshit.androideatit.Remote.APIService;
import com.harshit.androideatit.ViewHolder.CartAdapter;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    APIService mService;

    //paypal payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)  //use sandbox because we test , change it late if going to production
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address,comment;

    //ctrl+o


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/restaurant_font.otf")
        .setFontAttrId(R.attr.fontPath)
        .build());

        setContentView(R.layout.activity_cart);

        //init paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Init Service
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");


        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new request
                if(cart.size()>0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty !!!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address : ");

        final LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);

        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);

        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //show paypal to payment
                //first, get address and comment from alert dialog
                address = edtAddress.getText().toString();
                comment = edtComment.getText().toString();

                String formatAmount = txtTotalPrice.getText().toString()
                                           .replace("$","")
                                           .replace(",","");

               // float amount = Float.parseFloat(formatAmount);

                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                        "USD",
                        "Eat it app order",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                startActivityForResult(intent,PAYPAL_REQUEST_CODE);

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    //ctrl + o


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null)
                {
                    try{
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);


                //create new request
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        txtTotalPrice.getText().toString(),
                        "0", //status
                        comment,
                        jsonObject.getJSONObject("response").getString("state"), //state from JSON
                        cart
                );

                //Submit to firebase
                //We will use System.CurrentMilli to key
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);
                //Delete cart
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);
                Toast.makeText(Cart.this, "Thank you , order placed", Toast.LENGTH_SHORT).show();
                finish();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            else if(resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotificationOrder(final String order_number) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true); //get all node with isServerToken is true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapshot.getValue(Token.class);

                    //create raw payload to send
                    Notification notification = new Notification("VIT","You have new order "+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    //only run when get result
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you , order placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    //ctrl+o

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {

        //we will remove item at List<Order> by position
        cart.remove(position);

        //after that we will delete all old data from sqlite
        new Database(this).cleanCart();

        //and finally, we will update new data from List<Order> to Sqlite
        for (Order item:cart)
            new Database(this).addToCart(item);
        //refresh
        loadListFood();
    }
}
