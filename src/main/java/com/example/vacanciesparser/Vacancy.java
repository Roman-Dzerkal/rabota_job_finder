package com.example.vacanciesparser;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Vacancy {
    @FXML private TextField keyWordsLabel;

    private List<Integer> idsList;
    private static final Logger LOG = Logger.getLogger(Vacancy.class);

    @FXML protected void onHelloButtonClick() throws URISyntaxException, IOException {
        if (keyWordsLabel.getText().equals("")){
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setHeaderText("Please, type something in text field");
            alertError.showAndWait();
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

    @FXML void initialize() {
    }

    /**
     * Get all vacancies by entered keywords
     * @param vacancyName name of the vacancy
     * @return JSON string with all vacancies
     * @throws URISyntaxException is URL correct
     * @throws IOException is data returned
     */
    private String getVacancies(String vacancyName) throws URISyntaxException, IOException {
        URL SERVER = new URL(Config.HTTPS_API_RABOTA_UA + "vacancy/search");
        HttpPost post = new HttpPost(SERVER.toURI());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("keyWords", vacancyName));
        post.setEntity(new UrlEncodedFormEntity(nvps));
        HttpClient httpClient = HttpClients.createMinimal();
        HttpResponse httpResponse = httpClient.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity);
    }

    /**
     * Get all ids of the vacancies
     * @param jsonAnswer json string returned after post query
     * @return list of IDs of the vacancies
     */
    private List<Integer> getIdVacancies(String jsonAnswer)  {
        idsList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonAnswer);
        JSONArray documents = jsonObject.getJSONArray("documents");
        for (int i = 0 ; i < documents.length(); i++) {
            idsList.add(documents.getJSONObject(i).getInt("id"));
        }
        return idsList;
    }

    /**
     * Parse information in current ID
     * @param id current ID
     * @throws IOException is data returned
     * @throws URISyntaxException is URL correct
     */
    private void parseIdVacancy(Integer id) throws IOException, URISyntaxException {
        URL SERVER = new URL(Config.HTTPS_API_RABOTA_UA + "vacancy?id=" + id);
        HttpGet post = new HttpGet(SERVER.toURI());
        HttpClient httpClient = HttpClients.createMinimal();
        HttpResponse httpResponse = httpClient.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        String vacancyDescription = EntityUtils.toString(httpEntity);
        LOG.info(vacancyDescription + "\n\n");
    }
}