import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
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

        Terms terms = MultiTerms.getTerms(reader, "contents");
        BytesRef byteRef;

        if ( terms.size() > 0) {
            // access the terms for this field
            TermsEnum termsEnum = terms.iterator();
            BytesRef term = null;

            // explore the terms for this field
            while ((term = termsEnum.next()) != null) {
                // enumerate through documents, in this case only one
                PostingsEnum docsEnum = termsEnum.postings(null);
                int docIdEnum;

                System.out.print("\n\"" + term.utf8ToString() + "\" : ");
                while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                    // get the term frequency in the document

                    System.out.print("doc" + docIdEnum + "(" + docsEnum.freq() + "),");
                }
            }

        }



        reader.close();
    }


}
