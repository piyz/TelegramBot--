package by.matrosov.telegrambot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class TelegramBot extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message!=null && message.hasText()){
            if (message.getText().equals("/oil")){
                String oil = getOil();
                SendMessage s = new SendMessage().setChatId(message.getChatId()).setText(oil);
                try {
                    execute(s);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(message.getText().equals("/cb")){
                String cb = getCb();
                SendMessage s = new SendMessage().setChatId(message.getChatId()).setText(cb);
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

    private String getCb(){
        String result = "ЦБ";
        try {
            String s = readStringFromUrl("http://www.cbr.ru/scripts/XML_daily.asp");
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = db.parse(new InputSource(new StringReader(s)));

            Element root = (Element) document.getElementsByTagName("ValCurs").item(0);
            String data = root.getAttribute("Date");
            NodeList valutes = root.getElementsByTagName("Valute");

            String usd = "";
            String eur = "";
            String byn = "";

            for (int i = 0; i < valutes.getLength(); i++) {
                Element valute = (Element) valutes.item(i);
                if (valute.getAttribute("ID").equals("R01235")){
                    usd = valute.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
                }else if (valute.getAttribute("ID").equals("R01239")){
                    eur = valute.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
                }else if (valute.getAttribute("ID").equals("R01090B")){
                    byn = valute.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
                }
            }

            result += " курс на сегодня " + data +
                    "\nДоллар: " +  usd +
                    "\nЕвро: " + eur +
                    "\nЗайчик: " + byn;

        } catch (IOException | ParserConfigurationException | SAXException e) {
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
        return "lavashik_bot";
    }

    @Override
    public String getBotToken() {
        return "473333096:AAG3eYrU_jI5g3fxqs4GgTOOXJXtJnmJ54w";
    }
}
