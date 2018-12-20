package com.example.yahov.kinveyandroiddatastore;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Book extends GenericJson {
    @Key("name")
    public String name;
}