package com.company.archapp;

import android.os.AsyncTask;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WikipediaClass {
    private String information, url, word;
    private TextView textview;

    //В функцию нужно передавать название достопримечательности и textview в каком будет показыватся информация про неё
    public void findWikipediaText(String word, TextView textview) {
        this.textview = textview;
        this.word = word;
        url = "https://en.wikipedia.org/wiki/" + word.replaceAll(" ", "_"); //Находим ссылку на Википедию
        MyTask mt = new MyTask();
        mt.execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;
            try {
                //Ищем html документ за url
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }

            //Если всё считалось, берем из документа нужный абзац
            if (doc != null) {
                Elements paragraphs = doc.select("p");
                information = paragraphs.get(1).text();
                //Забираем ненужную информацию
                int ind1 = information.indexOf(" is ");
                information = information.substring(ind1);
                while (information.contains("[")) {
                    ind1 = information.indexOf("[");
                    int ind2 = information.indexOf("]");
                    information = information.substring(0, ind1) + information.substring(ind2 + 1);
                }
                information = word + information;
            } else
                information = "Error";

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            textview.setText(information);
        }
    }
}