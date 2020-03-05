package com.harshit.androideatit.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.harshit.androideatit.Common.Common;
import com.harshit.androideatit.Model.Token;

public class MyFirebaseIdService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if(Common.currentUser != null)
            updateTokenToFirebase(s);
    }

    private void updateTokenToFirebase(String s) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(s, false); // false because this token is send from client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}