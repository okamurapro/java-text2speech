import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;



class TextToSpeech {

    // トークン取得用URL.
    private static final String TOKEN_URL = "https://try-api.recaius.jp/auth/v2/tokens";

    // リクエストURL
    private static final String REQUEST_URL = "https://try-api.recaius.jp/tts/v2/plaintext2speechwave";

    // リクエストHEADER
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";

    // リクエストBODY KEY.
    //トークン取得時.
    private static final String SPEECH_SYNTHESIS_KEY = "speech_synthesis";
    private static final String SPEECH_SYNTHESIS_SERVICE_ID_KEY = "service_id";
    private static final String SPEECH_SYNTHESIS_PASSWORD_KEY = "password";
    //テキスト送信時.
    private static final String ID_KEY = "id";
    private static final String PASSWORD_KEY = "password";
    private static final String PLAIN_TEXT_KEY = "plain_text";
    private static final String LANG_KEY = "lang";
    private static final String CODEC_KEY = "codec";
    private static final String SPEAKER_ID_KEY = "speaker_id";

    // リクエストBODY VALUE.
    private static final String PLAIN_TEXT_VALUE = "リカイアスのご利用ありがとうございます！わからないことがあればデベロッパーサイトかフォーラムで質問してくださいね。";
    private static final String LANG_VALUE = "ja_JP"; // 日本語
    private static final String SPEAKER_ID_VALUE = "ja_JP-F0005-U01T"; // モエ（女性）
    private static final String CODEC_LINEAR_PCM = "audio/x-linear"; // リニアPCM

    private static String serviceId = System.getenv("RECAIUS_TTS_ID"); // 環境変数から取得
    private static String servicePassword = System.getenv("RECAIUS_TTS_PASSWORD"); // 環境変数から取得
    
