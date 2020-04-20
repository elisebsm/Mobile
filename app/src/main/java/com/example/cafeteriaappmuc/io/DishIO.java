package com.example.cafeteriaappmuc.io;

import androidx.annotation.NonNull;

import com.example.cafeteriaappmuc.Dish;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DishIO {

    static List<Dish> dishList = new ArrayList<>();
    static String dishPath = "Dishes/";
    static DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dishPath);

    public static void getAllDishes(String foodService, final FirebaseCallback callback) {
        DatabaseReference databaseReference = ref.child(foodService);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dishList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Dish dish = ds.getValue(Dish.class);
                    System.out.println(dish);
                    dishList.add(dish);
                }
                callback.onCallback(dishList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public interface FirebaseCallback {
        void onCallback(List<Dish> dishes);
    }

    public static void saveDish(String foodService, Dish dish) {
        DatabaseReference databaseReference = ref.child(foodService);
        String key = databaseReference.push().getKey();
        Map<String, Object> dishValues = dish.toMap();
        databaseReference.child(key).updateChildren(dishValues);
    }
}