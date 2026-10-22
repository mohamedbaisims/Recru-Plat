package com.example.backend.ai;

import com.example.backend.workdomain.WorkDomain;
import com.example.backend.workdomain.WorkDomainRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {
    private static final Set<String> STOP_WORDS = Set.of(
            "de", "des", "du", "la", "le", "les", "et", "ou", "en", "un", "une",
            "pour", "avec", "sur", "dans", "par", "au", "aux", "the", "a", "an"
    );

    private final WorkDomainRepository workDomainRepository;

    public AiService(WorkDomainRepository workDomainRepository) {
        this.workDomainRepository = workDomainRepository;
    }

    public Map<String, Object> analyser(String cvText) {
        List<WorkDomain> domaines = workDomainRepository.findAll();
        Set<String> cvTokens = tokenize(cvText);
        String normalizedCvText = normalizeText(cvText);

        WorkDomain bestDomain = null;
        double bestScore = 0;
        List<String> bestMatchedKeywords = List.of();

        for (WorkDomain domaine : domaines) {
            DomainAnalysisResult analysis = analyseDomain(cvTokens, normalizedCvText, domaine);

            if (analysis.score() > bestScore) {
                bestScore = analysis.score();
                bestDomain = domaine;
                bestMatchedKeywords = analysis.matchedKeywords();
            }
        }

        Map<String, Object> result = new HashMap<>();
        boolean hasSuggestion = bestDomain != null && bestScore > 0;

        result.put("domaine", hasSuggestion ? bestDomain.getName() : "Aucun domaine suggere");
        result.put("domaineSuggere", hasSuggestion ? bestDomain.getName() : null);
        result.put("domaineId", hasSuggestion ? bestDomain.getId() : null);
        result.put("score", Math.round(bestScore * 10.0) / 10.0);
        result.put("matchedKeywords", bestMatchedKeywords);
        result.put("confidence", getConfidenceLabel(bestScore));

        return result;
    }

    private DomainAnalysisResult analyseDomain(Set<String> cvTokens, String normalizedCvText, WorkDomain domaine) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        candidates.addAll(splitTerms(domaine.getName()));
        candidates.addAll(splitTerms(domaine.getKeywords()));

        if (candidates.isEmpty()) {
            return new DomainAnalysisResult(0, List.of());
        }

        double score = 0;
        List<String> matchedKeywords = new ArrayList<>();

        for (String term : candidates) {
            if (term.isBlank()) {
                continue;
            }

            String normalizedTerm = normalizeText(term);
            if (normalizedTerm.isBlank()) {
                continue;
            }

            if (normalizedTerm.contains(" ")) {
                if (normalizedCvText.contains(normalizedTerm)) {
                    score += 2.5;
                    matchedKeywords.add(term.trim());
                }
                continue;
            }

            if (cvTokens.contains(normalizedTerm)) {
                score += 1.0;
                matchedKeywords.add(term.trim());
            }
        }

        String normalizedDomainName = normalizeText(domaine.getName());
        if (!normalizedDomainName.isBlank() && normalizedCvText.contains(normalizedDomainName)) {
            score += 3.0;
            if (!matchedKeywords.contains(domaine.getName())) {
                matchedKeywords.add(0, domaine.getName());
            }
        }

        double normalizedScore = Math.min(100.0, (score / Math.max(1, candidates.size())) * 100.0);
        return new DomainAnalysisResult(normalizedScore, matchedKeywords.stream().distinct().limit(5).collect(Collectors.toList()));
    }

    private List<String> splitTerms(String rawTerms) {
        if (rawTerms == null || rawTerms.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rawTerms.split("[,;\\n\\r]+"))
                .map(String::trim)
                .filter(term -> !term.isBlank())
                .toList();
    }

    private Set<String> tokenize(String text) {
        String normalized = normalizeText(text);
        if (normalized.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(normalized.split("[^a-z0-9+#.]+"))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .filter(token -> token.length() > 1)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        return normalized.replaceAll("[^a-z0-9+#.\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String getConfidenceLabel(double score) {
        if (score >= 70) {
            return "Elevee";
        }
        if (score >= 40) {
            return "Moyenne";
        }
        if (score > 0) {
            return "Faible";
        }
        return "Aucune";
    }

    private record DomainAnalysisResult(double score, List<String> matchedKeywords) {
    }
}