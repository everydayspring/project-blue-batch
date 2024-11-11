//package com.example.projectbluebatch.dto;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//
//@Getter
//@NoArgsConstructor
//@Document(indexName = "search_v1")
//public class SearchDocument {
//
//    @Id
//    private Long performanceId;
//
//    @Field(type = FieldType.Long)
//    private Long hallId;
//
//    @Field(type = FieldType.Text)
//    private String performanceTitle;
//
//    @Field(type = FieldType.Long)
//    private Long startDate;
//
//    @Field(type = FieldType.Long)
//    private Long endDate;
//
//    @Field(type = FieldType.Long)
//    private Long price;
//
//    @Field(type = FieldType.Keyword)
//    private String category;
//
//    @Field(type = FieldType.Text)
//    private String description;
//
//    @Field(type = FieldType.Integer)
//    private int duration;
//
//    @Field(type = FieldType.Nested)
//    private List<PerformerInfo> performers;
//
//    @Field(type = FieldType.Object)
//    private Hall hall;
//
//    public SearchDocument(
//            Long performanceId,
//            Long hallId,
//            String performanceTitle,
//            LocalDateTime startDate,
//            LocalDateTime endDate,
//            Long price,
//            String category,
//            String description,
//            Integer duration,
//            Hall hall) {
//        this.performanceId = performanceId;
//        this.hallId = hallId;
//        this.performanceTitle = performanceTitle;
//        this.startDate = startDate.toEpochSecond(ZoneOffset.UTC) * 1000;
//        this.endDate = endDate.toEpochSecond(ZoneOffset.UTC) * 1000;
//        this.price = price;
//        this.category = category;
//        this.description = description;
//        this.duration = duration;
//        this.hall = hall;
//    }
//
//    @Getter
//    @NoArgsConstructor
//    public static class Hall {
//
//        @Field(type = FieldType.Text)
//        private String hallName;
//
//        @Field(type = FieldType.Text)
//        private String hallAddress;
//
//        @Field(type = FieldType.Integer)
//        private int hallSeats;
//
//        public Hall(String hallName, String hallAddress, int hallSeats) {
//            this.hallName = hallName;
//            this.hallAddress = hallAddress;
//            this.hallSeats = hallSeats;
//        }
//    }
//
//    @Getter
//    @NoArgsConstructor
//    public static class PerformerInfo {
//        @Field(type = FieldType.Long)
//        private Long performerId;
//
//        @Field(type = FieldType.Text)
//        private String performerName;
//
//        @Field(type = FieldType.Keyword)
//        private String birth;
//
//        @Field(type = FieldType.Keyword)
//        private String nation;
//
//        public PerformerInfo(Long performerId, String performerName, String birth, String nation) {
//            this.performerId = performerId;
//            this.performerName = performerName;
//            this.birth = birth;
//            this.nation = nation;
//        }
//    }
//}