    public static void main (String[] args){

        HttpClient httpClient = HttpClientBuilder.create().build();

        String token = "";          //認証トークン.
        JsonObject requestBody;     //リクエストBODYを格納するJsonオブジェクト.
        HttpPost httpPost;          //POST通信用オブジェクト.
        HttpResponse response;      //レスポンス受け取り用.

        //***************************************************************************
        //　1.認証トークンの取得.
        //      ※APIver2.0からはRECAIUSサービスの利用時にこの認証トークンが必要となります。
        //***************************************************************************
        try
        {
            // リクエストBODYの設定.
            requestBody = Json.createObjectBuilder()
                .add(SPEECH_SYNTHESIS_KEY, Json.createObjectBuilder()
                    .add(SPEECH_SYNTHESIS_SERVICE_ID_KEY, serviceId)
                    .add(SPEECH_SYNTHESIS_PASSWORD_KEY, servicePassword)
                    .build())
                .build();

            // POSTの準備
            httpPost = new HttpPost(TOKEN_URL);    
            // HEADERの設定
            httpPost.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            // リクエストBODYの設定およびContentの文字コードをUTF-8に設定
            httpPost.setEntity(new StringEntity(requestBody.toString(),ContentType.create("text/plain", "UTF-8")));

            // POSTを実行
            System.out.println("*****************************************************");
            System.out.println("1.RECAIUS 認証トークンを取得します。");
            System.out.println("*****************************************************");
            response = httpClient.execute(httpPost);

            // Responseの結果を出力
            HttpEntity entity = response.getEntity();
            System.out.println("RECAIUS 認証トークン取得結果を表示します。");
            System.out.println("StatusCode  :" + response.getStatusLine().getStatusCode());               
            System.out.println("ContentLength  :" + entity.getContentLength());
            System.out.println("ContentType  :" + entity.getContentType());

            //Responseの解析とJSONのパース.
            String json_string = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            JsonReader jsonReader = Json.createReader(new StringReader(json_string));
            JsonObject jObj = jsonReader.readObject();

            //トークンの有効期限.
            System.out.println("トークン有効期限(expiry_sec) :" + jObj.getInt("expiry_sec"));

            //トークン文字列の保存.
            token = jObj.getString("token");
            System.out.println("トークン(token) :" + jObj.getString("token"));

            jsonReader.close();
        }
        catch(Exception e)
        {
            System.err.println("Error:  " + e );
        }

        //***************************************************************************
        //　2.テキストデータの音声認識.
        //***************************************************************************
        try {
            // リクエストBODYの設定
            requestBody = Json.createObjectBuilder()
                                .add(ID_KEY, serviceId)
                                .add(PASSWORD_KEY, servicePassword)
                                .add(PLAIN_TEXT_KEY,PLAIN_TEXT_VALUE)
                                .add(LANG_KEY,LANG_VALUE)
                                .add(CODEC_KEY,CODEC_LINEAR_PCM)
                                .add(SPEAKER_ID_KEY,SPEAKER_ID_VALUE).build();
            // POSTの準備
            httpPost = new HttpPost(REQUEST_URL);    
            // HEADERの設定
            httpPost.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            httpPost.setHeader("X-Token", token);
            // リクエストBODYの設定およびContentの文字コードをUTF-8に設定
            httpPost.setEntity(new StringEntity(requestBody.toString(),ContentType.create("text/plain", "UTF-8")));

            // POSTを実行.
            System.out.println("*****************************************************");
            System.out.println("2.RECAIUS 音声認識APIにリクエストを実行します。");
            System.out.println("*****************************************************");
            response = httpClient.execute(httpPost);

            // Responseの結果を出力
            System.out.println("RECAIUS 音声認識APIへのリクエストを実行結果を表示します。");
            System.out.println("StatusCode  :" + response.getStatusLine().getStatusCode());               
            System.out.println("ContentLength  :" + response.getEntity().getContentLength());
            System.out.println("ContentType  :" + response.getEntity().getContentType());
            // ReponseBodyの取得(音声合成の結果を取得）
            InputStream inputStream = response.getEntity().getContent();
            
            // AudioInputStream変換 BufferedInputStreamでラップしないとエラーになる
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
            
            // 取得した音声ファイルのフォーマットを確認
            System.out.println("取得した音声ファイルのフォーマットは下記のとおりです。");
            System.out.println("AudioFormat Encording: " + audioInputStream.getFormat().getEncoding());
            System.out.println("AudioFormat SampleRate: " + audioInputStream.getFormat().getSampleRate());
            System.out.println("AudioFormat SampleBit: " + audioInputStream.getFormat().getSampleSizeInBits());
            System.out.println("AudioFormat channel: " + audioInputStream.getFormat().getChannels());
            System.out.println("AudioFormat FrameRate: " + audioInputStream.getFormat().getFrameRate());
            System.out.println("AudioFormat FrameSize: " + audioInputStream.getFormat().getFrameSize());
            System.out.println("AudioFormat BigEndian or Not: " + audioInputStream.getFormat().isBigEndian());
            
            System.out.println("-------------------------------");
            System.out.println("音声再生の準備を開始します。");
            // 取得した音声合成の結果の書き込み先を、音声合成した結果のフォーマットと同じフォーマットで作成
            SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
            // 音声合成した結果のフォーマットと同じフォーマットで書き込み先をオープン
            sourceDataLine.open(audioInputStream.getFormat());                
            System.out.println("下記のバッファサイズに音声データを書き込みます。");
            System.out.println("Buffer Size: " + sourceDataLine.getBufferSize());
            // スタートイベントを発火
            sourceDataLine.start();
            // データの書き込み先を作成
            final byte[] data = new byte[sourceDataLine.getBufferSize()];
            int bytesRead;
            // 音声合成したファイルがすべて書き込みが行われるまで処理を継続
            System.out.println("再生を開始します。");
            while ((bytesRead = audioInputStream.read(data, 0, data.length)) != -1) {
                sourceDataLine.write(data, 0, bytesRead);
            }
            // 再生が完了されるまで待つ 
            sourceDataLine.drain();
            System.out.println("再生を停止します。");
            sourceDataLine.stop();
            System.out.println("システムリソースを解放します。");
            sourceDataLine.close();
            
        } catch (Exception e) {
            System.err.println("Error:  " + e );
        }

    }
}