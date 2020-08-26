package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

//import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonActivity extends AppCompatActivity {    // Second screen CLass named PokemonActivity to show details for each pokemon
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private String url_d;
    private RequestQueue requestQueue;
    //private SharedPreferences sharedPreferences;
    //private SharedPreferences.Editor editor;

    private boolean caught;
    //private final String key = "CATCH_KEY";

    private String currentPokemonName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Creating ids */
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);

        //final Button button = (Button) findViewById(R.id.button_catch);   // get the button

        loadPokemonData();
        Log.i("TEST","1ro - después de loadPokemon");
        loadImage();
        Log.i("TEST","2ro - despues de loadImage");
        //loadDescription();
        //Log.i("TEST","3ro - despues de loadDescription");
        Log.i("cs50-OnCreate()","url: " + url_d);


        // load capture pokemon capture state
        /*
        if (currentPokemonName.isEmpty())
        {
            Log.i("cs50", "POKEMON NAME EMPTY");
        }
        caught = getPreferences(Context.MODE_PRIVATE).getBoolean(currentPokemonName, false);
        Log.i("cs50", "Pokemon" + currentPokemonName + "status is " + caught);
        if (caught) {
            button.setText("Release");
            Log.i("cs50", "onCreate, Button text is \"Release\"");
        } else {
            button.setText("Catch");
            Log.i("cs50", "onCreate, Button text is \"Catch\"");
        }

         */
    }

    public void loadPokemonData() {
        type1TextView.setText("");
        type2TextView.setText("");
        Log.i("cs50-loadPokemonData()","Cargar Pokemons");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {           // Reading and parsing data from url
                    url_d = "https://pokeapi.co/api/v2/pokemon-species/" + Integer.toString(response.getInt("id"));   // NEW URL
                    Log.i("cs50-loadPokemonData()","url: " + url_d);

                    currentPokemonName = response.getString("name");    //SAVED CURRENT POKEMON NAME
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));

                    final Button button = (Button) findViewById(R.id.button_catch);   // get the button

                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        } else if (slot == 2) {
                            type2TextView.setText(type);
                        }

                        // Recalling the capture Pokemon status
                        caught = getPreferences(Context.MODE_PRIVATE).getBoolean(currentPokemonName, false);
                        //Log.i("cs50", "Pokemon" + currentPokemonName + "status is " + caught);
                        if (caught) {
                            button.setText("Release");
                            //Log.i("cs50", "onCreate, Button text is \"Release\"");
                        } else {
                            button.setText("Catch");
                            //Log.i("cs50", "onCreate, Button text is \"Catch\"");
                        }
                        loadDescription();
                    }
                } catch (JSONException e) {     // Exception
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);
    }

    //Map<String,Boolean> pokemonCaptureList = new HashMap<>(); //List of Pokemons that have been captured

    public void toggleCatch(View view) {    // When the button is pressed...
        final Button button = (Button) findViewById(R.id.button_catch);   // get the button
        //Log.i("cs50", "The current Pokemon is \"" + currentPokemonName + "\"");
        if (!caught) {  // If not capture
            Log.i("cs50", "Caught");
            button.setText("Release");
            // add the pokemon catch to the list
            //pokemonCaptureList.put(currentPokemonName, true);
        } else {        // If is already been captured
            Log.i("cs50", "Released");
            button.setText("Catch");
            // remove pokemon from pokemonCaptureList
            //pokemonCaptureList.remove(currentPokemonName);
        }
        caught = !caught;   //toggle capture status

        // saving pokemon capture status
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(currentPokemonName, caught).commit();
        //Log.i("cs50", "Data saved");
    }

    public void loadImage() {   // lead sprites
        //final ImageView imageView = findViewById(R.id.sprite);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject sprites = response.getJSONObject("sprites");
                    String defaultSprite = sprites.getString("front_default");
                    new DownloadSpriteTask().execute(defaultSprite); // you need to get the url!
                    //Log.i("cs50","LOADING IMAGES");

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
                //error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {  // Asynchronous task
        @Override
        protected Bitmap doInBackground(String... strings) { //... means that zero or more String objects (or an array of them) may be passed as the argument(s) for this method
            try {
                URL url = new URL(strings[0]);
                //Log.i("cs50","DO IN BACKGROUND");
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }
        final ImageView imageView = findViewById(R.id.sprite);
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into the ImageView!
            imageView.setImageBitmap(bitmap);
            //Log.i("cs50","POST EXCECUTE");
        }
    }

    public void loadDescription() {   // lead sprites
        Log.i("cs50-loadDescription","url: " + url_d);
        final TextView textView = findViewById(R.id.description);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url_d, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response_d) {
                try {
                    JSONArray descriptionEntries = response_d.getJSONArray("flavor_text_entries");
                    JSONObject text_en = descriptionEntries.getJSONObject(0);
                    textView.setText(text_en.getString("flavor_text"));
                    Log.i("cs50",text_en.getString("flavor_text"));
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
                //error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }
}


