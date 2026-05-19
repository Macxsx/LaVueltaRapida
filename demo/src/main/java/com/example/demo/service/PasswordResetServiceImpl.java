package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.PasswordResetToken;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.frontend-url:https://hg6k75cd-4200.use2.devtunnels.ms}")
    private String frontendUrl;

    @Value("${app.mail.from:noreply@lavueltarapida.com}")
    private String mailFrom;

    @Override
    public void solicitarRecuperacion(String email) {
        Cliente cliente = clienteRepo.findByEmail(email);
        if (cliente == null) {
            throw new NoSuchElementException("No existe una cuenta registrada con ese correo.");
        }

        tokenRepo.findByEmail(email).ifPresent(tokenRepo::delete);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(UUID.randomUUID().toString());
        prt.setEmail(email);
        prt.setExpiry(LocalDateTime.now().plusHours(1));
        tokenRepo.save(prt);

        if (mailSender == null) {
            throw new IllegalStateException("El servicio de correo no está configurado. Contacta al administrador.");
        }

        String resetLink = frontendUrl + "/reset-password?token=" + prt.getToken();
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(mailFrom);
        mensaje.setTo(email);
        mensaje.setSubject("Recupera tu contraseña — La Vuelta Rápida");
        mensaje.setText(
            "Hola " + cliente.getName() + ",\n\n" +
            "Recibimos una solicitud para restablecer la contraseña de tu cuenta.\n\n" +
            "Haz clic en el siguiente enlace para crear una nueva contraseña:\n\n" +
            resetLink + "\n\n" +
            "Este enlace expira en 1 hora.\n\n" +
            "Si no solicitaste este cambio, ignora este mensaje. Tu contraseña no cambiará.\n\n" +
            "— El equipo de La Vuelta Rápida"
        );

        try {
            mailSender.send(mensaje);
        } catch (MailException e) {
            throw new IllegalStateException("No se pudo enviar el correo. Verifica la configuración SMTP.");
        }
    }

    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void resetContrasena(String token, String nuevaContrasena) {
        PasswordResetToken prt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido."));

        if (prt.getExpiry().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(prt);
            throw new IllegalArgumentException("El enlace ha expirado. Solicita uno nuevo.");
        }

        Cliente cliente = clienteRepo.findByEmail(prt.getEmail());
        if (cliente == null) {
            throw new NoSuchElementException("No existe una cuenta asociada a este enlace.");
        }

        cliente.getUsuario().setPassword(passwordEncoder.encode(nuevaContrasena));
        clienteRepo.save(cliente);
        tokenRepo.delete(prt);
    }
}
