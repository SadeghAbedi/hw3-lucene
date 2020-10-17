import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("salam" );
        try {
            String indexPath = System.getProperty("user.dir").concat("/indexes");
            System.out.println(indexPath);
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig  = new IndexWriterConfig( analyzer);
            IndexWriter writer = new IndexWriter(dir , indexWriterConfig);

<<<<<<< Updated upstream
            writer.close();
        } catch (IOException ex){
            System.out.println(ex.getStackTrace());

=======
        Arrays.stream(args).filter(s -> s.matches("\\d+-\\d+"))
                .map(s -> {
            var range = s.split("-");
            return IntStream.range(Integer.parseInt(range[0]),Integer.parseInt(range[1]));
        }).flatMap(IntStream::boxed).forEach(indexer::addDocumentNumber);
        indexer.perform();

        try {
            DocumentIndexer.readIndex();
        } catch (IOException e) {
            e.printStackTrace();
>>>>>>> Stashed changes
        }
    }
}
