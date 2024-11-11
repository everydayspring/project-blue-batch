//package com.example.projectbluebatch.batch;
//
//import com.example.projectbluebatch.dto.SearchDocument;
//import com.example.projectbluebatch.repository.ESRepository;
//import org.springframework.batch.item.Chunk;
//import org.springframework.batch.item.ItemWriter;
//
//public class ElasticsearchWriter implements ItemWriter<SearchDocument> {
//
//    private final ESRepository elasticsearchRepository;
//
//    public ElasticsearchWriter(ESRepository elasticsearchRepository) {
//        this.elasticsearchRepository = elasticsearchRepository;
//    }
//
//    @Override
//    public void write(Chunk<? extends SearchDocument> documents) {
//        elasticsearchRepository.deleteAll(); // Elasticsearch 인덱스 초기화
//        elasticsearchRepository.saveAll(documents.getItems()); // DB 상태와 동일하게 동기화
//    }
//}