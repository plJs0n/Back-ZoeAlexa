package com.zoealexa.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Service para envío de emails
 * VERSIÓN SIMPLIFICADA - SIN THYMELEAF (texto plano)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String emailFrom;

    @Value("${app.mail.from-name}")
    private String emailFromName;

    /**
     * Enviar email de recuperación de contraseña con código
     * VERSIÓN TEXTO PLANO (sin template HTML)
     *
     * @param destinatario Email del destinatario
     * @param nombreUsuario Nombre del usuario
     * @param codigoRecuperacion Código de 6 dígitos
     * @param vigenciaMinutos Minutos de vigencia del código
     */
    public void enviarCodigoRecuperacion(String destinatario, String nombreUsuario,
                                         String codigoRecuperacion, int vigenciaMinutos) {
        log.info("Enviando código de recuperación a: {}", destinatario);

        try {
            // Crear mensaje de texto plano
            String mensaje = String.format(
                    "═══════════════════════════════════════%n" +
                            "  RECUPERACIÓN DE CONTRASEÑA - ZOEALEXA%n" +
                            "═══════════════════════════════════════%n%n" +
                            "Hola %s,%n%n" +
                            "Has solicitado restablecer tu contraseña en ZoeAlexa.%n%n" +
                            "Tu código de verificación es:%n%n" +
                            "    ┌─────────┐%n" +
                            "    │  %s  │%n" +
                            "    └─────────┘%n%n" +
                            "⏱️  IMPORTANTE: Este código expira en %d minutos.%n%n" +
                            "ℹ️  Si no solicitaste este cambio, ignora este mensaje.%n" +
                            "   Tu cuenta permanecerá segura.%n%n" +
                            "───────────────────────────────────────%n" +
                            "Saludos,%n" +
                            "Equipo ZoeAlexa%n" +
                            "Sistema de Reservas Fluviales%n" +
                            "───────────────────────────────────────%n",
                    nombreUsuario,
                    codigoRecuperacion,
                    vigenciaMinutos
            );

            // Crear mensaje
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(emailFrom, emailFromName);
            helper.setTo(destinatario);
            helper.setSubject("Recuperación de Contraseña - ZoeAlexa");
            helper.setText(mensaje, false); // false = texto plano

            // Enviar email
            mailSender.send(mimeMessage);

            log.info("Email de recuperación enviado exitosamente a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error al enviar email de recuperación a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error al enviar email de recuperación", e);
        } catch (Exception e) {
            log.error("Error inesperado al enviar email: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email", e);
        }
    }

    /**
     * Enviar email de bienvenida
     *
     * @param destinatario Email del destinatario
     * @param nombreUsuario Nombre del usuario
     */
//    public void enviarBienvenida(String destinatario, String nombreUsuario) {
//        log.info("Enviando email de bienvenida a: {}", destinatario);
//
//        try {
//            String mensaje = String.format(
//                    "═══════════════════════════════════════%n" +
//                            "  BIENVENIDO A ZOEALEXA%n" +
//                            "═══════════════════════════════════════%n%n" +
//                            "Hola %s,%n%n" +
//                            "¡Gracias por registrarte en ZoeAlexa!%n%n" +
//                            "Tu cuenta ha sido creada exitosamente y ya puedes%n" +
//                            "disfrutar de nuestro sistema de reservas de transporte%n" +
//                            "fluvial.%n%n" +
//                            "Si tienes alguna pregunta, no dudes en contactarnos.%n%n" +
//                            "───────────────────────────────────────%n" +
//                            "Saludos,%n" +
//                            "Equipo ZoeAlexa%n" +
//                            "───────────────────────────────────────%n",
//                    nombreUsuario
//            );
//
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(
//                    mimeMessage,
//                    StandardCharsets.UTF_8.name()
//            );
//
//            helper.setFrom(emailFrom, emailFromName);
//            helper.setTo(destinatario);
//            helper.setSubject("Bienvenido a ZoeAlexa");
//            helper.setText(mensaje, false);
//
//            mailSender.send(mimeMessage);
//
//            log.info("Email de bienvenida enviado exitosamente a: {}", destinatario);
//
//        } catch (MessagingException e) {
//            log.error("Error al enviar email de bienvenida a {}: {}", destinatario, e.getMessage());
//            // No lanzar excepción para no bloquear el registro del usuario
//        }
//    }
//
//    /**
//     * Enviar email simple
//     *
//     * @param destinatario Email del destinatario
//     * @param asunto Asunto del email
//     * @param mensaje Contenido del mensaje
//     */
//    public void enviarEmailSimple(String destinatario, String asunto, String mensaje) {
//        log.info("Enviando email simple a: {}", destinatario);
//
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(
//                    mimeMessage,
//                    StandardCharsets.UTF_8.name()
//            );
//
//            helper.setFrom(emailFrom, emailFromName);
//            helper.setTo(destinatario);
//            helper.setSubject(asunto);
//            helper.setText(mensaje, false);
//
//            mailSender.send(mimeMessage);
//
//            log.info("Email simple enviado exitosamente a: {}", destinatario);
//
//        } catch (MessagingException e) {
//            log.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
//            throw new RuntimeException("Error al enviar email", e);
//        }
//    }
}