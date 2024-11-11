//package com.example.projectbluebatch.batch;
//
//import com.example.projectbluebatch.dto.SearchDocument;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ElasticsearchReader implements ItemReader<SearchDocument> {
//
//    private final JdbcTemplate jdbcTemplate;
//    private int nextIndex;
//    private List<SearchDocument> documents;
//
//    public ElasticsearchReader(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//        this.nextIndex = 0;
//        loadDocuments();
//    }
//
//    private void loadDocuments() {
//        String sql = "SELECT p.id AS performanceId, h.id AS hallId, p.title AS performanceTitle, " +
//                "p.start_date AS startDate, p.end_date AS endDate, p.price AS price, " +
//                "p.category AS category, p.description AS description, p.duration AS duration, " +
//                "h.name AS hallName, h.address AS hallAddress, h.seats AS hallSeats " +
//                "FROM performance p LEFT JOIN hall h ON p.hall_id = h.id";
//
//        this.documents = jdbcTemplate.query(sql, new SearchDocumentRowMapper());
//    }
//
//    @Override
//    public SearchDocument read() {
//        if (nextIndex < documents.size()) {
//            return documents.get(nextIndex++);
//        } else {
//            return null;
//        }
//    }
//
//    private static class SearchDocumentRowMapper implements RowMapper<SearchDocument> {
//        @Override
//        public SearchDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
//            return new SearchDocument(
//                    rs.getLong("performanceId"),
//                    rs.getLong("hallId"),
//                    rs.getString("performanceTitle"),
//                    rs.getTimestamp("startDate").toLocalDateTime(),
//                    rs.getTimestamp("endDate").toLocalDateTime(),
//                    rs.getLong("price"),
//                    rs.getString("category"),
//                    rs.getString("description"),
//                    rs.getInt("duration"),
//                    new SearchDocument.Hall(
//                            rs.getString("hallName"),
//                            rs.getString("hallAddress"),
//                            rs.getInt("hallSeats")
//                    )
//            );
//        }
//    }
//}