package dd.com.myq.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import dd.com.myq.R;

public class Categories extends Activity
{
    // Array of strings...
    ListView simpleList;
    String countryList[] = {"Category 1", "Category 2", "Category 3", "Category 4", "Category 5","Category 6"};

    @Override   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      setContentView(R.layout.activity_levels);
        simpleList = (ListView)findViewById(R.id.simpleListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview_categories, R.id.textView, countryList);
        simpleList.setAdapter(arrayAdapter);
    }
}