package br.com.loja.service;

import br.com.loja.dto.WhatsAppDtos.SendMessageRequest;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class WhatsAppClient {

  private final RestClient restClient;
  private final boolean enabled;

  public WhatsAppClient(
      @Value("${whatsapp.api.url:}") String apiUrl,
      @Value("${whatsapp.api.token:}") String token,
      @Value("${whatsapp.api.enabled:false}") boolean enabled) {
    this.enabled = enabled;

    var requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(5));
    requestFactory.setReadTimeout(Duration.ofSeconds(5));

    var builder = RestClient.builder().requestFactory(requestFactory);
    if (apiUrl != null && !apiUrl.isBlank()) {
      builder.baseUrl(apiUrl);
    }
    if (token != null && !token.isBlank()) {
      builder.defaultHeader("Authorization", "Bearer " + token);
    }
    this.restClient = builder.build();
  }

  public boolean send(String phone, String message) {
    if (phone == null || phone.isBlank()) {
      log.warn("[WhatsApp] Telefone vazio, mensagem não enviada.");
      return false;
    }

    if (!enabled) {
      log.info("[WhatsApp MOCK] Mensagem para {}: {}", phone, message);
      return true;
    }

    try {
      String cleanPhone = PhoneNumbers.normalize(phone);

      var response = restClient.post()
          .contentType(MediaType.APPLICATION_JSON)
          .body(new SendMessageRequest(cleanPhone, message))
          .retrieve()
          .toBodilessEntity();

      log.info("WhatsApp enviado com sucesso para {}. Status: {}", cleanPhone, response.getStatusCode());
      return response.getStatusCode().is2xxSuccessful();

    } catch (Exception exception) {
      log.error("Erro ao enviar mensagem WhatsApp para {}: {}", phone, exception.getMessage());
      return false;
    }
  }
}
