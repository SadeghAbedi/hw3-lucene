import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class DocumentIndexer {
    IndexWriter writer;
    DirectoryReader reader;

    public DocumentIndexer() {
        Analyzer analyzer = new EnglishAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir;
        try {
            dir = FSDirectory.open(Paths.get("index"));
            writer = new IndexWriter(dir, iwc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final List<Integer> documentNumbers = new ArrayList<>();

    void addDocumentNumber(int index) {
        documentNumbers.add(index);
    }

    void perform() {
        Collections.sort(documentNumbers);

        Path path = Paths.get("lucene_ dataset.txt");

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while (!documentNumbers.isEmpty()) {
                if (line == null) {
                    break;
                } else if (line.matches(".I \\d+") && line.contains(documentNumbers.get(0).toString())) {
                    line = addDocument(reader);
                    continue;
                }
                line = reader.readLine();
            }
            writer.flush();
            writer.commit();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String addDocument(BufferedReader reader) {
        var doc = new Document();
        var type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setStored(true);
        type.setStoreTermVectors(true);

        try {
            String line = reader.readLine();
            do {
                line = reader.readLine();
                doc.add(new Field("contents", line, type));
            } while (!line.matches(".I \\d+"));
            writer.addDocument(doc);
            documentNumbers.remove(0);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void readIndex() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("index"));
        reader = DirectoryReader.open(dir);

        System.out.println(Arrays.toString(reader.directory().listAll()));
        System.out.println(reader.numDocs());


        EnglishAnalyzer analyzer = new EnglishAnalyzer();
        String querystr = "what theoretical and experimental guides do we have as to turbulent\n" +
                "couette flow behaviour .";
        try {
            Query q = new QueryParser("contents", analyzer).parse(querystr);
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            System.out.println("Found " + hits.length + " hits.");

            for(int i=0;i<hits.length;++i) {

                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println(docId + ". " + i + "\t" + d.get("contents"));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.close();
    }


}
