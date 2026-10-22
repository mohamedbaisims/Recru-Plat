package com.example.backend.appForm;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CvOcrService {

    private static final String[] DIPLOMA_LIST = {
        "Doctorat", "Mastère professionnel", "Mastère recherche", "Master", "Maîtrise",
        "Licence", "BTS", "Baccalauréat"
    };

    private static final Map<String, String> NATIONALITY_MAP = new HashMap<>();
    static {
        NATIONALITY_MAP.put("tunisien",   "Tunisienne");
        NATIONALITY_MAP.put("français",   "Française");
        NATIONALITY_MAP.put("française",  "Française");
        NATIONALITY_MAP.put("algérien",   "Algérienne");
        NATIONALITY_MAP.put("marocain",   "Marocaine");
        NATIONALITY_MAP.put("canadien",   "Canadienne");
        NATIONALITY_MAP.put("belge",      "Belge");
        NATIONALITY_MAP.put("italien",    "Italienne");
        NATIONALITY_MAP.put("espagnol",   "Espagnole");
        NATIONALITY_MAP.put("allemand",   "Allemande");
        NATIONALITY_MAP.put("américain",  "Américaine");
        NATIONALITY_MAP.put("libanais",   "Libanaise");
        NATIONALITY_MAP.put("égyptien",   "Égyptienne");
    }

    public Map<String, String> extract(byte[] pdfBytes) throws IOException {
        Map<String, String> result = new HashMap<>();

        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            String lowerText = text.toLowerCase();
            String[] lines = text.split("\\r?\\n");

            // ── Email ────────────────────────────────────────────────────────────────
            extractByPattern(text, "[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z.]+", "email", result);

            // ── Phone (Tunisian +216 XX XXX XXX or local 8-digit) ────────────────────
            extractByPattern(text,
                "(\\+216[\\s.\\-]?\\d{2}[\\s.\\-]?\\d{3}[\\s.\\-]?\\d{3}|\\b[2-9]\\d{7}\\b)",
                "phoneNumber", result);

            // ── Full name ─────────────────────────────────────────────────────────────
            // 1) Label-based: "Nom :" / "Nom complet :" / "Name :"
            Matcher nameLabelMatcher = Pattern.compile(
                "(?im)^(?:nom(?: complet)?|name|prénom et nom)[\\s:]+([A-ZÀ-Ÿa-zà-ÿ][A-ZÀ-Ÿa-zà-ÿ\\s\\-']{2,40})$"
            ).matcher(text);
            if (nameLabelMatcher.find()) {
                result.put("fullName", nameLabelMatcher.group(1).trim());
            } else {
                // 2) ALL-CAPS line (many CVs put name in uppercase at top)
                boolean foundCaps = false;
                for (String line : lines) {
                    String t = line.trim();
                    if (t.length() < 4 || t.contains("@") || t.contains(":")) continue;
                    if (t.toLowerCase().startsWith("cv") || t.toLowerCase().startsWith("curriculum")) continue;
                    if (t.matches("[A-ZÀ-Ÿ][A-ZÀ-Ÿ\\s\\-']{3,40}") && t.equals(t.toUpperCase())) {
                        // Convert ALL-CAPS to Title Case
                        StringBuilder sb = new StringBuilder();
                        for (String w : t.split("\\s+")) {
                            if (!w.isEmpty()) {
                                sb.append(Character.toUpperCase(w.charAt(0)))
                                  .append(w.substring(1).toLowerCase()).append(" ");
                            }
                        }
                        result.put("fullName", sb.toString().trim());
                        foundCaps = true;
                        break;
                    }
                }
                if (!foundCaps) {
                    // 3) First line with 2-4 words made only of letters/hyphens
                    for (String line : lines) {
                        String t = line.trim();
                        if (t.length() < 4 || t.contains("@") || t.contains(":")) continue;
                        if (t.toLowerCase().startsWith("cv") || t.toLowerCase().startsWith("curriculum")) continue;
                        String[] words = t.split("\\s+");
                        if (words.length >= 2 && words.length <= 4) {
                            boolean allWords = true;
                            for (String w : words) {
                                if (!w.matches("[A-ZÀ-Ÿa-zà-ÿ\\-']+")) { allWords = false; break; }
                            }
                            if (allWords) { result.put("fullName", t); break; }
                        }
                    }
                }
            }

            // ── Birth date: dd/MM/yyyy or dd-MM-yyyy ─────────────────────────────────
            Matcher dateMatcher = Pattern.compile(
                "(0?[1-9]|[12]\\d|3[01])[\\-/](0?[1-9]|1[0-2])[\\-/](19|20)\\d{2}"
            ).matcher(text);
            if (dateMatcher.find()) {
                String raw = dateMatcher.group();
                String[] parts = raw.split("[\\-/]");
                try {
                    result.put("birthDate",
                        parts[2] + "-" +
                        String.format("%02d", Integer.parseInt(parts[1])) + "-" +
                        String.format("%02d", Integer.parseInt(parts[0])));
                } catch (Exception ignored) {}
            }

            // ── Nationality ───────────────────────────────────────────────────────────
            for (Map.Entry<String, String> entry : NATIONALITY_MAP.entrySet()) {
                if (lowerText.contains(entry.getKey())) {
                    result.put("nationality", entry.getValue());
                    break;
                }
            }

            // ── Diploma (highest level first) ─────────────────────────────────────────
            for (String diploma : DIPLOMA_LIST) {
                if (lowerText.contains(diploma.toLowerCase())) {
                    result.put("diploma", diploma);
                    break;
                }
            }

            // ── Diploma year: 4-digit year 2000-2030 ──────────────────────────────────
            Matcher yearMatcher = Pattern.compile("\\b(200[0-9]|201[0-9]|202[0-9]|2030)\\b").matcher(text);
            if (yearMatcher.find()) result.put("diplomaYear", yearMatcher.group());

            // ── Institution ───────────────────────────────────────────────────────────
            for (String line : lines) {
                String ll = line.toLowerCase();
                if (ll.contains("université") || ll.contains("ecole") || ll.contains("école") ||
                    ll.contains("institut") || ll.contains("faculty") || ll.contains("iset") ||
                    ll.contains("esprit") || ll.contains("supcom") || ll.contains("enis") ||
                    ll.contains("enit") || ll.contains("ensi") || ll.contains("isim")) {
                    result.put("institution", line.trim());
                    break;
                }
            }

            // ── Sections: technical skills, personal skills, experience ───────────────
            String techSkills = extractSection(text, lowerText,
                new String[]{"compétences techniques", "technical skills", "hard skills",
                             "compétences informatiques", "compétences"});
            if (!techSkills.isEmpty()) result.put("technicalSkills", techSkills);

            String softSkills = extractSection(text, lowerText,
                new String[]{"compétences personnelles", "soft skills", "qualités personnelles",
                             "compétences transversales", "qualités"});
            if (!softSkills.isEmpty()) result.put("personalSkills", softSkills);

            String exp = extractSection(text, lowerText,
                new String[]{"expérience professionnelle", "expériences professionnelles",
                             "professional experience", "expérience"});
            if (!exp.isEmpty()) result.put("experience", exp);

            // ── Address ───────────────────────────────────────────────────────────────
            // 1) Label-based: "Adresse :", "Address :"
            Matcher addrLabelMatcher = Pattern.compile(
                "(?im)^(?:adresse|address|domicile)[\\s:]+(.{5,80})$"
            ).matcher(text);
            if (addrLabelMatcher.find()) {
                result.put("address", addrLabelMatcher.group(1).trim());
            } else {
                // 2) Line containing street keywords
                for (String line : lines) {
                    if (line.matches(".*(?i)(rue|avenue|boulevard|cité|cite|allée|impasse|résidence|residence).*")) {
                        result.put("address", line.trim());
                        break;
                    }
                }
                // 3) Zip code + city pattern
                if (!result.containsKey("address")) {
                    Matcher zipMatcher = Pattern.compile("\\b\\d{4,5}[,\\s]+[A-ZÀ-Ÿa-zà-ÿ][A-Za-zÀ-Ÿà-ÿ\\s]{2,25}").matcher(text);
                    if (zipMatcher.find()) result.put("address", zipMatcher.group().trim());
                }
            }

            // ── Birth place ───────────────────────────────────────────────────────────
            // 1) Label on its own line: "Lieu de naissance : Tunis"
            Matcher bpLabelMatcher = Pattern.compile(
                "(?im)^(?:lieu de naissance|place of birth|lieu naissance)[\\s:]+([A-ZÀ-Ÿa-zà-ÿ][A-Za-zÀ-Ÿà-ÿ\\s\\-]{1,40})$"
            ).matcher(text);
            if (bpLabelMatcher.find()) {
                result.put("birthPlace", bpLabelMatcher.group(1).trim());
            } else {
                // 2) Inline with birth date: "Né(e) le DD/MM/YYYY à Tunis" or "Né(e) à Tunis"
                Matcher bpInlineMatcher = Pattern.compile(
                    "(?i)né[e]?(?:[\\s]+le[\\s]+[\\d/\\-]+)?[\\s]+à[\\s]+([A-ZÀ-Ÿa-zà-ÿ][A-Za-zÀ-Ÿà-ÿ\\s\\-]{1,30}?)(?:[\\n,.]|$)"
                ).matcher(text);
                if (bpInlineMatcher.find()) {
                    result.put("birthPlace", bpInlineMatcher.group(1).trim());
                } else {
                    // 3) "born in X" or "à X" right after date pattern
                    Matcher bpBornMatcher = Pattern.compile(
                        "(?i)(?:born in|born at)[\\s:]+([A-ZÀ-Ÿa-zà-ÿ][A-Za-zÀ-Ÿà-ÿ\\s\\-]{1,30}?)(?:[\\n,.]|$)"
                    ).matcher(text);
                    if (bpBornMatcher.find()) result.put("birthPlace", bpBornMatcher.group(1).trim());
                }
            }
        }

        // Remove blank entries
        result.entrySet().removeIf(e -> e.getValue() == null || e.getValue().trim().isEmpty());
        return result;
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private void extractByPattern(String text, String regex, String key, Map<String, String> result) {
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) result.put(key, m.group().trim());
    }

    private String extractSection(String text, String lowerText, String[] keywords) {
        int idx = -1;
        for (String kw : keywords) {
            idx = lowerText.indexOf(kw);
            if (idx >= 0) break;
        }
        if (idx < 0) return "";

        String[] afterLines = text.substring(idx).split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        int contentLines = 0;
        for (int i = 1; i < afterLines.length && contentLines < 6; i++) {
            String l = afterLines[i].trim();
            if (l.isEmpty()) continue;
            // Stop if we hit another section header (all-caps or ends with ':')
            if (l.matches("[A-ZÀÂÉÈÊËÎÏÔÙÛÜŸÇ\\s]{6,}") || l.endsWith(":")) break;
            sb.append(l).append("\n");
            contentLines++;
        }
        return sb.toString().trim();
    }
}
