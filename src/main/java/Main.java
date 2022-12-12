import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import java.util.UUID;

public class Main {

  public static void main(String[] args) {
    Mqtt3Client mqttClient3 = Mqtt3Client.builder()
            .identifier("modulesyncconsumer-" + UUID.randomUUID())
            .serverHost("localhost")
            .serverPort(1883)
            .automaticReconnectWithDefaultConfig()
            .build();
    mqttClient3.toAsync().connect();

    mqttClient3.toAsync().subscribeWith()
            .topicFilter("/topic")
            .callback(event -> new Main().consume(event))
            .send();
  }

  public void consume(Mqtt3Publish event) {
    throw new RuntimeException();
  }
}
