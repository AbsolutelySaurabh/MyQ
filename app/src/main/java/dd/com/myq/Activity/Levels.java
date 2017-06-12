package dd.com.myq.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import dd.com.myq.R;

public class Levels extends Activity
{
    // Array of strings...
    ListView simpleList;
    String countryList[] = {"Beginner", "Intermediate", "Advanced", "Expert", "God of Coding"};

    @Override   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        simpleList = (ListView)findViewById(R.id.simpleListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview_levels, R.id.textView, countryList);
        simpleList.setAdapter(arrayAdapter);
    }
}