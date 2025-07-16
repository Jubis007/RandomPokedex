package com.example.randompokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.randompokedex.databinding.ActivityMainBinding
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextPokemonButton.setOnClickListener {
            fetchRandomPokemonData()
        }
    }

    private fun fetchRandomPokemonData() {
        // 1. Create a client to make network requests
        val client = AsyncHttpClient()
        val randomId  = (1..1025).random()

        // 2. Define the API endpoint URL for a random dog image
        val url = "https://pokeapi.co/api/v2/pokemon/$randomId/"

        // 3. Make the GET request
        client.get(url, object : JsonHttpResponseHandler() {
            // This 'onSuccess' function is called when the API responds successfully
            override fun onSuccess(statusCode: Int, headers: okhttp3.Headers, json: JsonHttpResponseHandler.JSON) {
                // Get the Pokémon's name
                val pokemonName = json.jsonObject.getString("name").replaceFirstChar { it.uppercase() }

                // Get the Pokémon's image URL from the nested 'sprites' object
                val spriteObject = json.jsonObject.getJSONObject("sprites")
                val imageUrl = spriteObject.getString("front_default")

                // Get the Pokémon's type(s) from the 'types' array
                val typesArray = json.jsonObject.getJSONArray("types")
                val typesList = mutableListOf<String>()
                for (i in 0 until typesArray.length()) {
                    val typeObject = typesArray.getJSONObject(i).getJSONObject("type")
                    val typeName = typeObject.getString("name").replaceFirstChar { it.uppercase() }
                    typesList.add(typeName)
                }
                val pokemonTypes = typesList.joinToString(" / ")

                binding.pokemonNameTextView.text = pokemonName
                binding.pokemonTypeTextView.text = pokemonTypes

                Glide.with(this@MainActivity)
                    .load(imageUrl)
                    .fitCenter()
                    .into(binding.pokemonImageView)
            }

            // This 'onFailure' function is called when the API request fails
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Error", errorResponse)
            }
        })
    }
}