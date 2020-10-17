import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DocumentIndexer {
    IndexWriter writer;

    public DocumentIndexer() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get("index"));
        writer = new IndexWriter(dir, iwc);
    }

    private ArrayList documentNumbers = new ArrayList<Integer>();

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
        var field = new IntPoint("docId", (Integer) documentNumbers.get(0));
        doc.add(field);
        try {
            String line = reader.readLine();
            do {
                line = reader.readLine();
                doc.add(new TextField("contents", line, Field.Store.YES));
            } while (!line.matches(".I \\d+"));
            writer.addDocument(doc);
            documentNumbers.remove(0);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
