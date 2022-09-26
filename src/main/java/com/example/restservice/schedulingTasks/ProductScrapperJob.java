package com.example.restservice.schedulingTasks;

import java.util.*;
import java.text.*;
import java.io.IOException;

import org.jsoup.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Max;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import com.example.restservice.model.Product;
import com.example.restservice.repository.ProductRepository;


class ScraperThread extends Thread {
    private Product product;

    public static final String ACCOUNT_SID = "<Account_Id>";
    public static final String AUTH_TOKEN = "<Auth_Token>";

    public ProductRepository productRepository;

    public ScraperThread(Product product, ProductRepository productRepository) {
        this.product = product;
        this.productRepository = productRepository;
    }

    public void run() {
        Document doc;
        try {
            doc = Jsoup.connect(this.product.url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();

            Elements priceSpan = doc.select(".a-price-whole");
            Elements productTitleElem = doc.select("#productTitle");

            String productTitle = productTitleElem.first().text();
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            int currentPrice = 0;
            try {
                currentPrice = format.parse(priceSpan.first().text()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            
            if (product.prices.size() == 365) {
                product.prices.remove(0);
            }
            
            Integer flag = 0;

            for(Integer value: product.prices) {
                if (value <= currentPrice) {
                    flag = 1;
                    break;
                }
            }

            product.prices.add(currentPrice);
            this.productRepository.save(product);
            
            if (flag == 0) {

                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                for (String user: product.usersTracking) {
                    Message.creator(
                        new com.twilio.type.PhoneNumber(user),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        ("Price for your tracked product " + product.name + " has dropped to - " + currentPrice)
                    ).create();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

@Component
public class ProductScrapperJob {

    @Autowired
    public ProductRepository productRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void scrapeProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            ScraperThread thread = new ScraperThread(product, productRepository);
            thread.start();
        }
    }
}
