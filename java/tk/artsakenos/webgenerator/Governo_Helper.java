/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.webgenerator;

import java.util.HashMap;

/**
 *
 * @author Andrea
 */
public class Governo_Helper {

    private static final HashMap<String, String> REPLACEMENTS = new HashMap<String, String>() {
        {
            put("renzi", "Mister Bean");
            put("5stelle", "5 sorelle");
            put("5 stelle", "5 sorelle");
            put("m5s", "Movimento 5 Sorelle");
            put(" lega", " Lega Padana");
            put("gentiloni", "Malvagioni");
            put("governo", "malgoverno");
            put("pd", "Partito a Delinquere");
            put("stato", "Stato Antigovernativo");
            put("partiti ", "giocatori di tombola ");

            put("mattarella", "Gargamella");
            put("salvini", "Gambadilegno");
            put("zingaretti", "Puffo Brontolone");
            put("di maio", "Puffo Elegante");
        }
    };

    public static String replace(String source) {
        source = source.toLowerCase();
        for (String key : REPLACEMENTS.keySet()) {
            source = source.replaceAll(key, REPLACEMENTS.get(key));
        }
        return source;
    }

}
