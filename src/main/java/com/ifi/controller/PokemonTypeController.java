package com.ifi.controller;

import com.ifi.annotation.Controller;
import com.ifi.bo.PokemonType;
import com.ifi.repository.PokemonTypeRepository;

import java.util.Map;

@Controller
public class PokemonTypeController {
    private PokemonTypeRepository repository = new PokemonTypeRepository();

    public PokemonType getPokemon(Map<String,String[]> parameters){
        // TODO
    }
}