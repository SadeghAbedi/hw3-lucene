import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        DocumentIndexer indexer = null;
        try {
            indexer = new DocumentIndexer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Arrays.stream(args).filter(s -> s.matches("\\d+-\\d+"))
                .map(s -> {
            var range = s.split("-");
            return IntStream.range(Integer.parseInt(range[0]),Integer.parseInt(range[1]));
        }).flatMap(IntStream::boxed).forEach(indexer::addDocumentNumber);
        indexer.perform();
    }
}
