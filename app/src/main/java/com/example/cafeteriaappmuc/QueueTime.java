package com.example.cafeteriaappmuc;

import com.example.cafeteriaappmuc.Objects.LineInfo;
import com.example.cafeteriaappmuc.Objects.QueueInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueTime {


    private static double estimateY;;
    private static List<Integer> XiList=new ArrayList<>();
    private static List<Double>YiList=new ArrayList<>();

    // Creating DatabaseReference. Get broadcast receiver name(campus and cafeteria spesific)
    // in order to find right info in database
    //TODO: change so it is cafeteria spesific. aka get name of beacon and add to databasepath

    public static void getWaitTime(String campus, String foodservice, final FirebaseCallback callback){
        //get info from database, number in line atm and training data Xi and Yi
        //cont checking
        //String Database_Path_test = ("Beacons/Alameda/Central Bar/");
        String Database_Path_correct = ("Beacons/"+campus+"/"+foodservice+"/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_correct);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Integer numberInLineX;
                LineInfo lineInfo = snapshot.child("line").getValue(LineInfo.class);  //current number of people in line
                numberInLineX = lineInfo.getNumberInLine();
                //System.out.println(("this is line length:"+numberInLineX));
                for (DataSnapshot postSnapshot : snapshot.child("trainingData").getChildren()) {
                    QueueInfo queueInfo = postSnapshot.getValue(QueueInfo.class);

                    if (queueInfo.getXi()!=null){
                        XiList.add(queueInfo.getXi());   //add number of people in line to list
                        YiList.add(queueInfo.getYi());  //add corresponding number of how long people stay in line to list


                    }


                }

                    if (YiList.size()>=2) {
                        //estimate waiting time for this person by calculating b1 and b2 and using number of people (X) in line
                        estimateY = (QueueAlgorithm.getB1(XiList, YiList) * numberInLineX) + QueueAlgorithm.getb0(XiList, YiList);

                        callback.onCallback(estimateY, numberInLineX);
                    }
                    else{

                        callback.onCallback(0, numberInLineX);
                    }

           }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }


    public interface FirebaseCallback {
        void onCallback(double waitTimeEstimate , Integer numInLine);
    }

    //update this when someone has left line
    public static void updateWaitingTime(int numberInLine, double diffTime, String campus, String foodservice, Boolean hasArrived){
        String Database_Path = ("Beacons/Alameda/Central Bar/");
        String Database_Path_correct = ("Beacons/"+campus+"/"+foodservice+"/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_correct);
        //add info on new person in line into to database to improve training data
        //calculate values


        //fictional time
        // diffTime= 2.4;
        if (hasArrived){
            Integer newLineNumber= numberInLine+1;
            Integer lineAtArrival= numberInLine;
            LineInfo newLineInfo= new LineInfo((newLineNumber));
            databaseReference.child("line").setValue(newLineInfo);
        }
        else {


            Integer newLineNumber= numberInLine-1;
            Integer lineAtArrival= numberInLine;
            QueueInfo newQueueInfo = new QueueInfo(lineAtArrival,diffTime);
            LineInfo newLineInfo= new LineInfo((newLineNumber));

            // Getting upload ID.
            String UploadId = databaseReference.push().getKey();

            databaseReference.child("trainingData").child(UploadId).setValue(newQueueInfo);
            databaseReference.child("line").setValue(newLineInfo);
        }





        // System.out.println(("this is "+estimateY));
    }



}
