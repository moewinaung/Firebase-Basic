package com.mwa.base1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.*;
import android.view.*;
import com.google.firebase.auth.*;
import com.google.android.gms.tasks.*;
import java.util.*;
import com.google.firebase.database.*;
import android.content.*;

public class MainActivity extends AppCompatActivity {
    EditText input;
	ListView lv;
	ArrayAdapter<Message> adapter;
	List<Message> messages;

	private DatabaseReference db;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
 
		
		
			lv=(ListView)findViewById(R.id.lv);
			messages=new ArrayList<Message>();

			adapter=new MyAdapter(this,R.layout.message_item,messages);
			lv.setAdapter(adapter);

			input=(EditText)findViewById(R.id.etInput);
			db = FirebaseDatabase.getInstance().getReference().child("Messages");

			db.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {

						try{

							messages = new ArrayList<Message>();

							for (DataSnapshot dss: dataSnapshot.getChildren()) {

								String name = (String) dss.child("name").getValue();
								String msg = (String) dss.child("msg").getValue();
								long time = dss.child("time").getValue();

								messages.add(new Message(name,msg,time));

							}
							adapter=new MyAdapter(getApplicationContext(),R.layout.message_item,messages);
							lv.setAdapter(adapter);
							lv.setSelection(messages.size()-1);

						}catch(Exception e){
							Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
						}

					}

					@Override
					public void onCancelled(DatabaseError error) {
// Failed to read value
					}
				});
		}

		public void onClick(View v){
			String id = db.push().getKey();
			db.child(id).setValue(
				new Message("Moe Win Aung",input.getText().toString(),
							System.currentTimeMillis()));

// Clear the input
			input.setText("");
		}

		class Message{
			public String name="";
			public String msg="";
			public long time=0;

			public Message(String name,String msg,long curr){
				this.name=name;
				this.msg=msg;
				this.time=curr;
			}
		}

		class MyAdapter extends ArrayAdapter<Message>{

			public MyAdapter(Context context, int resource,List<Message> objects) {
				super(context, resource,messages);
			}

			public int getCount() {
				return super.getCount();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view=getLayoutInflater().inflate(R.layout.message_item,parent,false);

				TextView tvName=(TextView)view.findViewById(R.id.tvName);
				TextView tvMsg=(TextView)view.findViewById(R.id.tvMsg);
				tvName.setText(messages.get(position).name+ " : "+messages.get(position).time);
				tvMsg.setText(messages.get(position).msg);

				return view;
			}
		}

	}

