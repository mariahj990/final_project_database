/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenreService {
    
    private final DataSource dataSource;
    private final Map<String, String> genreToBucket;

    @Autowired
    public GenreService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.genreToBucket = createGenreMapping();
    }

    /**
     * Create the genre bucket mapping
     */
    private Map<String, String> createGenreMapping() {
        Map<String, String> mapping = new HashMap<>();
        
        // Adult Fiction
        mapping.put("fiction", "Adult Fiction");
        mapping.put("adult fiction", "Adult Fiction");
        mapping.put("contemporary", "Adult Fiction");
        mapping.put("historical fiction", "Adult Fiction");
        
        // Classics & Literature
        mapping.put("classics", "Classics & Literature");
        mapping.put("literature", "Classics & Literature");
        mapping.put("novels", "Classics & Literature");
        mapping.put("literary fiction", "Classics & Literature");
        mapping.put("american", "Classics & Literature");
        mapping.put("british literature", "Classics & Literature");
        
        // Young Adult (YA)
        mapping.put("young adult", "Young Adult (YA)");
        mapping.put("young adult contemporary", "Young Adult (YA)");
        mapping.put("coming of age", "Young Adult (YA)");
        mapping.put("high school", "Young Adult (YA)");
        mapping.put("teen", "Young Adult (YA)");
        mapping.put("ya", "Young Adult (YA)");
        
        // Children / Middle Grade
        mapping.put("middle grade", "Children / Middle Grade");
        mapping.put("childrens", "Children / Middle Grade");
        mapping.put("kids", "Children / Middle Grade");
        mapping.put("chapter books", "Children / Middle Grade");
        mapping.put("picture books", "Children / Middle Grade");
        
        // Fantasy & Sci-Fi
        mapping.put("fantasy", "Fantasy & Sci-Fi");
        mapping.put("high fantasy", "Fantasy & Sci-Fi");
        mapping.put("epic fantasy", "Fantasy & Sci-Fi");
        mapping.put("urban fantasy", "Fantasy & Sci-Fi");
        mapping.put("science fiction", "Fantasy & Sci-Fi");
        mapping.put("science fiction fantasy", "Fantasy & Sci-Fi");
        mapping.put("sci-fi", "Fantasy & Sci-Fi");
        mapping.put("scifi", "Fantasy & Sci-Fi");
        mapping.put("space", "Fantasy & Sci-Fi");
        mapping.put("space opera", "Fantasy & Sci-Fi");
        mapping.put("magic", "Fantasy & Sci-Fi");
        mapping.put("supernatural", "Fantasy & Sci-Fi");
        mapping.put("speculative fiction", "Fantasy & Sci-Fi");
        mapping.put("time travel", "Fantasy & Sci-Fi");
        mapping.put("dystopia", "Fantasy & Sci-Fi");
        mapping.put("witches", "Fantasy & Sci-Fi");
        mapping.put("vampires", "Fantasy & Sci-Fi");
        
        // Mystery / Thriller / Crime
        mapping.put("mystery", "Mystery / Thriller / Crime");
        mapping.put("crime", "Mystery / Thriller / Crime");
        mapping.put("mystery thriller", "Mystery / Thriller / Crime");
        mapping.put("legal thriller", "Mystery / Thriller / Crime");
        mapping.put("thriller", "Mystery / Thriller / Crime");
        mapping.put("murder mystery", "Mystery / Thriller / Crime");
        mapping.put("detective", "Mystery / Thriller / Crime");
        mapping.put("espionage", "Mystery / Thriller / Crime");
        mapping.put("suspense", "Mystery / Thriller / Crime");
        
        // Romance & Humor
        mapping.put("romance", "Romance & Humor");
        mapping.put("chick lit", "Romance & Humor");
        mapping.put("love", "Romance & Humor");
        mapping.put("love story", "Romance & Humor");
        mapping.put("humor", "Romance & Humor");
        mapping.put("comedy", "Romance & Humor");
        mapping.put("adult romance", "Romance & Humor");
        mapping.put("contemporary romance", "Romance & Humor");
        
        // Nonfiction / Biography / History
        mapping.put("historical", "Nonfiction / Biography / History");
        mapping.put("history", "Nonfiction / Biography / History");
        mapping.put("military history", "Nonfiction / Biography / History");
        mapping.put("world history", "Nonfiction / Biography / History");
        mapping.put("19th century", "Nonfiction / Biography / History");
        mapping.put("20th century", "Nonfiction / Biography / History");
        mapping.put("biography", "Nonfiction / Biography / History");
        mapping.put("memoir", "Nonfiction / Biography / History");
        mapping.put("autobiography", "Nonfiction / Biography / History");
        mapping.put("essays", "Nonfiction / Biography / History");
        mapping.put("nonfiction", "Nonfiction / Biography / History");
        mapping.put("philosophy", "Nonfiction / Biography / History");
        mapping.put("science", "Nonfiction / Biography / History");
        mapping.put("popular science", "Nonfiction / Biography / History");
        mapping.put("psychology", "Nonfiction / Biography / History");
        mapping.put("sociology", "Nonfiction / Biography / History");
        mapping.put("economics", "Nonfiction / Biography / History");
        
        // Education / School
        mapping.put("school", "Education / School");
        mapping.put("read for school", "Education / School");
        mapping.put("teaching", "Education / School");
        mapping.put("education", "Education / School");
        
        // Religion / Spirituality
        mapping.put("religion", "Religion / Spirituality");
        mapping.put("christian", "Religion / Spirituality");
        mapping.put("christian fiction", "Religion / Spirituality");
        mapping.put("christian romance", "Religion / Spirituality");
        mapping.put("atheism", "Religion / Spirituality");
        mapping.put("faith", "Religion / Spirituality");
        mapping.put("spirituality", "Religion / Spirituality");
        mapping.put("jewish", "Religion / Spirituality");
        
        // Food & Cooking
        mapping.put("cooking", "Food & Cooking");
        mapping.put("cookbooks", "Food & Cooking");
        mapping.put("food", "Food & Cooking");
        
        // Health / Medicine
        mapping.put("health", "Health / Medicine");
        mapping.put("health care", "Health / Medicine");
        mapping.put("medicine", "Health / Medicine");
        
        // Travel / Geography / Nature
        mapping.put("travel", "Travel / Geography / Nature");
        mapping.put("geography", "Travel / Geography / Nature");
        mapping.put("environment", "Travel / Geography / Nature");
        mapping.put("nature", "Travel / Geography / Nature");
        mapping.put("outdoors", "Travel / Geography / Nature");
        mapping.put("conservation", "Travel / Geography / Nature");
        
        // Business / Finance
        mapping.put("finance", "Business / Finance");
        mapping.put("money", "Business / Finance");
        mapping.put("personal finance", "Business / Finance");
        mapping.put("business", "Business / Finance");
        
        // Self Help / Personal Development
        mapping.put("self help", "Self Help / Personal Development");
        mapping.put("personal development", "Self Help / Personal Development");
        
        // Arts / Music / Theatre
        mapping.put("art", "Arts / Music / Theatre");
        mapping.put("music", "Arts / Music / Theatre");
        mapping.put("theatre", "Arts / Music / Theatre");
        mapping.put("plays", "Arts / Music / Theatre");
        
        // International / Regional Literature
        mapping.put("japanese literature", "International / Regional Literature");
        mapping.put("asian literature", "International / Regional Literature");
        mapping.put("french literature", "International / Regional Literature");
        mapping.put("spanish literature", "International / Regional Literature");
        mapping.put("irish literature", "International / Regional Literature");
        mapping.put("indian literature", "International / Regional Literature");
        mapping.put("america", "International / Regional Literature");
        mapping.put("europe", "International / Regional Literature");
        mapping.put("africa", "International / Regional Literature");
        
        // Other / Rare
        mapping.put("banned books", "Other / Rare");
        mapping.put("unfinished", "Other / Rare");
        mapping.put("adoption", "Other / Rare");
        mapping.put("journalism", "Other / Rare");
        mapping.put("sexuality", "Other / Rare");
        mapping.put("military fiction", "Other / Rare");
        
        return mapping;
    }

    /**
     * Map a genre to its bucket category.
     * Returns the bucket name, or "Other / Rare" if not found
     */
    public String mapGenreToBucket(String genre) {
        String cleanedGenre = genre.trim().toLowerCase();
        return genreToBucket.getOrDefault(cleanedGenre, "Other / Rare");
    }

    /**
     * Get or create a genre bucket by name.
     * Returns the genre_id.
     */
    public int getGenre(String bucketName) throws SQLException {
        // Get existing genre
        final String selectSql = "select genre_id from genre where genre_name = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, bucketName);
            
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("genre_id");
                }
            }
        }
        
        throw new SQLException("Failed to get genre: " + bucketName);
    }

    /**
     * Update user's genre count when they finish reading a book.
     * Increments count if exists, inserts new row if not.
     */
    public void incrementUserGenreCount(String userId, int genreId) throws SQLException {
        final String upsertSql = """
            insert into user_genre_count (user_id, genre_id, count)
            values (?, ?, 1)
            on duplicate key update count = count + 1
        """;
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(upsertSql)) {
            stmt.setString(1, userId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        }
    }

    /**
     * Parse genres from book's genre string and map to buckets
     * Handles formats like: ['fantasy', 'young-adult'] or [fantasy, ya]
     * Returns list of genre bucket names.
     */
    public List<String> parseAndMapGenres(String genresString) {
        List<String> buckets = new ArrayList<>();
        
        if (genresString == null || genresString.trim().isEmpty()) {
            return buckets;
        }
        
        // Remove brackets and quotes
        String cleaned = genresString
            .replace("[", "")
            .replace("]", "")
            .replace("'", "")
            .replace("\"", "");
        
        // Split by comma
        String[] genreArray = cleaned.split(",");
        
        for (String genre : genreArray) {
            String trimmed = genre.trim();
            if (!trimmed.isEmpty()) {
                String bucket = mapGenreToBucket(trimmed);
                if (!buckets.contains(bucket)) {  // Avoid duplicates
                    buckets.add(bucket);
                }
            }
        }
        
        return buckets;
    }
}