package com.example.searchservice.controller;

import com.example.searchservice.document.PostDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ElasticsearchOperations elasticsearchOperations;

    @GetMapping
    public ResponseEntity<List<PostDocument>> search(@RequestParam String q) {
        String wildcard = "*" + q.toLowerCase() + "*";

        NativeQuery query = NativeQuery.builder()
                .withQuery(qb -> qb.queryString(qs -> qs
                        .query(wildcard)
                        .fields("title", "content", "tags")
                        .analyzeWildcard(true)
                ))
                .build();
 
        List<PostDocument> results = elasticsearchOperations
                .search(query, PostDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();

        return ResponseEntity.ok(results);
    }
}
