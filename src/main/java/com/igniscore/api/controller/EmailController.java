package com.igniscore.api.controller;

import com.igniscore.api.service.EmailService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class EmailController {

    private final EmailService emailService;

    // Injeção via construtor (boa prática recomendada)
    public EmailController(EmailService emailService) {
        // Vincula o serviço de e-mail ao controlador da API
        this.emailService = emailService;
    }

    @MutationMapping
    public String enviarEmailTeste(
            @Argument String destinatario,
            @Argument String assunto,
            @Argument String mensagem) {
        try {

            return "E-mail enviado com sucesso para " + destinatario;
        } catch (Exception e) {
            return "Erro ao enviar e-mail: " + e.getMessage();
        }
    }
}
