package com.example.cafeteriaappmuc;

import com.example.cafeteriaappmuc.Objects.LineInfo;
import com.example.cafeteriaappmuc.Objects.QueueInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QueueTime {



    //private List<Integer> XiList= new ArrayList<>();
    //private List<Double> YiList= new ArrayList<>();

    private Double timeTest;
    private static Double estimateY;;
    private static List<Integer> XiList;
    private static List<Double>YiList;

    // Creating DatabaseReference. Get broadcast receiver name(campus and cafeteria spesific)
    // in order to find right info in database
    //TODO: change so it is cafeteria spesific. aka get name of beacon and add to databasepath

    public static Double getWaitTime(String campus, String foodservice){
        //get info from database, number in line atm and training data Xi and Yi
        //cont checking
        String Database_Path_test = ("Beacons/Alameda/Central Bar/");
        String Database_Path_correct = ("Beacons/"+campus+"/"+foodservice+"/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_test);

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
                        //System.out.println(("this is X mean:"+Xi));

                    }

                }

                if (YiList.size()>=2){

                    //estimate waiting time for this person by calculating b1 and b2 and using number of people (X) in line
                    estimateY = (QueueAlgorithm.getB1(XiList, YiList) * numberInLineX) + QueueAlgorithm.getb0(XiList, YiList);
                    System.out.println(("this is waiting time bitch" + estimateY));

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        // System.out.println(("this is waiting time bitch" + tempXiList));
        System.out.println(("this is waiting time bitch" + estimateY));
        return estimateY;
    }


    public void updateWaitingTime(Integer numberInLine, Double diffTime, String campus, String foodservice){
        String Database_Path = ("Beacons/Alameda/Central Bar/");
        String Database_Path_correct = ("Beacons/"+campus+"/"+foodservice+"/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        //add info on new person in line into to database to improve training data
        //calculate values
        Integer newPersonInLine= numberInLine+1;

        //fictional time
        // diffTime= 2.4;
        QueueInfo newQueueInfo = new QueueInfo(newPersonInLine,diffTime);

        // Getting upload ID.
        String UploadId = databaseReference.push().getKey();

        databaseReference.child("trainingData").child(UploadId).setValue(newQueueInfo);


        // System.out.println(("this is "+estimateY));
    }


}
