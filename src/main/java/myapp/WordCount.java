package myapp;

import java.util.Arrays;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;

public class WordCount {
  public static void main(String[] args) {
    var inputsDir = "data/sample.txt";
    var outputsPrefix = "outputs/part";

    var options = PipelineOptionsFactory.fromArgs(args).create();
    var pipeline = Pipeline.create(options);
    pipeline
        .apply("Read lines", TextIO.read().from(inputsDir))
        .apply(
            "Find words",
            FlatMapElements.into(TypeDescriptors.strings())
                .via((String line) -> Arrays.asList(line.split("[^\\p{L}]+"))))
        .apply("Filter empty words", Filter.by((String word) -> !word.isEmpty()))
        .apply("Count words", Count.perElement())
        .apply(
            "Write results",
            MapElements.into(TypeDescriptors.strings())
                .via(
                    (KV<String, Long> wordCount) ->
                        wordCount.getKey() + ": " + wordCount.getValue()))
        .apply(TextIO.write().to(outputsPrefix));
    pipeline.run();
  }
}
