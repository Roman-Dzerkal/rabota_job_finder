package com.example.vacanciesparser;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Vacancy {
    public WebView webView;
    @FXML
    private TextField keyWordsLabel;

    private List<Integer> idsList;
    private static final Logger LOG = Logger.getLogger(Vacancy.class);

    @FXML
    protected void onHelloButtonClick() throws URISyntaxException, IOException {
        if (keyWordsLabel.getText().isEmpty()) {
            Notifications
                    .create()
                    .text("Please, type something in text field")
                    .showError();
            return;
        } else {
            String jsonAnswer = getVacancies(keyWordsLabel.getText());
            idsList = getIdVacancies(jsonAnswer);
            int amountVacancies = idsList.size();
            LOG.info("Found " + amountVacancies + " vacancies");
        }

        if (!idsList.isEmpty()) {
            for (int currentId : idsList) {
                parseIdVacancy(currentId);
            }
        }
    }

    @FXML
    void initialize() {
        WebEngine webEngine = webView.getEngine();
        webEngine.setUserAgent("job_finder");
        webEngine.load("https://pinta.com.ua/");
    }

    /**
     * Get all vacancies by entered keywords
     *
     * @param vacancyName name of the vacancy
     * @return JSON string with all vacancies
     * @throws IOException        is data returned
     */
    private String getVacancies(String vacancyName) throws IOException {
        String encodedVacancyName = URLEncoder.encode(vacancyName, StandardCharsets.UTF_8);
        HttpClient httpClient = HttpClients.createMinimal();
        HttpGet httpGet = new HttpGet(Config.HTTPS_API_RABOTA_UA + "vacancy/search?keyWords=" + encodedVacancyName);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        InputStream responseContent = httpResponse.getEntity().getContent();
        return new String(responseContent.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Get all ids of the vacancies
     *
     * @param jsonAnswer json string returned after post query
     * @return list of IDs of the vacancies
     */
    private List<Integer> getIdVacancies(String jsonAnswer) {
        idsList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonAnswer);
        JSONArray documents = jsonObject.getJSONArray("documents");
        for (int i = 0; i < documents.length(); i++) {
            idsList.add(documents.getJSONObject(i).getInt("id"));
        }
        return idsList;
    }

    /**
     * Parse information in current ID
     *
     * @param id current ID
     * @throws IOException        is data returned
     * @throws URISyntaxException is URL correct
     */
    private void parseIdVacancy(Integer id) throws IOException, URISyntaxException {
        HttpGet post = new HttpGet(Config.HTTPS_API_RABOTA_UA + "vacancy?id=" + id);
        HttpClient httpClient = HttpClients.createMinimal();
        HttpResponse httpResponse = httpClient.execute(post);
        InputStream responseContent = httpResponse.getEntity().getContent();
        String vacancyDescription = new String(responseContent.readAllBytes(), StandardCharsets.UTF_8);
        LOG.info(vacancyDescription + "\n\n");
    }
}