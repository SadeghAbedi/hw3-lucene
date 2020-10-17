import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

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

    public static void readIndex() throws IOException {


        Directory dir = FSDirectory.open(Paths.get("index"));
        DirectoryReader reader = DirectoryReader.open(dir);

        System.out.println(Arrays.toString(reader.directory().listAll()));
        System.out.println(reader.numDocs());

        Terms terms = MultiTerms.getTerms(reader, "contents");
        var it = terms.iterator();
        BytesRef byteRef = null;
        while ((byteRef = it.next()) != null) {
            System.out.println("\"" + byteRef.utf8ToString() + "\"" + " : TotalFreq : " + it.totalTermFreq());

            for (int i = 0; i < reader.numDocs(); i++) {
                Terms termVector = reader.getTermVector(i, "contents");
                TermsEnum itr = termVector.iterator();
                BytesRef term = null;
                while ((term = itr.next()) != null) {
                    try {
                        String termText = term.utf8ToString();
                        if (termText.equals(byteRef.utf8ToString())) {
                            System.out.print("doc" + i + "(" + itr.totalTermFreq() + "),");
                        }


                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

            System.out.println("");


        }
        reader.close();
    }

    private String addDocument(BufferedReader reader) {
        var doc = new Document();
        FieldType type = new FieldType();
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

}
