package com.rmit.android_tiramisu_vacation_rental.helpers.firebase;

import android.util.Log;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class FirebaseAccessToken {
    private static final String TAG = "FCMAccessToken";
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"tiramisu-vacation-rental\",\n" +
                    "  \"private_key_id\": \"c27ba2ed0096116082af50d5341d7913fbaa1704\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDIT0xnboTRi4EJ\\nqVc9aIv6Id67bDYbNOKbRCb32p295c8IcF9vFbzKjFn3Kg/zMB1JkC/qn316ejc/\\nrvu0w1BQ8BSMfx2ZmQCwI7Qtj4yMgiXlXuE3WxQleKHoqxTDpqv22F0gSf+W+dHa\\nkbU4QI6xQO8WheekGT0fktDAV0lGqDTwkEF4XZHbu8VE2ZW0GmWZ97BwaFH3jZdL\\n4WagVCRHsp7Yh33OSd7e6aZZu8yUsFJ5l5LUqSk5TDapNzHI3xQNCZThua22Qahw\\npMz3TB6c9HFxRF2/t8ZMSPiJoxYFFmN7u/ot1NJj8qixJ3R6AA7rgdqG7Rqrvr1w\\n0vKwJkrTAgMBAAECggEANjPvEtX/Lsy3ICwopT9TEV507PZ9NaN75lPsz4jiwm4b\\nNcdiq6aQLacI+S7ynfXZFvzjWnPJ+ohzxtHM3r8zTpOIm/QRyGx0mTUwHlwHxkW5\\nh3XDL71/mBwqg9E3jzHcurEVkhU0CmCTfreK+CvP8PsHN2sIoYkK1y5dOo7kDzAd\\nmejUF0psy4OmTTYorUgcU3Obgy6EzWp7gMnP8DIAZ/gtyE5ksX8N1XOU2GkqqJBV\\nVoY4hnJsUhg7CKPC4mLBKW38ArGZFzrSK07dW4v9pNeOVR5NQxBYF8FCAKn5MPtH\\nIvGEIFjsORbf6I6l8bVGwjsmsG4uOQRhqWgM7OtNEQKBgQD/8XPy6+ADU8KSiN9C\\no9N92J4PyU6NEPs531w3h7Ca1Ss24x9dn/5FyuCg0DB6I3nxA7MkquR7SyX7f4C2\\njE5BAFVrGB3HjkTSYTfVhElOtudBA+I4YaMlkHKwSKMgyaf8aSsFIvIgS+HzDPsY\\nzSc8SwsEiB5R4K/5+AKV1/Mo9wKBgQDIWq741CuA/dGeWnHS04VUjEHS5+pzHBvs\\nJO4Ihjv/GPZcfWMWc++f06lAff7gqka5ITDdp1gGwlgL3AnAo/y448bhOYTc7zMQ\\nlIGoViY4QXCpeVsRKrl+dlqLUpUqdOgmfBSm1SuO3wAJWMhmLdqxURHrDGS7PECk\\nnJ5EO3vyBQKBgQC/kV0E/PHN67qd7V6WRYL+Fc2w6S8nPQedOIaQdbqFGXrf+T23\\n2RH78S8SjuFJ+hXChM9oaZTch9HlHXkcqYXE91f38bQQcncmtGeTJBGung4XRq3j\\nH4l6i1Sch7G5z9YxghIWJSLvc3yqbBPha2Srp0uW179B75gn1C8zMtCo3wKBgQDD\\nX8roUt3pd5oFtWTxldqay/sUU5tIjMydxpVoMp4m8IytPIh3yK6PSLPvc+4r31//\\n/+HLA/jO/o0lY3kPJdq34UZ7Mr/hQlClhmu5X+j9SrM2UGUBpIwmSfuZiPp04HPR\\ns4Xf+lfzhnpeovkoyuZLqyax8u/3tZXD8AZVuzvukQKBgQCMRxZgE1ldkNI5BJhQ\\nUvIfY50iwK0coHIhP89eKF77r1Zw/5RB/AfGNv09DfY27lfBgHpUqIJD2+gR9p2H\\nQ+OyiEP0rfdFa6N53elqAaed+bYQlfu5Jfq/NrGULzVVyfuDY/BC9E+9DzGSNlUT\\nY0k26e39NfNjt2VTQf9YD8LjNw==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-ddqlw@tiramisu-vacation-rental.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"116141670429262266658\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-ddqlw%40tiramisu-vacation-rental.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes());

            List<String> firebaseScopes = List.of(firebaseMessagingScope);

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream);
            GoogleCredentials scoped = googleCredentials.createScoped(firebaseScopes);

            Log.d(TAG, scoped.toString());
            scoped.refresh();
            AccessToken accessToken = scoped.getAccessToken();
            Log.d(TAG, accessToken.toString());

            Log.d(TAG, accessToken.getTokenValue());
            return accessToken.getTokenValue();
        } catch (IOException e) {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }
}
