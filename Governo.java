/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.webgenerator;

import fi.iki.elonen.NanoHTTPD;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import tk.artsakenos.iperunits.web.SuperUrl;
import tk.artsakenos.ultraanalysis.UltraGrammar.SuperGrammar;
import tk.artsakenos.ultraanalysis.UltraPOS.JavaPosTagger;
import tk.artsakenos.ultracms.UC_Helpers;
import tk.artsakenos.ultracms.UltraCMS;
import tk.artsakenos.ultracms.microservices.UCMS_Variables;
import tk.artsakenos.ultracms.microservices.UCMS_Connection;
import tk.artsakenos.ultracms.microservices.UCMS_Starter;

/**
 *
 * @author Andrea
 */
public class Governo {

    private final SuperGrammar super_grammar = new SuperGrammar();
    private final JavaPosTagger postagger = new JavaPosTagger();

    private final UltraCMS ultra_cms = new UltraCMS(8085, "ultracms_webgenerator.db") {
        @Override
        protected ArrayList<String> on_ultratag(String u_command, String u_key, String u_value, String u_html) {
            final ArrayList<String> result = new ArrayList<>();

            if (u_command.equals("REPLACE") && u_key.startsWith("GRAMMAR")) {
                String produced = UC_Helpers.escapeHTML(super_grammar.produce(u_value));
                produced = produced.substring(0, 1).toUpperCase() + produced.substring(1);
                result.add(produced);
            }

            if (u_command.equals("REPLACE") && u_key.startsWith("COMPAGINE")) {
                for (int i = 0; i < 5; i++) {
                    String nome_politicante = UC_Helpers.escapeHTML(super_grammar.produce("{nome_politicante}"));
                    String ruolo_politicante = UC_Helpers.escapeHTML(super_grammar.produce("{ruolo_politicante}"));
                    String membro_compagine = "<b>" + nome_politicante + "</b>, " + ruolo_politicante;
                    membro_compagine = "<li>" + membro_compagine + "</li>";
                    result.add(membro_compagine);
                }
            }

            if (u_command.equals("REPLACE") && u_key.startsWith("ANSA")) {
                String news = SuperUrl.get_page_content("https://newsapi.org/v2/top-headlines?country=it&apiKey=d07652ea05514dee91b7262d82a60d85&q=govern");
                String TAG_TITLE = "\"title\":";
                String TAG_DESCRIPTION = "\"description\":";
                String TAG_URL = "\"url\":";
                String title_raw = news.substring(news.indexOf(TAG_TITLE) + TAG_TITLE.length(), news.indexOf(TAG_DESCRIPTION));
                String description_raw = news.substring(news.indexOf(TAG_DESCRIPTION) + TAG_DESCRIPTION.length(), news.indexOf(TAG_URL));

                String title_rep = Governo_Helper.replace(title_raw);
                String description_rep = Governo_Helper.replace(description_raw);

                String title_lol = postagger.synonymize(title_raw);
                String description_lol = postagger.synonymize(description_raw);

                title_rep = UC_Helpers.escapeHTML(title_rep);
                description_rep = UC_Helpers.escapeHTML(description_rep);
                title_lol = UC_Helpers.escapeHTML(title_lol);
                description_lol = UC_Helpers.escapeHTML(description_lol);

                String response = "Dicono del nuovo governo: "
                        + "<b>" + title_rep + "</b>, " + description_rep + ", ovvero in parole povere \n"
                        + "<b>" + title_lol + "</b>, " + description_lol + "."
                        + "";
                result.add(response);
            }

            if (u_command.equals("ULTIMI_GOVERNI") && u_key.startsWith("NONE")) {
                LinkedHashMap<String, String> db_variabile_get = UCMS_Variables.db_variabile_get(database, "");
                for (String key : db_variabile_get.keySet()) {
                    if (!key.contains("::")) {
                        continue;
                    }
                    String slogan = UC_Helpers.escapeHTML(db_variabile_get.get(key));
                    key = key.substring(key.indexOf("::") + 2);
                    String output = "<b>" + UC_Helpers.escapeHTML(key) + "</b> ha creato <i>" + slogan + "</i>";
                    output = "<li>" + output + "</li>\n";
                    result.add(output);
                }
            }

            if (!result.isEmpty()) {
                return result;
            } else {
                return super.on_ultratag(u_command, u_key, u_value, u_html);
            }
        }

        @Override
        protected NanoHTTPD.Response on_serve(NanoHTTPD.IHTTPSession session, String uri, Map<String, String> parameters) {

            if (parameters.get("form_name") != null) {
                String name = parameters.get("form_name").trim();
                String luogo = parameters.get("form_luogo").trim();
                String slogan = parameters.get("form_slogan").trim();
                // String sessionKey = session.toString();
                if (name.length() > 3 && luogo.length() > 3 && slogan.length() > 3) {
                    String sessionKey = session.getHeaders().get("remote-addr");
                    String key = name + " da " + luogo;
                    String value = slogan;
                    UCMS_Variables.db_variable_put(database, sessionKey, key, value);
                }
            }

            if (uri.startsWith("/test_pippo")) {
                String news = SuperUrl.get_page_content("https://newsapi.org/v2/top-headlines?country=it&apiKey=d07652ea05514dee91b7262d82a60d85&q=govern");
                String TAG_TITLE = "\"title\":";
                String TAG_DESCRIPTION = "\"description\":";
                String TAG_URL = "\"url\":";
                String title_raw = news.substring(news.indexOf(TAG_TITLE) + TAG_TITLE.length(), news.indexOf(TAG_DESCRIPTION));
                String description_raw = news.substring(news.indexOf(TAG_DESCRIPTION) + TAG_DESCRIPTION.length(), news.indexOf(TAG_URL));

                String title_rep = Governo_Helper.replace(title_raw);
                String description_rep = Governo_Helper.replace(description_raw);

                String title_lol = postagger.synonymize(title_raw);
                String description_lol = postagger.synonymize(description_raw);
                String response = ""
                        + "<p>" + title_rep + "</p>\n"
                        + "<p>" + description_rep + "</p>\n"
                        + "<p>" + title_lol + "</p>\n"
                        + "<p>" + description_lol + "</p>\n"
                        + "";

                return RESPONSE_HTML(response);
            }

            if (uri.startsWith("/test_hash")) {
                LinkedHashMap<String, String> db_variabile_get = UCMS_Variables.db_variabile_get(database, "");
                String output = "";
                for (String key : db_variabile_get.keySet()) {
                    output += key + ":=" + db_variabile_get.get(key) + "\n";
                }
                return RESPONSE_TEXT(output);
            }

            return super.on_serve(session, uri, parameters);
        }

    };

    public Governo() {
        /**
         * Indico quale è la root
         */
        UCMS_Starter.ROOT_PATH = "/governo.html";

        /**
         * Inserisco i microservizi che mi servono
         */
        ultra_cms.microservice_add(UCMS_Starter.class);
        ultra_cms.microservice_add(UCMS_Connection.class);
        ultra_cms.microservice_add(UCMS_Variables.class);

        super_grammar.addGrammarResource(super_grammar, "/grammars/PoliBot.txt");
        super_grammar.addGrammarResource(super_grammar, "/grammars/{nome_proprio_maschile}.txt");
        super_grammar.addGrammarResource(super_grammar, "/grammars/{nome_proprio_femminile}.txt");
        super_grammar.addGrammarResource(super_grammar, "/grammars/{cognome}.txt");

        super_grammar.addGrammar("{nomepersona}:={nome_politicante}");
        super_grammar.addGrammar("{slogan}:={slogan_governo}");

    }

    public static void main(String[] args) {
        Governo pp = new Governo();
    }

}
