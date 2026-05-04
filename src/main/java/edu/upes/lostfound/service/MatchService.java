package edu.upes.lostfound.service;

import edu.upes.lostfound.dao.ItemReportDAO;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.MatchResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MatchService {
    private static final double MINIMUM_SCORE = 0.35;
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "and", "or", "of", "for", "with", "in", "on", "near",
            "my", "i", "lost", "found", "item", "at", "from"
    );

    private final ItemReportDAO itemReportDAO;
    private final Map<String, String> locationGroups = new HashMap<>();

    public MatchService(ItemReportDAO itemReportDAO) {
        this.itemReportDAO = itemReportDAO;
        seedLocationGroups();
    }

    public List<MatchResult> findMatchesFor(ItemReport source) throws SQLException {
        List<MatchResult> results = new ArrayList<>();
        for (ItemReport candidate : itemReportDAO.findCounterpartCandidates(source)) {
            double categoryScore = source.getCategory().equalsIgnoreCase(candidate.getCategory()) ? 1.0 : 0.0;
            double locationScore = locationScore(source.getLocation(), candidate.getLocation());
            double keywordScore = keywordSimilarity(source, candidate);
            double finalScore = (categoryScore * 0.35) + (locationScore * 0.25) + (keywordScore * 0.40);

            if (finalScore >= MINIMUM_SCORE) {
                String reason = "Category " + percentage(categoryScore) +
                        ", Location " + percentage(locationScore) +
                        ", Keywords " + percentage(keywordScore);
                results.add(new MatchResult(candidate, finalScore, reason));
            }
        }
        results.sort(Comparator.comparingDouble(MatchResult::getScore).reversed());
        return results.stream().limit(5).toList();
    }

    private double keywordSimilarity(ItemReport left, ItemReport right) {
        Set<String> a = tokens(left.getItemName() + " " + left.getDescription());
        Set<String> b = tokens(right.getItemName() + " " + right.getDescription());
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return (double) intersection.size() / union.size();
    }

    private Set<String> tokens(String text) {
        Set<String> tokens = new HashSet<>();
        String cleaned = text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ");
        for (String token : cleaned.split("\\s+")) {
            if (token.length() > 2 && !STOP_WORDS.contains(token)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private double locationScore(String left, String right) {
        if (left == null || right == null) {
            return 0.0;
        }
        if (left.equalsIgnoreCase(right)) {
            return 1.0;
        }
        String leftGroup = locationGroups.get(left.toLowerCase(Locale.ROOT));
        String rightGroup = locationGroups.get(right.toLowerCase(Locale.ROOT));
        if (leftGroup != null && leftGroup.equals(rightGroup)) {
            return 0.65;
        }
        return 0.15;
    }

    private String percentage(double value) {
        return Math.round(value * 100) + "%";
    }

    private void seedLocationGroups() {
        locationGroups.put("library", "academic");
        locationGroups.put("classroom", "academic");
        locationGroups.put("computer lab", "academic");
        locationGroups.put("auditorium", "academic");
        locationGroups.put("hostel", "residential");
        locationGroups.put("cafeteria", "student-life");
        locationGroups.put("sports ground", "student-life");
        locationGroups.put("parking area", "campus-services");
        locationGroups.put("admin block", "campus-services");
        locationGroups.put("medical center", "campus-services");
    }
}
