package dd.com.myq.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import dd.com.myq.App.Config;
import dd.com.myq.R;
import dd.com.myq.Util.Questions.Question;
import dd.com.myq.Util.Questions.QuestionAdapter;
import dd.com.myq.Util.SessionManager;

public class HomeFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private  ArrayList<String> al = new ArrayList<String>();
    // ArrayList<String> al_text = new ArrayList<String>();
    private ArrayList<String> al_id = new ArrayList<String>();
    private ArrayList<String> al_correctAns = new ArrayList<String>();

    private static int index;

    private  static int flag=0;

    private static int another_flag=0;

    int mBackStackSize = 0;

    ArrayAdapter<String> arrayAdapter;


    private int i;

    List<Question> questions;

    public  SwipeFlingAdapterView flingContainer;

    private LoaderManager.LoaderCallbacks<List<Question>> mCallbacks;

    private  LayoutInflater inflater=null;

    //SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getContext());

    //private CardContainer mCardContainer;
    Context mContext;

    public  QuestionAdapter questionAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String user_id;
    SessionManager currentSession;

    private static final int QUESTION_LOADER_ID = 1;

    private TextView emptyTextView;

    private static final String LOG_TAG = HomeFragment.class.getName();

    private static String QUESTIONS_REQUEST_URL = "http://myish.com:10010/api/questions/";

    private TextView mEmptyStateTextView;

    private OnFragmentInteractionListener mListener;

    View view;

    public HomeFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        currentSession = new SessionManager(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        HashMap<String, String> user_details = currentSession.getUserDetails();

        user_id = user_details.get(SessionManager.KEY_UID);

        QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;

        CallAPI call = new CallAPI();
        call.execute();

        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item, R.id.helloText, al);

        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                al_id.remove(0);
                al_correctAns.remove(0);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
//                 makeToast(getContext(), "Left!");

                    String correctness;

                    if (al_correctAns.get(index).equals("NO")) {

                        correctness = "Correct";
//                        Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();

                    } else {

                        correctness = "incorrect";
//                        Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();
                    }

//                flingContainer.getTopCardListener().selectLeft();
                    Log.d("getTop() : ", String.valueOf(flingContainer.getTop()));

                    Log.d(" al_id : ", al_id.get(index));

                    Log.d(" Questions : ", al.get(index));

                    Log.d(" correctA : ", al_correctAns.get(index));


                    AddQuestion(user_id, al_id.get(index), al.get(index), "", al_correctAns.get(index), "2", correctness);
                    AddUserToQuestion(user_id, al_id.get(index), al_correctAns.get(index));

            }

            @Override
            public void onRightCardExit(Object dataObject) {

//              makeToast(getContext(), "Right!");
                    String correctness;

                    if (al_correctAns.get(index).equals("YES")) {

                        correctness = "Correct";
//                        Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();

                    } else {

                        correctness = "incorrect";
//                        Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();
                    }

//                flingContainer.getTopCardListener().selectRight();

                    Log.d(" al_id : ", al_id.get(index));

                    Log.d(" Questions : ", al.get(index));

                    Log.d(" correctA : ", al_correctAns.get(index));

                    AddQuestion(user_id, al_id.get(index), al.get(index), "", al_correctAns.get(index), "2", correctness);
                    AddUserToQuestion(user_id, al_id.get(index), al_correctAns.get(index));

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //al.add("XML ".concat(String.valueOf(i)));
                // arrayAdapter.notifyDataSetChanged();
                if(flag==10)
                {
                    CallAPI call = new CallAPI();
                    call.execute();
                    flag=0;
                }
            }
            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();

            }

        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //makeToast(getA "Clicked!");
            }
        });

        Button button_left = (Button) view.findViewById(R.id.left);
        button_left.setBackgroundResource(R.drawable.false_btn);

        button_left.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

//                another_flag = 1;
//
//                String correctness;
//
//                if(al_correctAns.get(index).equals("NO")){
//
//                    correctness = "Correct";
////                    Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();
//
//                }else{
//
//                    correctness = "incorrect";
////                    Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();
//                }

                flingContainer.getTopCardListener().selectLeft();


//                AddQuestion( user_id,al_id.get(index),al.get(index), "",al_correctAns.get(index),  "2" ,  correctness );
//                AddUserToQuestion(user_id, al_id.get(index), al_correctAns.get(index));

                //index++;

            }
        });

        Button button_right = (Button) view.findViewById(R.id.right);

        button_right.setBackgroundResource(R.drawable.true_btn);

        button_right.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

//                another_flag = 1;
//
//                String correctness;
//
//                if(al_correctAns.get(index).equals("YES")){
//
//                    correctness = "Correct";
////                    Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();
//
//                }else{
//
//                    correctness = "incorrect";
////                    Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT).show();
//                }

                flingContainer.getTopCardListener().selectRight();

