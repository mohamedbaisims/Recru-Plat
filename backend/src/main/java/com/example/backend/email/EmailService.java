package com.example.backend.email;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendMfaCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("ZimApply - Code de réinitialisation du mot de passe");
        message.setText(
                "Bonjour,\n\n" +
                        "Voici votre code de réinitialisation du mot de passe : " + code + "\n\n" +
                        "Ce code est valable pendant quelques minutes. Veuillez ne pas le partager.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe ZimApply"
        );
        mailSender.send(message);
    }

    public void sendAuthMfaCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("ZimApply - Code d'authentification");
        message.setText(
                "Bonjour,\n\n" +
                        "Voici votre code d'authentification : " + code + "\n\n" +
                        "Ce code est valable pendant quelques minutes. Veuillez ne pas le partager.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe ZimApply"
        );
        mailSender.send(message);
    }

    // 🆕 Email de refus de candidature
    public void sendRejectionEmail(String to, String candidateName, String rejectionReason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Concernant votre candidature - Décision");
        message.setText(
                "Bonjour " + candidateName + ",\n\n" +
                        "Nous regrettons de vous informer que votre candidature n'a pas été retenue cette fois-ci.\n\n" +
                        "Motif du refus: " + rejectionReason + "\n\n" +
                        "Nous vous encourageons à postuler à nouveau pour d'autres positions qui pourraient mieux correspondre à votre profil.\n\n" +
                        "Nous vous remercions de l'intérêt que vous portez à notre entreprise.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe Recrutement"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ Email de refus envoyé à " + to);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email de refus: " + e.getMessage());
        }
    }

    // 🆕 Email d'invitation à un entretien
    public void sendInterviewInvitationEmail(String to, String candidateName, String interviewDate, 
                                              String interviewTime, String interviewType, String meetingLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Invitation à un entretien - " + interviewType);
        message.setText(
                "Bonjour " + candidateName + ",\n\n" +
                        "Félicitations! Votre candidature a retenu notre attention et nous sommes heureux de vous inviter à un entretien.\n\n" +
                        "Détails de l'entretien:\n" +
                        "  📅 Date: " + interviewDate + "\n" +
                        "  ⏰ Heure: " + interviewTime + "\n" +
                        "  📋 Type: " + interviewType + "\n" +
                        (meetingLink != null && !meetingLink.isEmpty() ? "  🔗 Lien de réunion: " + meetingLink + "\n\n" : "\n") +
                        "Veuillez confirmer votre présence en répondant à cet email.\n\n" +
                        "Si vous avez des questions, n'hésitez pas à nous contacter.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe Recrutement"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ Email d'invitation à l'entretien envoyé à " + to);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email d'invitation: " + e.getMessage());
        }
    }

}
