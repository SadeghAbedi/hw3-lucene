import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        var indexer = new DocumentIndexer();

        Arrays.stream(args).filter(s -> s.matches("\\d+-\\d+"))
                .map(s -> {
            var range = s.split("-");
            return IntStream.range(Integer.parseInt(range[0]),Integer.parseInt(range[1]));
        }).flatMap(IntStream::boxed).forEach(indexer::addDocumentNumber);
        indexer.perform();
    }
}
