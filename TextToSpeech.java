import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;


class TextToSpeech {
    // リクエストURL
    private static String REQUEST_URL = "https://try-api.recaius.jp/tts/v1/plaintext2speechwave";
    // リクエストHEADER
    private static String CONTENT_TYPE_KEY = "Content-Type";
    private static String CONTENT_TYPE_VALUE = "application/json";

    // リクエストBODY KEY
    private static String ID_KEY = "id";
    private static String PASSWORD_KEY = "password";
    private static String PLAIN_TEXT_KEY = "plain_text";
    private static String LANG_KEY = "lang";
    private static String CODEC_KEY = "codec";
    private static String SPEAKER_ID_KEY = "speaker_id";

    // リクエストBODY VALUE
    private static String PLAIN_TEXT_VALUE = "リカイアスのご利用ありがとうございます！わからないことがあればデベロッパーサイトかフォーラムで質問してくださいね。";
    private static String LANG_VALUE = "ja_JP"; // 日本語
    private static String SPEAKER_ID_VALUE = "ja_JP-F0005-U01T"; // モエ（女性）
    private static String CODEC_LINEAR_PCM = "audio/x-linear"; // リニアPCM
    private static String serviceId = System.getenv("RECAIUS_TTS_ID"); // 環境変数から取得
    private static String servicePassword = System.getenv("RECAIUS_TTS_PASSWORD"); // 環境変数から取得
    
    public static void main (String[] args){

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {

                // リクエストBODYの設定
                JsonObject requestBody = Json.createObjectBuilder()
                                    .add(ID_KEY, serviceId)
                                    .add(PASSWORD_KEY, servicePassword)
                                    .add(PLAIN_TEXT_KEY,PLAIN_TEXT_VALUE)
                                    .add(LANG_KEY,LANG_VALUE)
                                    .add(CODEC_KEY,CODEC_LINEAR_PCM)
                                    .add(SPEAKER_ID_KEY,SPEAKER_ID_VALUE).build();
                // POSTの準備
                HttpPost httpPost = new HttpPost(REQUEST_URL);    
                // HEADERの設定
                httpPost.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
                // リクエストBODYの設定およびContentの文字コードをUTF-8に設定
                httpPost.setEntity(new StringEntity(requestBody.toString(),ContentType.create("text/plain", "UTF-8")));
                // POSTを実行
                System.out.println("RECAIUS 音声認識APIにリクエストを実行します。");
                System.out.println("-------------------------------");
                HttpResponse response = httpClient.execute(httpPost);
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