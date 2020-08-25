package edu.harvard.cs50.pokedex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.List;
/* A class from ReciclerView for show data at the screen and how elements show be showned */
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {  // PokeAdapter Class
    public static class PokedexViewHolder extends RecyclerView.ViewHolder {         // PokedexViewHolder Class
        public LinearLayout containerView;
        public TextView textView;

        PokedexViewHolder(View view) { //Constuctor
            super(view);    // Jus in case this class is doing something important?

            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);

            containerView.setOnClickListener(new View.OnClickListener() {   // reponds when a pokemon is clicked
                @Override
                public void onClick(View v) {   // Launch PokemonActivity
                    Pokemon current = (Pokemon) containerView.getTag(); // casting to Pokemon type
                    Intent intent = new Intent(v.getContext(), PokemonActivity.class);
                    intent.putExtra("url", current.getUrl());

                    v.getContext().startActivity(intent);   //Start activity on click
                }
            });
        }
    }
    /*Recall that an interface is just a list of methods that any class can implement*/
    private class PokemonFilter extends Filter {    // Method from Filterable
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // The argument to this method, constraint, will be whatever text the user has typed into the search bar,
            // which you can use for your filter. The performFiltering method should return an instance of FilterResults.
            // The performFiltering method should return an instance of FilterResults
            FilterResults results = new FilterResults();

            if (constraint.length() == 0 || constraint == null) {   // Return all Pokemons
                results.values = pokemon; // you need to create this variable!
                results.count = pokemon.size();
            } else {                    // Return Pokemons that match search
                List<Pokemon> filteredPokemon = new ArrayList<>();
                for (int i = 0; i < pokemon.size(); i++) {
                    if (pokemon.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredPokemon.add(pokemon.get(i));
                    }
                }
                results.values = filteredPokemon; // you need to create this variable!
                results.count = filteredPokemon.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) { // Method from Filterable
            filtered = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }

    }

    private List<Pokemon> pokemon = new ArrayList<>();
    private RequestQueue requestQueue;
    private List<Pokemon> filtered = pokemon;    // COPIA DE POKEMON?

    PokedexAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    public void loadPokemon() {     // Taking results from url and publishing in the screen as a list
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override   /* Am I overriding something from loadPokemon method? */
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        pokemon.add(new Pokemon(                                           // Adding Pokemons
                            name.substring(0, 1).toUpperCase() + name.substring(1), // Capitalizing  the fist letter of the Pokemon's name
                            result.getString("url")                                // url
                        ));
                    }

                    notifyDataSetChanged();
                } catch (JSONException e) {     // Exception
                    Log.e("cs50", "Json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon list error", error);
            }
        });

        requestQueue.add(request);
    }

    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  //Creating a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokedex_row, parent, false);  //Convert a XML into a JAVA object

        return new PokedexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) { // What is show at the moment in the screen
        Pokemon current = filtered.get(position);
        holder.textView.setText(current.getName());
        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return filtered.size(); // number of row to be shown
    }

    @Override   /*Recall that an interface is just a list of methods that any class can implement*/
    public Filter getFilter() {
        return new PokemonFilter();
    }
}
