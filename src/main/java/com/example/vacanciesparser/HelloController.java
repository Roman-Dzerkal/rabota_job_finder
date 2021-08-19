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

public class HelloController {
    @FXML private TextField keyWordsLabel;
    private List<Integer> idList;
    private final Logger LOG = Logger.getLogger(HelloController.class);

    @FXML protected void onHelloButtonClick() throws URISyntaxException, IOException {
        String json;
        if (keyWordsLabel.getText().equals("")){
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setHeaderText("Please, type something in text field");
            alertError.showAndWait();
        } else {
            json = findJobs(keyWordsLabel.getText());
            //LOG.info(json);
            getVacancyIds(json);
        }

        if (idList.size() != 0) {
            for (Integer currentId : idList) {
                parseIdVacancy(currentId);
            }
        }
    }

    @FXML void initialize() {
    }

    private String findJobs(String keyWord) throws URISyntaxException, IOException {
        URL SERVER = new URL(API.SERVER + "vacancy/search");
        HttpPost post = new HttpPost(SERVER.toURI());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("keyWords", keyWord));
        post.setEntity(new UrlEncodedFormEntity(nvps));
        HttpClient httpClient = HttpClients.createMinimal();
        HttpResponse httpResponse = httpClient.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity);
    }

    private void getVacancyIds(String json)  {
        idList = new ArrayList<>();
        JSONObject obj = new JSONObject(json);
        JSONArray ja = obj.getJSONArray("documents");
        for (int t = 0 ; t < ja.length(); t++) {
            idList.add(ja.getJSONObject(t).getInt("id"));
        }
    }
    
    private void parseIdVacancy(Integer id) throws IOException, URISyntaxException {
        URL SERVER = new URL(API.SERVER + "vacancy?id=" + id);
        HttpGet post = new HttpGet(SERVER.toURI());
        HttpClient httpClient = HttpClients.createMinimal();
        HttpResponse httpResponse = httpClient.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        String vacancyDescription = EntityUtils.toString(httpEntity);
        JSONObject jo = new JSONObject(vacancyDescription);
        JSONArray ja = jo.optJSONArray("clusters");
        for (var t : ja) {
            LOG.info(t + "\n\n");
        }

    }

}