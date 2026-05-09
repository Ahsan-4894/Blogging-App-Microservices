package com.example.searchservice.repository;

import com.example.searchservice.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, String> {
}
