package edu.harvard.cs50.pokedex;

public class Pokemon {                      // Pokemon class
    private String name;
    private String url;

    Pokemon(String name, String url) {      // Constructor for Pokemon class
        this.name = name;
        this.url = url;
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
}
