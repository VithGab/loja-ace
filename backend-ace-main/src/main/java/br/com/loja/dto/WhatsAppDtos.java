package br.com.loja.dto;

public final class WhatsAppDtos {
  private WhatsAppDtos() {}

  public record SendMessageRequest(String phone, String message) {}

  public record WhatsAppMessageRequest(String phone, String message) {}

  public record SendMessageResponse(boolean success, String messageId) {}
}
