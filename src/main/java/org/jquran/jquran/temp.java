package org.jquran.jquran;


import static spark.Spark.*;

import java.io.FileWriter;
import java.net.URL;
import java.util.Scanner;

public class temp {

    public static void main(String[] args) {

        get("/print", (req, res) -> {

            String apiUrl = "https://api.quran.com/api/v4/verses/by_key/1:1";

            String json = new Scanner(new URL(apiUrl).openStream()).useDelimiter("\\A").next();

            System.out.println(json);

            return json;

        });
    }

}