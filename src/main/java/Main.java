import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;


public class Main {
    public static void main(String[] args) {
        final DocumentIndexer indexer =new DocumentIndexer();


        Arrays.stream(args).filter(s -> s.matches("\\d+-\\d+"))
                .map(s -> {
                    var range = s.split("-");
                    return IntStream.range(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                }).flatMap(IntStream::boxed).forEach(indexer::addDocumentNumber);

        Arrays.stream(args).filter(s -> s.matches("\\d[,\\d]*"))
                .forEach(s -> {
                    var numbers = s.split(",");
                    for (var number : numbers)
                        indexer.addDocumentNumber(Integer.parseInt(number));
                });


        indexer.perform();


        try {
            indexer.readIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
