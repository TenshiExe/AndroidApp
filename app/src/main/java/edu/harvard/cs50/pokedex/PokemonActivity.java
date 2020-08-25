package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        loadPokemon();
        loadImage();
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

    public void loadPokemon() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {           // Reading and parsing data from url
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
        Log.i("cs50", "The current Pokemon is \"" + currentPokemonName + "\"");
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
        Log.i("cs50", "Data saved");
    }
    public void loadImage() {
        final ImageView imageView = findViewById(R.id.spirit);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Map<String ,String> dic = response.getJSONObject("sprites");
                    //HashMap<String,Object> result = new ObjectMapper().readValue(response.getJSONObject("sprites"), HashMap.class);
                    //JSONObject json = new JSONObject();
                }
                catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        });
    }
}
