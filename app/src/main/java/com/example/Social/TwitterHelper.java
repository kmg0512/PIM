package com.example.social;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by HotHaeYoung on 2016-06-06.
 */
public class TwitterHelper {
    final String CONSUMER_KEY = "Vbgot05yxb34L52AaPrB9XcJf";
    final String CONSUMER_SECRET = "2XjK9GB3q4rGtz2SVP8hNNyVk7oqSuW8ySjhA2cj1y5Hpd5xPi";

    public TwitterHelper() {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}
