package by.matrosov.telegrambot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class TelegramBot extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message!=null && message.hasText()){
            if (message.getText().equals("/kurs")){
                String oil = getOil();
                SendMessage s = new SendMessage().setChatId(message.getChatId()).setText(oil);
                try {
                    execute(s);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getOil() {
        String result = "Нефть";
        try {
            JsonObject jsonObject = new Gson().fromJson(readStringFromUrl("https://www.quandl.com/api/v1/datasets/CHRIS/ICE_B1.json"), JsonObject.class);
            float oil = jsonObject.getAsJsonArray("data").get(0).getAsJsonArray().get(4).getAsFloat();
            result += " " + oil;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String readStringFromUrl(String url) throws IOException {
        try(InputStream in = new URL(url).openStream()){
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}