//                AddQuestion( user_id,al_id.get(index),al.get(index), "",al_correctAns.get(index),  "2" , correctness  );
//                AddUserToQuestion(user_id, al_id.get(index), al_correctAns.get(index));

                //index++;

            }
        });

        ButterKnife.inject(getActivity());

        return view;
    }


    public void AddQuestion(String userid,String questionid, String questiontext, String questionimage, String questioncorrectanswer, String questionpoints, String answercorrectness){


        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("questionid", questionid);
        requestParams.put("questiontext", questiontext);
        requestParams.put("questionimage", questionimage);
        requestParams.put("questioncorrectanswer", questioncorrectanswer);
        requestParams.put("questionpoints", questionpoints);
        requestParams.put("answercorrectness", answercorrectness);

        client.post(getActivity(), Config.AddQuestionUrl , requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("ResponsePoint Success",response.toString());            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }

        });
    }


    public void AddUserToQuestion(String uid,String qid, String questioncorrectanswer){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", uid);
        requestParams.put("qid", qid);
        requestParams.put("answer", questioncorrectanswer);

        client.post(getActivity(), Config.AddQuestionToUser, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());

            }

        });
    }


    public class CallAPI extends AsyncTask<URL, Integer, List<Question>> {

        public CallAPI(){
            super();

        }

        @Override
        protected List<Question> doInBackground(URL... params) {

            URL url = createUrl(QUESTIONS_REQUEST_URL);

            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            questions = extractFeatureFromJson(jsonResponse);

            return questions;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        private URL  createUrl(String stringUrl){

            URL url = null;
            try{
                url = new URL(stringUrl);

            }catch(MalformedURLException e){

                Log.e(LOG_TAG,"Error with creating URL",e);
            }
            return url;
        }

        private String makeHttpRequest(URL url)throws IOException{

            String jsonResponse = "";

            if(url==null){

                return jsonResponse;
            }
            HttpURLConnection urlConnection =null;
            InputStream inputStream = null;
            try{

                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                if(urlConnection.getResponseCode()==200){

                    inputStream = urlConnection.getInputStream();

                    jsonResponse = readFromStream(inputStream);
                    Log.d("Response",""+jsonResponse);
                }else{

                    Log.e(LOG_TAG,"Error Response code: "+urlConnection.getResponseCode());

                }

            }catch(IOException e){
                Log.e(LOG_TAG,"Problem retrieving the news JSON results. ",e);

            }finally{

                if(urlConnection!=null){

                    urlConnection.disconnect();;
                }
                if(inputStream!=null){
                    inputStream.close();
                }
            }
            return jsonResponse;

        }

        private String readFromStream(InputStream inputStream) throws IOException{

            StringBuilder output = new StringBuilder();
            if(inputStream!=null){

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while(line!=null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();

        }

        private List<Question> extractFeatureFromJson(String questionsJSON) {


            if (TextUtils.isEmpty(questionsJSON)) {
                return null;
            }

            // Create an empty ArrayList that we can start adding questions to
            List<Question> questions = new ArrayList<>();

            try {

                JSONArray questionsArray = new JSONArray(questionsJSON);

                //for each question create a json object
                for(int i=0;i<questionsArray.length();i++){

                    JSONObject currentNews = questionsArray.getJSONObject(i);

                    String text = currentNews.getString("text");

                    String level = currentNews.getString("level");

                    String id = currentNews.getString("_id");

                    String correctAnswer = currentNews.getString("correctAnswere");
                    // String imageUrl = currentNews.getString("imageUrl");

                    Question newquestion = new Question(text, level, correctAnswer, id);
                    questions.add(newquestion);

                }

            } catch (JSONException e) {

                Log.e("QueryUtils", "Problem parsing the Questions JSON results", e);
            }

            return questions;
        }

        @Override
        protected void onPostExecute(List<Question> questions) {

           Question question;
            String text;
            String id;
            String correctAns;

            if(questions!=null) {

                for (int i = 0; i < questions.size(); i++) {

                    question = questions.get(i);

                    text = question.getText();
                    //level = question.getLevel();
                    id = question.getId();

                    correctAns = question.getCorrectAnswere();


                    //String final_text = text+"!@#"+id;

                    al_id.add(id);

                    al.add(text);

                    al_correctAns.add(correctAns);

                    Log.d("Add To List", "al_id: "+id+"  al: "+ text+ "  al_correctAns "+correctAns);

                   // al.add(final_text);


                }

                Log.d(" IDs size : ",String.valueOf(al_id.size()));
                Log.d(" Questions size : ",String.valueOf(al.size()));
                Log.d(" CorrectAns size : ",String.valueOf(al_correctAns.size()));

                flag=10;

            }
            arrayAdapter.notifyDataSetChanged();

        }


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}