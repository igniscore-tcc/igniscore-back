package com.igniscore.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRecoveryLink(String recipient, String token) {
        String recoveryLink = "https://front-end-tcc-lovat.vercel.app/reset-password?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject("Recuperação de Senha - Igniscore");
            helper.setFrom("Igniscore <andersongamasilva08@gmail.com>");

            String htmlContent = buildHtmlTemplate(recoveryLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail de recuperação", e);
        }
    }

    public void sendVerificationCode(String recipient, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject("Verificação de E-mail - Igniscore");
            helper.setFrom("Igniscore <andersongamasilva08@gmail.com>");

            String htmlContent = buildVerificationHtmlTemplate(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail de verificação", e);
        }
    }

    private String buildHtmlTemplate(String url) {
        String logoUrl = "https://front-end-tcc-lovat.vercel.app/_next/image?url=%2Figniscore.png&w=48&q=75";

        return "<div style=\"font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #f9f9f9; padding: 40px 10px; color: #333;\">" +
                "  <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); overflow: hidden;\">" +

                "    <div style=\"background-color: #6D28D9; padding: 25px; text-align: center;\">" +
                "      <img src=\"" + logoUrl + "\" alt=\"Igniscore Logo\" style=\"max-height: 50px; display: inline-block; vertical-align: middle;\" />" +
                "    </div>" +

                "    <div style=\"padding: 40px 30px; line-height: 1.6;\">" +
                "      <h2 style=\"color: #1a1a2e; margin-top: 0; font-size: 20px;\">Olá,</h2>" +
                "      <p style=\"font-size: 16px; color: #555;\">Você recebeu este e-mail porque solicitou a redefinição de senha para a sua conta no Igniscore.</p>" +
                "      <p style=\"font-size: 16px; color: #555;\">Clique no botão abaixo para escolher uma nova senha de acesso. Este link é válido por apenas 15 minutos.</p>" +
                "      " +

                "      <div style=\"text-align: center; margin: 35px 0;\">" +
                "        <a href=\"" + url + "\" style=\"background-color: #FF5A1F; color: #ffffff; padding: 14px 30px; font-size: 16px; font-weight: bold; border-radius: 5px; text-decoration: none; display: inline-block; box-shadow: 0 4px 6px rgba(255, 90, 31, 0.2);\">Redefinir Minha Senha</a>" +
                "      </div>" +
                "      " +
                "      <p style=\"font-size: 14px; color: #888;\">Se você não solicitou essa alteração, nenhuma ação é necessária e você pode desconsiderar esta mensagem com segurança.</p>" +
                "    </div>" +

                "    <div style=\"background-color: #f1f2f6; padding: 20px; text-align: center; font-size: 12px; color: #a4b0be; border-top: 1px solid #e4e7eb;\">" +
                "      <p style=\"margin: 0;\">&copy; " + java.time.Year.now().getValue() + " Igniscore. Todos os direitos reservados.</p>" +
                "      <p style=\"margin: 5px 0 0 0;\">Por favor, não responda a este e-mail automático.</p>" +
                "    </div>" +
                "  </div>" +
                "</div>";
    }

    private String buildVerificationHtmlTemplate(String code) {
        String logoUrl = "https://front-end-tcc-lovat.vercel.app/_next/image?url=%2Figniscore.png&w=48&q=75";

        return "<div style=\"font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #f9f9f9; padding: 40px 10px; color: #333;\">" +
                "  <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); overflow: hidden;\">" +

                "    <div style=\"background-color: #6D28D9; padding: 25px; text-align: center;\">" +
                "      <img src=\"" + logoUrl + "\" alt=\"Igniscore Logo\" style=\"max-height: 50px; display: inline-block; vertical-align: middle;\" />" +
                "    </div>" +

                "    <div style=\"padding: 40px 30px; line-height: 1.6;\">" +
                "      <h2 style=\"color: #1a1a2e; margin-top: 0; font-size: 20px;\">Olá,</h2>" +
                "      <p style=\"font-size: 16px; color: #555;\">Obrigado por se cadastrar no Igniscore! Para ativar a sua conta, utilize o código de verificação abaixo:</p>" +
                "      " +

                "      <div style=\"text-align: center; margin: 35px 0;\">" +
                "        <span style=\"background-color: #f1f2f6; color: #1a1a2e; padding: 15px 35px; font-size: 24px; font-weight: bold; border-radius: 5px; letter-spacing: 6px; border: 1px dashed #6D28D9; display: inline-block;\">" + code + "</span>" +
                "      </div>" +
                "      " +
                "      <p style=\"font-size: 16px; color: #555;\">Este código é válido por apenas 15 minutos.</p>" +
                "      <p style=\"font-size: 14px; color: #888;\">Se você não realizou este cadastro, por favor desconsidere este e-mail.</p>" +
                "    </div>" +

                "    <div style=\"background-color: #f1f2f6; padding: 20px; text-align: center; font-size: 12px; color: #a4b0be; border-top: 1px solid #e4e7eb;\">" +
                "      <p style=\"margin: 0;\">&copy; " + java.time.Year.now().getValue() + " Igniscore. Todos os direitos reservados.</p>" +
                "      <p style=\"margin: 5px 0 0 0;\">Por favor, não responda a este e-mail automático.</p>" +
                "    </div>" +
                "  </div>" +
                "</div>";
    }
}
